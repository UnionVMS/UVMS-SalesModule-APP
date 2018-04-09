package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;

import javax.ejb.*;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

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

    @Inject
    @FLUX
    private MapperFacade mapper;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private FluxReportDao fluxReportDao;

    @EJB
    BeanValidatorHelper beanValidatorHelper;

    @AccessTimeout(value = 60, unit = SECONDS)
    @Lock(LockType.WRITE)
    public Report create(@NonNull Report report, @NonNull String localCurrency, @NonNull BigDecimal exchangeRate) {
        log.debug("Persisting report {}", report.toString());

        FluxReport fluxReport = mapper.map(report, FluxReport.class);

        // set the receivedOn property to now
        fluxReport.receivedOn(DateTime.now());

        // convert the prices from the currency in the report to the local currency
        if (!reportHelper.isReportDeleted(report)) {
            enrichWithLocalCurrency(fluxReport, localCurrency, exchangeRate);
            roundPricesToTwoDecimals(fluxReport);
        }

        // to simplify search, each report entity has calculated fields that keeps whether is has been deleted or
        // corrected. Keep these fields up to date.
        if (reportHelper.isReportDeleted(report)) {
            markPreviousReportsAsDeleted(report);
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

        beanValidatorHelper.validateBean(fluxReport);

        // save
        fluxReport = fluxReportDao.create(fluxReport);
        return mapper.map(fluxReport, Report.class);

    }

    protected void roundPricesToTwoDecimals(FluxReport fluxReport) {
        if (fluxReport.getDocument().getTotalPrice() != null &&
                (fluxReport.getDocument().getTotalPrice().scale() > 2 ||
                fluxReport.getDocument().getTotalPriceLocal().scale() > 2)) {
            fluxReport.getDocument().setTotalPrice(fluxReport.getDocument().getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
            fluxReport.getDocument().setTotalPriceLocal(fluxReport.getDocument().getTotalPriceLocal().setScale(2, BigDecimal.ROUND_HALF_UP));
        }


        for (Product product : fluxReport.getDocument().getProducts()) {
            if (product.getPrice() != null &&
                    (product.getPrice().scale() > 2 ||
                     product.getPriceLocal().scale() > 2)) {
                product.setPrice(product.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
                product.setPriceLocal(product.getPriceLocal().setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
    }

    protected void enrichWithLocalCurrency(FluxReport fluxReport, String localCurrency, BigDecimal exchangeRate) {
        Document document = fluxReport.getDocument();

        document.setCurrencyLocal(localCurrency);

        // Total price in Sales Document is not mandatory, if it's missing we set the local total price to 0
        if (document.getTotalPrice() != null) {
            document.totalPriceLocal(document.getTotalPrice().setScale(2).divide(exchangeRate, 2));
        }

        for (Product product : document.getProducts()) {
            BigDecimal priceFromReport = product.getPrice();

            if (priceFromReport != null) {
                product.setPriceLocal(priceFromReport.setScale(2).divide(exchangeRate, 2));
            }

        }
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
        } else {
            Optional<FluxReport> possibleCorrection = fluxReportDao.findCorrectionOf(fluxReport.getExtId());
            if (possibleCorrection.isPresent() && possibleCorrection.get().isDeleted()) {
                fluxReport.setDeletion(possibleCorrection.get().getDeletion());
            }
        }
    }

    private void markPreviousReportsAsDeleted(Report report) {
        List<FluxReport> toBeMarkedAsDeleted = new ArrayList<>();

        DateTime deletionDate = reportHelper.getCreationDate(report);
        String originalReportExtId = reportHelper.getFLUXReportDocumentReferencedId(report);
        Optional<FluxReport> originalReport = fluxReportDao.findByExtId(originalReportExtId);

        if (originalReport.isPresent()) {
            toBeMarkedAsDeleted.add(originalReport.get());
            toBeMarkedAsDeleted.addAll(fluxReportDao.findOlderVersions(originalReport.get()));
        }

        for (FluxReport fluxReport : toBeMarkedAsDeleted) {
            // If a report was already deleted, we want to keep the original deletion date and not the date of the 'new' deletion.
            if (fluxReport.getDeletion() == null) {
                fluxReport.setDeletion(deletionDate);
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
