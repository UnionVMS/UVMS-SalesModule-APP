package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.SalesParameterService;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
    private SalesParameterService parameterService;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private RulesService rulesService;

    @EJB
    private ReportDomainModel reportDomainModel;

    public void sendResponseToSenderOfReport(Report report,
                                             String pluginToSendResponseThrough,
                                             List<ValidationQualityAnalysisType> validationResults,
                                             String messageValidationStatus) throws ServiceException {
        FLUXSalesResponseMessage responseToSender = fluxSalesResponseMessageFactory.create(report, validationResults, messageValidationStatus);
        String senderOfReport = reportHelper.getFLUXReportDocumentOwnerId(report);
        rulesService.sendResponseToRules(responseToSender, senderOfReport, pluginToSendResponseThrough);
    }

    public void forwardReportToOtherRelevantParties(Report report, String pluginToSendResponseThrough) throws ServiceException {
        Report originalReport = findOriginalReport(report);
        String countryOfHost = parameterService.getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        String vesselFlagState = reportHelper.getVesselFlagState(originalReport);
        String salesLocationCountry = reportHelper.getSalesLocationCountry(originalReport);
        String landingCountry = reportHelper.getLandingCountry(originalReport);

        if (salesLocationCountry.equals(countryOfHost)) {
            if (!vesselFlagState.equals(countryOfHost)) {
                rulesService.sendReportToRules(report.getFLUXSalesReportMessage(), vesselFlagState, pluginToSendResponseThrough);
            }
            if (!landingCountry.equals(countryOfHost) && !landingCountry.equals(vesselFlagState)) {
                rulesService.sendReportToRules(report.getFLUXSalesReportMessage(), landingCountry, pluginToSendResponseThrough);
            }
        }
    }

    private Report findOriginalReport(Report report) {
        if (reportHelper.isReportCorrected(report) || reportHelper.isReportDeleted(report)) {
            Report referencedReport = findByExtId(reportHelper.getFLUXReportDocumentReferencedId(report));
            return findOriginalReport(referencedReport);
        } else {
            return report;
        }
    }

    private Report findByExtId(String extId) {
        return reportDomainModel.findByExtId(extId);
    }



}
