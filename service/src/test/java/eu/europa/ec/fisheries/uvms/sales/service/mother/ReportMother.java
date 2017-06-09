package eu.europa.ec.fisheries.uvms.sales.service.mother;

import eu.europa.ec.fisheries.schema.sales.*;
import org.joda.time.DateTime;

/**
 * Utility class to generate objects, meant for tests.
 * The goal is to promote reuse and keep tests smaller/more readable.
 */
public class ReportMother {

    public static Report withId(String id) {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue(id));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument);

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
}
