package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.OutgoingMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.ResponseService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * Class who's only purpose is to hide low-level logic from the ReportServiceBean.
 * Should not be used by any other class!
 */
@Slf4j
@Stateless
public class ReportServiceHelper {

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private ConfigService configService;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private ReportDomainModel reportDomainModel;

    @EJB
    private OutgoingMessageService outgoingMessageService;

    @EJB
    private ResponseService responseService;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendResponseToSenderOfReport(Report report,
                                             String pluginToSendResponseThrough,
                                             List<ValidationQualityAnalysisType> validationResults,
                                             String messageValidationStatus) {
        try {
            FLUXSalesResponseMessage responseToSender = fluxSalesResponseMessageFactory.create(report, validationResults, messageValidationStatus);
            String senderOfReport = reportHelper.getFLUXReportDocumentOwnerId(report);

            //Save the outgoing response
            responseService.saveResponse(responseToSender.getFLUXResponseDocument());

            outgoingMessageService.sendResponse(responseToSender, senderOfReport, pluginToSendResponseThrough);
        } catch (Exception e) {
            throw new SalesNonBlockingException("Rolling back transaction in sendResponseToSenderOfReport", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void forwardReportToOtherRelevantParties(Report report, String pluginToSendResponseThrough) {
        try {
            Report originalReport = findOriginalReport(report);

            // The rule that checks whether a correction/deletion refers to an existing report, has level "WARNING".
            // This means that it is possible to receive a delete report that refers to a non-existing original report.
            // In this case, it it is impossible to determine all the data needed to forward the delete report.
            if (report == originalReport /* not equals, literally the same object */ && reportHelper.isReportDeleted(report)) {
                log.error("A delete report has been received with id " + reportHelper.getId(report) + ". The referenced id, {}, refers to a non-existing report! We cannot determine whether this delete report should be forwarded!", reportHelper.getFLUXReportDocumentReferencedId(report));
                return;
            }

            String countryOfHost = configService.getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
            String vesselFlagState = reportHelper.getVesselFlagState(originalReport);
            String salesLocationCountry = reportHelper.getSalesLocationCountry(originalReport);
            String landingCountry = reportHelper.getLandingCountry(originalReport);

            if (reportHelper.isFirstSaleOrNegotiatedSale(originalReport) && salesLocationCountry.equals(countryOfHost)) {
                if (!vesselFlagState.equals(countryOfHost)) {
                    log.info("Forward sales report for vessel flag state: " + vesselFlagState);
                    outgoingMessageService.forwardReport(report.getFLUXSalesReportMessage(), vesselFlagState, pluginToSendResponseThrough);
                }
                if (!landingCountry.equals(countryOfHost) && !landingCountry.equals(vesselFlagState)) {
                    log.info("Forward sales report for landing country: " + landingCountry);
                    outgoingMessageService.forwardReport(report.getFLUXSalesReportMessage(), landingCountry, pluginToSendResponseThrough);
                }
            }
        } catch (Exception e) {
            throw new SalesNonBlockingException("Rolling back transaction in forwardReportToOtherRelevantParties", e);
        }
    }

    private Report findOriginalReport(Report report) {
        if (reportHelper.isReportCorrected(report) || reportHelper.isReportDeleted(report)) {
            Optional<Report> referencedReport = reportDomainModel.findByExtId(reportHelper.getFLUXReportDocumentReferencedId(report), true);
            if (referencedReport.isPresent()) {
                return findOriginalReport(referencedReport.get());
            } else {
                return report;
            }
        } else {
            return report;
        }
    }

}
