package eu.europa.ec.fisheries.uvms.sales.domain.mother;

import eu.europa.ec.fisheries.schema.sales.*;
import org.joda.time.DateTime;

/**
 * Utility class to generate objects, meant for tests.
 * The goal is to promote reuse and keep tests smaller/more readable.
 */
public class ReportMother {

    public static Report withExtId(String extId) {
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(new FLUXReportDocumentType()
                        .withIDS(new IDType().withValue(extId)));

        return new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
    }

    public static Report with(String extId, String referencedId) {
        FLUXReportDocumentType document = new FLUXReportDocumentType();

        if (extId != null) {
            document.getIDS().add(new IDType().withValue(extId));
        }

        if (referencedId != null) {
            document.setReferencedID(new IDType().withValue(referencedId));
        }

        return new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(document));
    }


    public static Report with(String purpose, String landingLocationCountry, String salesLocationCountry, String vesselFlagState, String referencedId) {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withPurposeCode(new CodeType().withValue(purpose));

        if (referencedId != null) {
            fluxReportDocument.setReferencedID(new IDType().withValue(referencedId));
        }

        FLUXLocationType fluxLocationForFishingActivity = new FLUXLocationType()
                .withCountryID(new IDType().withValue(landingLocationCountry));

        FLUXLocationType fluxLocationForSalesDocument = new FLUXLocationType()
                .withCountryID(new IDType().withValue(salesLocationCountry));

        VesselCountryType vesselCountry = new VesselCountryType()
                .withID(new IDType().withValue(vesselFlagState));

        VesselTransportMeansType vessel = new VesselTransportMeansType()
                .withRegistrationVesselCountry(vesselCountry);

        FishingActivityType fishingActivity = new FishingActivityType()
                .withRelatedFLUXLocations(fluxLocationForFishingActivity)
                .withRelatedVesselTransportMeans(vessel);

        SalesDocumentType salesDocument = new SalesDocumentType()
                .withSpecifiedFishingActivities(fishingActivity)
                .withSpecifiedFLUXLocations(fluxLocationForSalesDocument);

        SalesReportType salesReport = new SalesReportType()
                .withIncludedSalesDocuments(salesDocument);

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument)
                .withSalesReports(salesReport);

        return new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
    }

    public static Report withCreationDate(DateTime creationDate) {
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(new FLUXReportDocumentType()
                        .withCreationDateTime(new DateTimeType().withDateTime(creationDate)));

        return new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
    }


    public static Report withId(String id) {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue(id));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument);

        return new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
    }

}
