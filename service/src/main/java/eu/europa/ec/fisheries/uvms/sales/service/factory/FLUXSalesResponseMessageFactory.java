package eu.europa.ec.fisheries.uvms.sales.service.factory;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.model.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.constants.ServiceConstants;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Stateless
public class FLUXSalesResponseMessageFactory {

    @EJB
    private ReportHelper reportHelper;

    @EJB(lookup = ServiceConstants.DB_ACCESS_PARAMETER_SERVICE)
    private ParameterService parameterService;

    public FLUXSalesResponseMessage create(FLUXSalesQueryMessage fluxSalesQueryMessage,
                                           List<Report> reports,
                                           List<ValidationQualityAnalysisType> validationResults,
                                           String messageValidationStatus) {

        String referencedId = fluxSalesQueryMessage.getSalesQuery().getID().getValue();
        String fluxLocalNationCode = findFluxLocalNationCode();
        FLUXPartyType fluxParty = createFluxParty(fluxLocalNationCode);
        ValidationResultDocumentType validationResultDocument = createValidationResultDocument(validationResults, fluxLocalNationCode);
        FLUXResponseDocumentType fluxResponseDocument = createFluxResponseDocumentType(referencedId, validationResultDocument, fluxParty, messageValidationStatus);

        FLUXSalesResponseMessage fluxSalesResponseMessage = new FLUXSalesResponseMessage();
        fluxSalesResponseMessage.setFLUXResponseDocument(fluxResponseDocument);
        for (Report report : reports) {
            fluxSalesResponseMessage.getSalesReports().addAll(report.getFLUXSalesReportMessage().getSalesReports());
        }

        return fluxSalesResponseMessage;
    }

    private String findFluxLocalNationCode() {
        return parameterService.getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
    }

    public FLUXSalesResponseMessage create(Report report,
                                           Collection<ValidationQualityAnalysisType> validationResults,
                                           String messageValidationStatus) {
        String referencedId = reportHelper.getFLUXReportDocumentId(report);
        return create(referencedId, validationResults, messageValidationStatus);
    }

    public FLUXSalesResponseMessage create(String referencedId,
                                           Collection<ValidationQualityAnalysisType> validationResults,
                                           String messageValidationStatus) {
        String fluxLocationNationCode = findFluxLocalNationCode();
        FLUXPartyType fluxParty = createFluxParty(fluxLocationNationCode);
        ValidationResultDocumentType validationResultDocument = createValidationResultDocument(validationResults, fluxLocationNationCode);
        FLUXResponseDocumentType fluxResponseDocument = createFluxResponseDocumentType(referencedId, validationResultDocument, fluxParty, messageValidationStatus);

        return new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(fluxResponseDocument);
    }

    private FLUXPartyType createFluxParty(String fluxLocalNationCode) {
        return new FLUXPartyType().withIDS(new IDType().withValue(fluxLocalNationCode));
    }

    private ValidationResultDocumentType createValidationResultDocument(Collection<ValidationQualityAnalysisType> validationResults, String fluxLocalNationCode) {
        return new ValidationResultDocumentType()
                    .withValidatorID(new IDType().withValue(fluxLocalNationCode))
                    .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now()))
                    .withRelatedValidationQualityAnalysises(validationResults);
    }

    private FLUXResponseDocumentType createFluxResponseDocumentType(String referencedId, ValidationResultDocumentType validationResultDocument, FLUXPartyType fluxParty, String messageValidationStatus) {
        FLUXResponseDocumentType fluxResponseDocumentType = new FLUXResponseDocumentType()
                .withIDS(new IDType().withValue(UUID.randomUUID().toString()))
                .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now()))
                .withReferencedID(new IDType().withValue(referencedId))
                .withRespondentFLUXParty(fluxParty);

        fluxResponseDocumentType.withResponseCode(new CodeType().withValue(messageValidationStatus));
        fluxResponseDocumentType.withRelatedValidationResultDocuments(validationResultDocument);

        return fluxResponseDocumentType;
    }

}
