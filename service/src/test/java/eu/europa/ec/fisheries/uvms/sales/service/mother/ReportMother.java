package eu.europa.ec.fisheries.uvms.sales.service.mother;

import eu.europa.ec.fisheries.schema.sales.*;

import javax.annotation.Nullable;

/**
 * Utility class to generate objects, meant for tests.
 * The goal is to promote reuse and keep tests smaller/more readable.
 */
public class ReportMother {

    public static Report with(String purpose, String landingLocationCountry, String salesLocationCountry, String vesselFlagState, @Nullable String referencedId) {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withPurposeCode(new CodeType().withValue(purpose));

        if (referencedId != null) {
            fluxReportDocument.setReferencedID(new IDType().withValue(referencedId));
        }

        FLUXLocationType fluxLocationForFishingActivity = new FLUXLocationType()
                .withCountryID(new IDType().withValue(landingLocationCountry));

        FLUXLocationType fluxLocationForSalesDocument = new FLUXLocationType()
                .withCountryID(new IDType().withValue(salesLocationCountry));

        RegistrationLocationType registrationLocationForVessel = new RegistrationLocationType()
                .withCountryID(new IDType().withValue(vesselFlagState));

        RegistrationEventType registrationEventForVessel = new RegistrationEventType()
                .withRelatedRegistrationLocation(registrationLocationForVessel);

        VesselTransportMeansType vessel = new VesselTransportMeansType()
                .withSpecifiedRegistrationEvents(registrationEventForVessel);

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

    public static Report withId(String id) {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue(id));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument);

        return new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
    }
}
