package eu.europa.ec.fisheries.uvms.sales.domain.bean;


import com.google.common.base.Optional;
import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import eu.europa.ec.fisheries.schema.sales.ReportSummary;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.comparator.CompareReportOnCreationDateDescending;
import eu.europa.ec.fisheries.uvms.sales.domain.comparator.CompareReportSummaryOnCreationDescending;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.CreateReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.ListUtils;

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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Stateless
@Slf4j
public class ReportDomainModelBean implements ReportDomainModel {

    @EJB
    private FluxReportDao fluxReportDao;

    @EJB
    private CreateReportHelper createReportHelper;

    @Inject @FLUX
    private MapperFacade mapper;

    @EJB
    private ReportHelper reportHelper;

    @Override
    public Optional<Report> findByExtId(String extId) {
        return findByExtId(extId, false);
    }

    @Override
    public Optional<Report> findByExtId(String extId, boolean includeDeletedOrCorrectedReports) {
        log.debug("Find report by extId {}", extId);

        Optional<FluxReport> fluxReport = fluxReportDao.findByExtId(extId);
        if (!fluxReport.isPresent()
                || (!includeDeletedOrCorrectedReports && (fluxReport.get().isCorrected() || fluxReport.get().isDeleted()))) {
            return Optional.absent();
        } else {
            return Optional.of(mapper.map(fluxReport.get(), Report.class));
        }
    }

    @Override
    public Report create(@NonNull Report report) {
        return createReportHelper.create(report);
    }

    @Override
    public Report findSalesDetails(String extId) {
        log.debug("Find sales details of flux report with extId {}", extId);
        checkArgument(!Strings.isNullOrEmpty(extId), "extId cannot be null or blank");

        FluxReport fluxReportWithDetails = fluxReportDao.findDetailsByExtId(extId);

        return mapper.map(fluxReportWithDetails, Report.class);
    }

    @Override
    public List<ReportSummary> search(ReportQuery fluxReportQuery, boolean eagerLoadRelations) {
        checkNotNull(fluxReportQuery);
        log.debug("Searching reports on query {}", fluxReportQuery.toString());
        List<FluxReport> fluxReports = fluxReportDao.search(fluxReportQuery, eagerLoadRelations);
        return mapper.mapAsList(fluxReports, ReportSummary.class);
    }

    @Override
    public List<Report> searchIncludingDetails(ReportQuery fluxReportQuery) {
        checkNotNull(fluxReportQuery);
        log.debug("Searching reports on query {}", fluxReportQuery.toString());
        List<FluxReport> fluxReports = fluxReportDao.search(fluxReportQuery);
        return mapper.mapAsList(fluxReports, Report.class);
    }

    @Override
    public long count(ReportQuery fluxReportQuery) {
        checkNotNull(fluxReportQuery);
        log.debug("Counting reports on query {}", fluxReportQuery.toString());
        return fluxReportDao.count(fluxReportQuery);
    }

    @Override
    public Optional<Report> findCorrectionOf(@NotNull String extId) {
        log.debug("Finding report which refers to a report with extId {}", extId);
        Optional<FluxReport> referral = fluxReportDao.findCorrectionOf(extId);

        if (referral.isPresent()) {
            return Optional.of(mapper.map(referral.get(), Report.class));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public List<ReportSummary> findOlderVersionsOrderedByCreationDateDescending(@Nullable String firstReferencedId) {
        List<ReportSummary> referencedReports = new ArrayList<>();

        if (isNotBlank(firstReferencedId)) {
            referencedReports.addAll(findOlderVersions(firstReferencedId, ReportSummary.class));
            Collections.sort(referencedReports, new CompareReportSummaryOnCreationDescending());
        }

        return referencedReports;
    }

    @Override
    public List<Report> findOlderVersionsOrderedByCreationDateDescendingIncludingDetails(@Nullable Report report) {
        String firstReferencedId = reportHelper.getFLUXReportDocumentReferencedIdOrNull(report);

        if (isBlank(firstReferencedId)) {
            return new ArrayList<Report>();
        }

        List<Report> olderVersions = findOlderVersions(firstReferencedId, Report.class);

        Collections.sort(olderVersions, new CompareReportOnCreationDateDescending(reportHelper));

        return olderVersions;
    }

    private <T> List<T> findOlderVersions(String firstReferencedId, Class<T> expectedResult) {
        List<T> referencedReports = new ArrayList<>();
        Optional<FluxReport> referencedReport = fluxReportDao.findByExtId(firstReferencedId);

        if (referencedReport.isPresent()) {
            referencedReports.add(mapper.map(referencedReport.get(), expectedResult));

            List<FluxReport> olderVersions = fluxReportDao.findOlderVersions(referencedReport.get());
            referencedReports.addAll(mapper.mapAsList(olderVersions, expectedResult));
        }

        return referencedReports;
    }


    @Override
    public boolean isLatestVersion(Report report) {
        String extId = reportHelper.getFLUXReportDocumentId(report);
        return !fluxReportDao.findCorrectionOf(extId)
                .isPresent();
    }

    @Override
    public Report findLatestVersion(Report report) {
        String extId = reportHelper.getFLUXReportDocumentId(report);
        Optional<FluxReport> newerVersion = fluxReportDao.findCorrectionOf(extId);
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
