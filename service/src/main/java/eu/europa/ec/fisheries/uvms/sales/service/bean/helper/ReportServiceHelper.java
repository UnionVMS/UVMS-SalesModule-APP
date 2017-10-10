package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * Class who's only purpose is to hide low-level logic from the ReportServiceBean.
 * Should not be used by any other class!
 */
@Stateless
public class ReportServiceHelper {

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private ConfigService configService;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private RulesService rulesService;

    @EJB
    private ReportDomainModel reportDomainModel;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendResponseToSenderOfReport(Report report,
                                             String pluginToSendResponseThrough,
                                             List<ValidationQualityAnalysisType> validationResults,
                                             String messageValidationStatus) {
        try {
            FLUXSalesResponseMessage responseToSender = fluxSalesResponseMessageFactory.create(report, validationResults, messageValidationStatus);
            String senderOfReport = reportHelper.getFLUXReportDocumentOwnerId(report);
            rulesService.sendResponseToRules(responseToSender, senderOfReport, pluginToSendResponseThrough);
        } catch (Exception e) {
            throw new SalesNonBlockingException("Rolling back transaction in sendResponseToSenderOfReport", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void forwardReportToOtherRelevantParties(Report report, String pluginToSendResponseThrough) {
        try {
            Report originalReport = findOriginalReport(report);

            String countryOfHost = configService.getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
            String vesselFlagState = reportHelper.getVesselFlagState(originalReport);
            String salesLocationCountry = reportHelper.getSalesLocationCountry(originalReport);
            String landingCountry = reportHelper.getLandingCountry(originalReport);

            if (reportHelper.isFirstSaleOrNegotiatedSale(originalReport) && salesLocationCountry.equals(countryOfHost)) {
                if (!vesselFlagState.equals(countryOfHost)) {
                    rulesService.sendReportToRules(report.getFLUXSalesReportMessage(), vesselFlagState, pluginToSendResponseThrough);
                }
                if (!landingCountry.equals(countryOfHost) && !landingCountry.equals(vesselFlagState)) {
                    rulesService.sendReportToRules(report.getFLUXSalesReportMessage(), landingCountry, pluginToSendResponseThrough);
                }
            }
        } catch (Exception e) {
            throw new SalesNonBlockingException("Rolling back transaction in forwardReportToOtherRelevantParties", e);
        }
    }

    private Report findOriginalReport(Report report) {
        if (reportHelper.isReportCorrected(report) || reportHelper.isReportDeleted(report)) {
            Report referencedReport = reportDomainModel.findByExtId(reportHelper.getFLUXReportDocumentReferencedId(report)).get();
            return findOriginalReport(referencedReport);
        } else {
            return report;
        }
    }

}
