package eu.europa.ec.fisheries.uvms.sales.domain.bean;


import com.google.common.base.Optional;
import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.UnsavedMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.comparator.CompareReportOnCreationDateDescending;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Stateless
public class ReportDomainModelBean implements ReportDomainModel {

    private static final Logger LOG = LoggerFactory.getLogger(ReportDomainModelBean.class);

    @EJB
    private FluxReportDao fluxReportDao;

    @Inject @FLUX
    private MapperFacade mapper;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private UnsavedMessageDomainModel unsavedMessageDomainModel;

    @Override
    public Optional<Report> findByExtId(String extId) {
        LOG.debug("Find report by extId {}", extId);

        Optional<FluxReport> fluxReport = fluxReportDao.findByExtId(extId);
        if (fluxReport.isPresent()) {
            return Optional.of(mapper.map(fluxReport.get(), Report.class));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public Report create(Report report) {
        checkNotNull(report);
        LOG.debug("Persisting report {}", report.toString());

        FluxReport fluxReport;

        if (reportHelper.isReportDeleted(report)) {
            fluxReport = updateDeletionDateOfReportReferencedBy(report);

            saveExtId(report);
        } else {
            fluxReport = mapper.map(report, FluxReport.class);

            if (reportHelper.isReportCorrected(report)) {
                enrichWithPreviousReport(fluxReport, report);
            }

            if (reportHelper.hasReferencesToTakeOverDocuments(report)) {
                enrichWithRelatedTakeOverDocuments(fluxReport, report);
            }

            fluxReport = fluxReportDao.create(fluxReport);
        }

        checkNotNull(fluxReport, "Variable fluxReport should not be nullable at this point");

        return mapper.map(fluxReport, Report.class);

    }

    private void saveExtId(Report report) {
        String extId = reportHelper.getFLUXReportDocumentId(report);

        if (!unsavedMessageDomainModel.exists(extId)) {
            unsavedMessageDomainModel.save(extId);
        }
    }

    private FluxReport updateDeletionDateOfReportReferencedBy(Report report) {
        DateTime deletionDate = reportHelper.getCreationDate(report);
        String originalReportExtId = reportHelper.getFLUXReportDocumentReferencedId(report);

        FluxReport originalReport = fluxReportDao.findByExtId(originalReportExtId).get();

        // If a report was already deleted, we want to keep the original deletion date and not the date of the 'new' deletion.
        if (originalReport.getDeletion() == null) {
            originalReport.setDeletion(deletionDate);
        }

        return originalReport;
    }

    private void enrichWithRelatedTakeOverDocuments(FluxReport fluxReportEntity, Report report) {
        List<String> takeOverDocumentIds = reportHelper.getReferenceIdsToTakeOverDocuments(report);
        List<FluxReport> takeOverDocuments = new ArrayList<>();

        for (String takeOverDocumentId : takeOverDocumentIds) {
            FluxReport takeOverDocument = fluxReportDao.findByExtId(takeOverDocumentId).get();
            if (takeOverDocument != null) {
                takeOverDocuments.add(takeOverDocument);
            }
        }

        fluxReportEntity.setRelatedTakeOverDocuments(takeOverDocuments);
    }

    private void enrichWithPreviousReport(FluxReport fluxReportEntity, Report report) {
        String referencedId = reportHelper.getFLUXReportDocumentReferencedId(report);
        Optional<FluxReport> previousReport = fluxReportDao.findByExtId(referencedId);
        if (previousReport.isPresent()) {
            fluxReportEntity.setPreviousFluxReport(previousReport.get());
        }
    }

    @Override
    public Report findSalesDetails(String extId) {
        LOG.debug("Find sales details of flux report with extId {}", extId);
        checkArgument(!Strings.isNullOrEmpty(extId), "extId cannot be null or blank");

        FluxReport fluxReportWithDetails = fluxReportDao.findDetailsByExtId(extId);

        return mapper.map(fluxReportWithDetails, Report.class);
    }

    @Override
    public List<Report> search(ReportQuery fluxReportQuery) {
        checkNotNull(fluxReportQuery);
        LOG.debug("Searching reports on query {}", fluxReportQuery.toString());
        List<FluxReport> fluxReports = fluxReportDao.search(fluxReportQuery);
        return mapper.mapAsList(fluxReports, Report.class);
    }

    @Override
    public long count(ReportQuery fluxReportQuery) {
        checkNotNull(fluxReportQuery);
        LOG.debug("Counting reports on query {}", fluxReportQuery.toString());
        return fluxReportDao.count(fluxReportQuery);
    }

    @Override
    public Optional<Report> findCorrectionOrDeletionOf(@NotNull String extId) {
        LOG.debug("Finding report which refers to a report with extId {}", extId);
        Optional<FluxReport> referral = fluxReportDao.findCorrectionOrDeletionOf(extId);

        if (referral.isPresent()) {
            return Optional.of(mapper.map(referral.get(), Report.class));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public List<Report> findOlderVersionsOrderedByCreationDateDescending(@Nullable String firstReferencedId) {
        List<Report> referencedReports = new ArrayList<>();

        if (StringUtils.isNotBlank(firstReferencedId)) {
            Report referencedReport = findByExtId(firstReferencedId).get();
            referencedReports.add(referencedReport);

            String nextReferencedId = reportHelper.getFLUXReportDocumentReferencedIdOrNull(referencedReport);
            referencedReports.addAll(findOlderVersionsOrderedByCreationDateDescending(nextReferencedId));
        }

        Collections.sort(referencedReports, new CompareReportOnCreationDateDescending(reportHelper));

        return referencedReports;
    }

    @Override
    public List<Report> findOlderVersionsOrderedByCreationDateDescending(@Nullable Report report) {
        String firstReferencedId = reportHelper.getFLUXReportDocumentReferencedIdOrNull(report);
        return findOlderVersionsOrderedByCreationDateDescending(firstReferencedId);
    }

    @Override
    public boolean isLatestVersion(Report report) {
        String extId = reportHelper.getFLUXReportDocumentId(report);
        return !fluxReportDao.findCorrectionOrDeletionOf(extId)
                             .isPresent();
    }

    @Override
    public Report findLatestVersion(Report report) {
        String extId = reportHelper.getFLUXReportDocumentId(report);
        Optional<FluxReport> newerVersion = fluxReportDao.findCorrectionOrDeletionOf(extId);
        if (newerVersion.isPresent()) {
            FluxReport latestVersion = fluxReportDao.findLatestVersion(newerVersion.get());
            return mapper.map(latestVersion, Report.class);
        } else {
            return report;
        }
    }

    @Override
    public List<Report> findRelatedReportsOf(Report report) {
        String extId = reportHelper.getFLUXReportDocumentId(report);
        FluxReport fluxReport = fluxReportDao.findByExtId(extId).get();
        List<FluxReport> relatedReports = ListUtils.union(fluxReport.getRelatedSalesNotes(), fluxReport.getRelatedTakeOverDocuments());
        return mapper.mapAsList(relatedReports, Report.class);
    }

}
