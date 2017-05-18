package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationResultDocumentType;
import eu.europa.ec.fisheries.uvms.sales.model.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
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
    private RulesService rulesService;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Test
    public void testSendResponseToSenderOfReport() throws Exception {
        //data set
        Report report = new Report();
        FLUXSalesResponseMessage responseToSender = new FLUXSalesResponseMessage();
        String senderOfReport = "FRA";
        String pluginToSendResponseThrough = "FLUX";

        //mock
        doReturn(responseToSender).when(fluxSalesResponseMessageFactory).create(report, new ValidationResultDocumentType());
        doReturn(senderOfReport).when(reportHelper).getFLUXReportDocumentOwnerId(report);

        //execute
        reportServiceHelper.sendResponseToSenderOfReport(report, pluginToSendResponseThrough);

        //verify
        verify(fluxSalesResponseMessageFactory).create(report, new ValidationResultDocumentType());
        verify(reportHelper).getFLUXReportDocumentOwnerId(report);
        verify(rulesService).sendResponseToRules(responseToSender, senderOfReport, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "NLD";
        String landingCountry = "SWE";
        Report report = new Report();
        String pluginToSendResponseThrough = "FLUX";

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "NLD";
        String landingCountry = "BEL";
        Report report = new Report();
        String pluginToSendResponseThrough = "FLUX";

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "BEL";
        String landingCountry = "SWE";
        Report report = new Report();
        String pluginToSendResponseThrough = "FLUX";

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "FRA";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        Report report = new Report();
        String pluginToSendResponseThrough = "FLUX";

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "NLD";
        String pluginToSendResponseThrough = "FLUX";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "NLD";
        String pluginToSendResponseThrough = "FLUX";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "BEL";
        String pluginToSendResponseThrough = "FLUX";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        String pluginToSendResponseThrough = "FLUX";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsOriginalAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryIsTheSameAsVesselFlagCountry() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "FRA";
        String pluginToSendResponseThrough = "FLUX";

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage();
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        //mock
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionOrDeletionAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "NLD";
        String referencedID = "abc";
        String pluginToSendResponseThrough = "FLUX";

        Report original = ReportMother.withId("original");
        Report correction = ReportMother.withId("correction");
        FLUXSalesReportMessage fluxSalesReportMessage = correction.getFLUXSalesReportMessage();

        //mock
        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(correction);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(correction);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionOrDeletionAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        String referencedID = "abc";
        String pluginToSendResponseThrough = "FLUX";

        Report original = ReportMother.withId("original");
        Report correction = ReportMother.withId("correction");

        //mock
        doReturn(true).when(reportHelper).isReportCorrectedOrDeleted(correction);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(correction);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrectedOrDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
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
        String pluginToSendResponseThrough = "FLUX";

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
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
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
        String pluginToSendResponseThrough = "FLUX";

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
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrectedOrDeleted(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        verify(reportHelper).isReportCorrectedOrDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

}