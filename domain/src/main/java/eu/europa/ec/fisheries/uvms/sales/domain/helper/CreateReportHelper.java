package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Creating a report is highly dependent on which reports are already in the database.
 * Since the order of delivery of the reports is not guaranteed, it's possible that a correction
 * or deletion of reports enters the system before the original report.
 * In order to keep the data correct, it is important that only one report is created at a time.
 * To realize this goal, we use a singleton helper with a write lock, so that the create() method
 * can only be accessed by one process at a time.
 */
@Singleton
@Slf4j
public class CreateReportHelper {

    @Inject @FLUX
    private MapperFacade mapper;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private FluxReportDao fluxReportDao;

    @Lock(LockType.WRITE)
    public Report create(@NonNull Report report) {
        log.debug("Persisting report {}", report.toString());

        FluxReport fluxReport = mapper.map(report, FluxReport.class);

        // to simplify search, each report entity has calculated fields that keeps whether is has been deleted or
        // corrected. Keep these fields up to date.
        if (reportHelper.isReportDeleted(report)) {
            markPreviousReportAsDeleted(report);
        } else if (reportHelper.isReportCorrected(report)) {
            markPreviousReportAsCorrected(report);
        }

        // link entity to related take over documents
        if (reportHelper.hasReferencesToTakeOverDocuments(report)) {
            enrichWithRelatedTakeOverDocuments(fluxReport, report);
        }

        // check if the new report has already been deleted or corrected by another report, which could happen since
        // the order of delivery is not guaranteed
        markReportAsCorrectedIfNewerVersionExists(fluxReport);
        markReportAsDeletedIfDeletionReportExists(fluxReport);

        // save
        fluxReport = fluxReportDao.create(fluxReport);
        return mapper.map(fluxReport, Report.class);

    }

    private void markReportAsCorrectedIfNewerVersionExists(FluxReport fluxReport) {
        Optional<FluxReport> possibleCorrection = fluxReportDao.findCorrectionOf(fluxReport.getExtId());
        if (possibleCorrection.isPresent()) {
            fluxReport.setCorrection(possibleCorrection.get().getCreation());
        }
    }

    private void markReportAsDeletedIfDeletionReportExists(FluxReport fluxReport) {
        Optional<FluxReport> possibleDeletion = fluxReportDao.findDeletionOf(fluxReport.getExtId());
        if (possibleDeletion.isPresent()) {
            fluxReport.setDeletion(possibleDeletion.get().getCreation());
        }
    }

    private void markPreviousReportAsDeleted(Report report) {
        DateTime deletionDate = reportHelper.getCreationDate(report);
        String originalReportExtId = reportHelper.getFLUXReportDocumentReferencedId(report);

        Optional<FluxReport> originalReport = fluxReportDao.findByExtId(originalReportExtId);

        //TODO: don't only delete the referenced report, but also all reports it references in its turn
        if (originalReport.isPresent()) {
            // If a report was already deleted, we want to keep the original deletion date and not the date of the 'new' deletion.
            if (originalReport.get().getDeletion() == null) {
                originalReport.get().setDeletion(deletionDate);
            }
        }
    }

    private void enrichWithRelatedTakeOverDocuments(FluxReport fluxReportEntity, Report report) {
        List<String> takeOverDocumentIds = reportHelper.getReferenceIdsToTakeOverDocuments(report);
        List<FluxReport> takeOverDocuments = new ArrayList<>();

        for (String takeOverDocumentId : takeOverDocumentIds) {
            Optional<FluxReport> takeOverDocument = fluxReportDao.findByExtId(takeOverDocumentId);
            if (takeOverDocument.isPresent()) {
                takeOverDocuments.add(takeOverDocument.get());
            }
        }

        fluxReportEntity.setRelatedTakeOverDocuments(takeOverDocuments);
    }

    private void markPreviousReportAsCorrected(Report report) {
        String referencedId = reportHelper.getFLUXReportDocumentReferencedId(report);
        DateTime correctionDate = reportHelper.getCreationDate(report);

        Optional<FluxReport> previousReport = fluxReportDao.findByExtId(referencedId);
        if (previousReport.isPresent()) {
            previousReport.get().setCorrection(correctionDate);
        } else {
            log.error("Received a sales note with extId " + reportHelper.getId(report.getFLUXSalesReportMessage()) +
                    ", with  referencedId " + referencedId + ", but this referencedId does not exist!");
        }
    }
}
