package eu.europa.ec.fisheries.uvms.sales.service.factory;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FLUXSalesResponseMessageFactoryTest {

    @InjectMocks
    private FLUXSalesResponseMessageFactory FLUXSalesResponseMessageFactory;

    @Mock
    private ReportHelper reportHelper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void testCreateForSalesQuery() throws Exception {
        //data set
        FLUXPartyType fluxParty = new FLUXPartyType()
                .withIDS(new IDType().withValue("fluxParty"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withID(new IDType().withValue("salesQuery"))
                .withSubmitterFLUXParty(fluxParty);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(salesQuery);

        Report report = new Report().withFLUXSalesReportMessage(new FLUXSalesReportMessage());
        List<Report> reports = Arrays.asList(report, report, report, report);

        ValidationResultDocumentType validationResult = new ValidationResultDocumentType();

        for (int i = 0; i < 100; i++) {
           report.getFLUXSalesReportMessage().withSalesReports(new SalesReportType());
        }

        //execute
        FLUXSalesResponseMessage fluxSalesResponse = FLUXSalesResponseMessageFactory.create(fluxSalesQueryMessage, reports, validationResult);

        //assert
        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getIDS().get(0).getValue());
        assertEquals("salesQuery", fluxSalesResponse.getFLUXResponseDocument().getReferencedID().getValue());
        assertEquals(fluxParty, fluxSalesResponse.getFLUXResponseDocument().getRespondentFLUXParty());
        assertEquals(validationResult, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0));
        assertEquals(400, fluxSalesResponse.getSalesReports().size());
    }

    @Test
    public void testCreateForReport() {
        //data set
        String referencedId = "abc";
        Report report = new Report();
        FLUXPartyType fluxParty = new FLUXPartyType();
        ValidationResultDocumentType validationResult = new ValidationResultDocumentType();

        //mock
        doReturn(referencedId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(fluxParty).when(reportHelper).getFLUXReportDocumentOwner(report);

        //execute
        FLUXSalesResponseMessage fluxSalesResponse = FLUXSalesResponseMessageFactory.create(report, validationResult);

        //verify and assert
        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(reportHelper).getFLUXReportDocumentOwner(report);

        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getIDS().get(0).getValue());
        assertEquals(referencedId, fluxSalesResponse.getFLUXResponseDocument().getReferencedID().getValue());
        assertEquals(fluxParty, fluxSalesResponse.getFLUXResponseDocument().getRespondentFLUXParty());
        assertEquals(validationResult, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0));
        assertTrue(fluxSalesResponse.getSalesReports().isEmpty());
    }

}