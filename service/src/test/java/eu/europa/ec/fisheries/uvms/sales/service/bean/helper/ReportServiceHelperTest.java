package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationResultDocumentType;
import eu.europa.ec.fisheries.uvms.sales.model.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import eu.europa.ec.fisheries.uvms.sales.service.mother.ReportMother;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceHelperTest {

    @InjectMocks
    private ReportServiceHelper reportServiceHelper;

    @Mock
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @Mock
    private ReportHelper reportHelper;

    @Mock
    private ParameterService parameterService;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Test
    public void testSendResponseToSenderOfReport() throws Exception {
        //data set
        Report report = new Report();
        FLUXSalesResponseMessage responseToSender = new FLUXSalesResponseMessage();
        String senderOfReport = "FRA";

        //mock
        doReturn(responseToSender).when(fluxSalesResponseMessageFactory).create(report, new ValidationResultDocumentType());
        doReturn(senderOfReport).when(reportHelper).getFLUXReportDocumentOwnerId(report);

        //execute
        reportServiceHelper.sendResponseToSenderOfReport(report);

        //verify
        verify(fluxSalesResponseMessageFactory).create(report, new ValidationResultDocumentType());
        verify(reportHelper).getFLUXReportDocumentOwnerId(report);
        verify(exchangeService).sendToExchange(responseToSender, senderOfReport);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "NLD";
        String landingCountry = "SWE";
        Report report = new Report();

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "NLD";
        String landingCountry = "BEL";
        Report report = new Report();

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "BEL";
        String landingCountry = "SWE";
        Report report = new Report();

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        Report report = new Report();

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "NLD";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, vesselFlagState);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, landingCountry);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "NLD";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, landingCountry);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "BEL";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, vesselFlagState);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsTheSameAsVesselFlagCountry() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "FRA";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, vesselFlagState);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionOrDeletionAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "NLD";
        String referencedID = "abc";

        Report original = ReportMother.withId("original");
        Report correction = ReportMother.withId("correction");
        FLUXSalesReportMessage fluxSalesReportMessage = correction.getFLUXSalesReportMessage();

        //mock
        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(correction);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(correction);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, vesselFlagState);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, landingCountry);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionOrDeletionAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        String referencedID = "abc";

        Report original = ReportMother.withId("original");
        Report correction = ReportMother.withId("correction");

        //mock
        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(correction);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(correction);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionOrDeletionWhen2LevelsDeepAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "NLD";
        String referencedIDOfSecondCorrection = "abc";
        String referencedIDOfFirstCorrection = "def";

        Report original = ReportMother.withId("original");
        Report firstCorrection = ReportMother.withId("firstCorrection");
        Report secondCorrection = ReportMother.withId("secondCorrection");
        FLUXSalesReportMessage fluxSalesReportMessage = secondCorrection.getFLUXSalesReportMessage();

        //mock
        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(secondCorrection);
        doReturn(referencedIDOfSecondCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        doReturn(firstCorrection).when(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(firstCorrection);
        doReturn(referencedIDOfFirstCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        doReturn(original).when(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, vesselFlagState);
        verify(exchangeService).sendToExchange(fluxSalesReportMessage, landingCountry);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionOrDeletionWhen2LevelsDeepAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        String referencedIDOfSecondCorrection = "abc";
        String referencedIDOfFirstCorrection = "def";

        Report original = ReportMother.withId("original");
        Report firstCorrection = ReportMother.withId("firstCorrection");
        Report secondCorrection = ReportMother.withId("secondCorrection");
        FLUXSalesReportMessage fluxSalesReportMessage = secondCorrection.getFLUXSalesReportMessage();

        //mock
        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(secondCorrection);
        doReturn(referencedIDOfSecondCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        doReturn(firstCorrection).when(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(firstCorrection);
        doReturn(referencedIDOfFirstCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        doReturn(original).when(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.COUNTRY_OF_HOST);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, exchangeService);
    }

}