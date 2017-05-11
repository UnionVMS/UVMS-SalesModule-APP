package eu.europa.ec.fisheries.uvms.sales.service.factory;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportHelper;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.UUID;

@Stateless
public class FLUXSalesResponseMessageFactory {

    @EJB
    private ReportHelper reportHelper;

    public FLUXSalesResponseMessage create(FLUXSalesQueryMessage fluxSalesQueryMessage,
                                           List<Report> reports,
                                           ValidationResultDocumentType validationResultDocument) {

        String referencedId = fluxSalesQueryMessage.getSalesQuery().getID().getValue();
        FLUXPartyType fluxParty = fluxSalesQueryMessage.getSalesQuery().getSubmitterFLUXParty();
        FLUXResponseDocumentType fluxResponseDocument = createFluxResponseDocumentType(referencedId, validationResultDocument, fluxParty);

        FLUXSalesResponseMessage fluxSalesResponseMessage = new FLUXSalesResponseMessage();
        fluxSalesResponseMessage.setFLUXResponseDocument(fluxResponseDocument);
        for (Report report : reports) {
            fluxSalesResponseMessage.getSalesReports().addAll(report.getFLUXSalesReportMessage().getSalesReports());
        }

        return fluxSalesResponseMessage;
    }

    public FLUXSalesResponseMessage create(Report report, ValidationResultDocumentType validationResultDocument) {
        String referencedId = reportHelper.getFLUXReportDocumentId(report);
        FLUXPartyType fluxParty = reportHelper.getFLUXReportDocumentOwner(report);
        FLUXResponseDocumentType fluxResponseDocument = createFluxResponseDocumentType(referencedId, validationResultDocument, fluxParty);

        return new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(fluxResponseDocument);
    }

    private FLUXResponseDocumentType createFluxResponseDocumentType(String referencedId, ValidationResultDocumentType validationResultDocument, FLUXPartyType fluxParty) {
        FLUXResponseDocumentType fluxResponseDocumentType = new FLUXResponseDocumentType()
                .withIDS(new IDType().withValue(UUID.randomUUID().toString()))
                .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now()))
                .withReferencedID(new IDType().withValue(referencedId))
                .withRespondentFLUXParty(fluxParty);

        fluxResponseDocumentType.withResponseCode(new CodeType().withListID("OK")); //TODO: implement correctly
        fluxResponseDocumentType.withRelatedValidationResultDocuments(validationResultDocument);

        return fluxResponseDocumentType;
    }

}
