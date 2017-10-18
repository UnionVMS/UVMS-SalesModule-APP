package eu.europa.ec.fisheries.uvms.sales.service.factory;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Stateless
public class FLUXSalesResponseMessageFactory {

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private ConfigService configService;

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
        return configService.getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
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
        return create(referencedId, validationResults, messageValidationStatus, "UUID");
    }

    public FLUXSalesResponseMessage create(String referencedId,
                                           Collection<ValidationQualityAnalysisType> validationResults,
                                           String messageValidationStatus,
                                           String schemeId) {
        String fluxLocationNationCode = findFluxLocalNationCode();
        FLUXPartyType fluxParty = createFluxParty(fluxLocationNationCode);
        ValidationResultDocumentType validationResultDocument = createValidationResultDocument(validationResults, fluxLocationNationCode);
        FLUXResponseDocumentType fluxResponseDocument = createFluxResponseDocumentType(referencedId, validationResultDocument, fluxParty, messageValidationStatus, schemeId);

        return new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(fluxResponseDocument);
    }

    private FLUXPartyType createFluxParty(String fluxLocalNationCode) {
        return new FLUXPartyType()
                .withIDS(new IDType() .withValue(fluxLocalNationCode)
                                      .withSchemeID("FLUX_GP_PARTY"));
    }

    private ValidationResultDocumentType createValidationResultDocument(Collection<ValidationQualityAnalysisType> validationResults, String fluxLocalNationCode) {
        return new ValidationResultDocumentType()
                    .withValidatorID(new IDType().withValue(fluxLocalNationCode))
                    .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now().withZone(DateTimeZone.UTC)))
                    .withRelatedValidationQualityAnalysises(validationResults);
    }

    private FLUXResponseDocumentType createFluxResponseDocumentType(String referencedId, ValidationResultDocumentType validationResultDocument, FLUXPartyType fluxParty, String messageValidationStatus) {
        FLUXResponseDocumentType fluxResponseDocumentType = new FLUXResponseDocumentType()
                .withIDS(createRandomUUID())
                .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now().withZone(DateTimeZone.UTC)))
                .withReferencedID(createUUID(referencedId))
                .withRespondentFLUXParty(fluxParty);

        fluxResponseDocumentType.withResponseCode(new CodeType().withValue(messageValidationStatus)
                                                                .withListID("FLUX_GP_RESPONSE"));
        fluxResponseDocumentType.withRelatedValidationResultDocuments(validationResultDocument);

        return fluxResponseDocumentType;
    }

    private FLUXResponseDocumentType createFluxResponseDocumentType(String referencedId, ValidationResultDocumentType validationResultDocument, FLUXPartyType fluxParty, String messageValidationStatus, String schemeId) {
        FLUXResponseDocumentType fluxResponseDocumentType = new FLUXResponseDocumentType()
                .withIDS(createRandomUUID())
                .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now().withZone(DateTimeZone.UTC)))
                .withReferencedID(createID(referencedId, schemeId))
                .withRespondentFLUXParty(fluxParty);

        fluxResponseDocumentType.withResponseCode(new CodeType().withValue(messageValidationStatus)
                                                                .withListID("FLUX_GP_RESPONSE"));
        fluxResponseDocumentType.withRelatedValidationResultDocuments(validationResultDocument);

        return fluxResponseDocumentType;
    }

    private IDType createUUID(String uuidValue) {
        return new IDType() .withValue(uuidValue)
                            .withSchemeID("UUID");
    }

    private IDType createID(String id, String schemeId) {
        return new IDType() .withValue(id)
                            .withSchemeID(schemeId);
    }

    private IDType createRandomUUID() {
        return createUUID(UUID.randomUUID().toString());
    }

}
