package eu.europa.ec.fisheries.uvms.sales.service.factory;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import org.joda.time.DateTimeZone;
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

    @Mock
    private ConfigService configService;

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

        String messageValidationStatus = "OK";
        String fluxLocalNationCode = "BEL";

        for (int i = 0; i < 100; i++) {
           report.getFLUXSalesReportMessage().withSalesReports(new SalesReportType());
        }

        ValidationQualityAnalysisType validationResult = new ValidationQualityAnalysisType().withID(new IDType().withValue("v"));
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(validationResult);

        //mock
        doReturn(fluxLocalNationCode).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);

        //execute
        FLUXSalesResponseMessage fluxSalesResponse = FLUXSalesResponseMessageFactory.create(fluxSalesQueryMessage, reports, validationResults, messageValidationStatus);

        //verify and assert
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);

        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getIDS().get(0).getValue());
        assertEquals("UUID", fluxSalesResponse.getFLUXResponseDocument().getIDS().get(0).getSchemeID());

        assertEquals("salesQuery", fluxSalesResponse.getFLUXResponseDocument().getReferencedID().getValue());
        assertEquals("UUID", fluxSalesResponse.getFLUXResponseDocument().getReferencedID().getSchemeID());

        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getCreationDateTime());
        assertEquals(DateTimeZone.UTC, fluxSalesResponse.getFLUXResponseDocument().getCreationDateTime().getDateTime().getZone());

        assertEquals(fluxLocalNationCode, fluxSalesResponse.getFLUXResponseDocument().getRespondentFLUXParty().getIDS().get(0).getValue());
        assertEquals("FLUX_GP_PARTY", fluxSalesResponse.getFLUXResponseDocument().getRespondentFLUXParty().getIDS().get(0).getSchemeID());

        assertEquals(validationResult, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getRelatedValidationQualityAnalysises().get(0));
        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getCreationDateTime());
        assertEquals(DateTimeZone.UTC, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getCreationDateTime().getDateTime().getZone());
        assertEquals(fluxLocalNationCode, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getValidatorID().getValue());
        assertEquals(400, fluxSalesResponse.getSalesReports().size());

        assertEquals(messageValidationStatus, fluxSalesResponse.getFLUXResponseDocument().getResponseCode().getValue());
        assertEquals("FLUX_GP_RESPONSE", fluxSalesResponse.getFLUXResponseDocument().getResponseCode().getListID());
    }

    @Test
    public void testCreateForReport() {
        //data set
        String referencedId = "abc";
        Report report = new Report();
        String fluxLocalNationCode = "BEL";
        String messageValidationStatus = "OK";

        ValidationQualityAnalysisType validationResult = new ValidationQualityAnalysisType().withID(new IDType().withValue("v"));
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(validationResult);

        //mock
        doReturn(referencedId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(fluxLocalNationCode).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);

        //execute
        FLUXSalesResponseMessage fluxSalesResponse = FLUXSalesResponseMessageFactory.create(report, validationResults, messageValidationStatus);

        //verify and assert
        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);

        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getIDS().get(0).getValue());
        assertEquals("UUID", fluxSalesResponse.getFLUXResponseDocument().getIDS().get(0).getSchemeID());

        assertEquals(referencedId, fluxSalesResponse.getFLUXResponseDocument().getReferencedID().getValue());
        assertEquals("UUID", fluxSalesResponse.getFLUXResponseDocument().getReferencedID().getSchemeID());

        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getCreationDateTime());
        assertEquals(DateTimeZone.UTC, fluxSalesResponse.getFLUXResponseDocument().getCreationDateTime().getDateTime().getZone());

        assertEquals(fluxLocalNationCode, fluxSalesResponse.getFLUXResponseDocument().getRespondentFLUXParty().getIDS().get(0).getValue());
        assertEquals("FLUX_GP_PARTY", fluxSalesResponse.getFLUXResponseDocument().getRespondentFLUXParty().getIDS().get(0).getSchemeID());

        assertEquals(validationResult, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getRelatedValidationQualityAnalysises().get(0));
        assertNotNull(fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getCreationDateTime());
        assertEquals(DateTimeZone.UTC, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getCreationDateTime().getDateTime().getZone());
        assertEquals(fluxLocalNationCode, fluxSalesResponse.getFLUXResponseDocument().getRelatedValidationResultDocuments().get(0).getValidatorID().getValue());
        assertTrue(fluxSalesResponse.getSalesReports().isEmpty());

        assertEquals(messageValidationStatus, fluxSalesResponse.getFLUXResponseDocument().getResponseCode().getValue());
        assertEquals("FLUX_GP_RESPONSE", fluxSalesResponse.getFLUXResponseDocument().getResponseCode().getListID());
    }

}