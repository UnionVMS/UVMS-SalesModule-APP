package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * Class who's only purpose is to hide low-level logic from the ReportServiceBean.
 * Should not be used by any other class!
 */
@Stateless
public class ReportServiceHelper {

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB(lookup = ServiceConstants.DB_ACCESS_PARAMETER_SERVICE)
    private ParameterService parameterService;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private RulesService rulesService;

    @EJB(lookup = ServiceConstants.DB_ACCESS_REPORT_DOMAIN_MODEL)
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

    /**
     * Returns all referenced reports, including the report that has
     * the given referencedId.
     * @param firstReferencedId the first referenced id of the report for which all referenced reports need to be retrieved.
     * @return all referenced reports. When nothing found, an empty list.
     */
    public List<Report> findAllReportsThatAreCorrectedOrDeleted(@Nullable String firstReferencedId) {
        List<Report> referencedReports = new ArrayList<>();

        if (StringUtils.isNotBlank(firstReferencedId)) {
            Report referencedReport = reportDomainModel.findByExtId(firstReferencedId);
            referencedReports.add(referencedReport);

            String nextReferencedId = reportHelper.getFLUXReportDocumentReferencedIdOrNull(referencedReport);
            referencedReports.addAll(findAllReportsThatAreCorrectedOrDeleted(nextReferencedId));
        }

        return referencedReports;
    }

    /**
     *  Retrieves all sales notes and take over documents which refer, via any path, to the given sales note / take
     *  over document. In contradiction to ReportDomainModelBean.findReportsWhichReferTo, indirect relations are also
     *  returned.
     *
     *  Referring means: have a referencedId which is the extId of the given report.
     *
     *  Example:
     *  A refers to B
     *  B refers to D
     *  C refers to D
     *  D refers to E
     *
     *  What is returned when you execute findAllCorrectionsOrDeletionsOf(D)? A, B and C (yes, A, because A
     *  points to B and B points to D. That means: D was corrected by B, and B was corrected/deleted by A).
     *
     **/
    public List<Report> findAllCorrectionsOrDeletionsOf(String extId) {
        List<Report> directlyLinkedReports = reportDomainModel.findReportsWhichReferTo(extId);
        List<Report> indirectlyLinkedReports = new ArrayList<>();

        for (Report directlyLinkedReport : directlyLinkedReports) {
            String directlyLinkedReportExtId = reportHelper.getFLUXReportDocumentId(directlyLinkedReport);
            indirectlyLinkedReports.addAll(findAllCorrectionsOrDeletionsOf(directlyLinkedReportExtId));
        }

        return ListUtils.union(directlyLinkedReports, indirectlyLinkedReports);
    }

    private Report findOriginalReport(Report report) {
        if (reportHelper.isReportCorrectedOrDeleted(report)) {
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
