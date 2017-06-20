package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.SalesParameterService;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mother.ReportMother;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

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
    private SalesParameterService parameterService;

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
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(new ValidationQualityAnalysisType());
        String messageValidationResult = "OK";

        //mock
        doReturn(responseToSender).when(fluxSalesResponseMessageFactory).create(report, validationResults, messageValidationResult);
        doReturn(senderOfReport).when(reportHelper).getFLUXReportDocumentOwnerId(report);

        //execute
        reportServiceHelper.sendResponseToSenderOfReport(report, pluginToSendResponseThrough, validationResults, messageValidationResult);

        //verify
        verify(fluxSalesResponseMessageFactory).create(report, validationResults, messageValidationResult);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
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
        doReturn(false).when(reportHelper).isReportCorrected(report);
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).isReportDeleted(report);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryNotCountryOfHost() throws Exception {
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
        doReturn(true).when(reportHelper).isReportCorrected(correction);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(correction);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsDeletionAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryNotCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "FRA";
        String landingCountry = "NLD";
        String referencedID = "abc";
        String pluginToSendResponseThrough = "FLUX";

        Report original = ReportMother.withId("original");
        Report deletion = ReportMother.withId("deletion");
        FLUXSalesReportMessage fluxSalesReportMessage = deletion.getFLUXSalesReportMessage();

        //mock
        doReturn(false).when(reportHelper).isReportCorrected(deletion);
        doReturn(true).when(reportHelper).isReportDeleted(deletion);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(deletion);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(deletion, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(deletion);
        verify(reportHelper).isReportDeleted(deletion);
        verify(reportHelper).getFLUXReportDocumentReferencedId(deletion);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryCountryOfHost() throws Exception {
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
        doReturn(true).when(reportHelper).isReportCorrected(correction);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(correction);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsDeletionAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryCountryOfHost() throws Exception {
        //data set
        String countryOfHost = "BEL";
        String salesLocationCountry = "BEL";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        String referencedID = "abc";
        String pluginToSendResponseThrough = "FLUX";

        Report original = ReportMother.withId("original");
        Report deletion = ReportMother.withId("deletion");

        //mock
        doReturn(false).when(reportHelper).isReportCorrected(deletion);
        doReturn(true).when(reportHelper).isReportDeleted(deletion);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(deletion);
        doReturn(original).when(reportDomainModel).findByExtId(referencedID);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(deletion, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(deletion);
        verify(reportHelper).isReportDeleted(deletion);
        verify(reportHelper).getFLUXReportDocumentReferencedId(deletion);
        verify(reportDomainModel).findByExtId(referencedID);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionWhen2LevelsDeepAndSalesLocationIsCountryOfHostAndVesselFlagIsNotCountryOfHostAndLandingCountryNotCountryOfHost() throws Exception {
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
        doReturn(true).when(reportHelper).isReportCorrected(secondCorrection);
        doReturn(referencedIDOfSecondCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        doReturn(firstCorrection).when(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        doReturn(true).when(reportHelper).isReportCorrected(firstCorrection);
        doReturn(referencedIDOfFirstCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        doReturn(original).when(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        verify(reportHelper).isReportCorrected(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(rulesService).sendReportToRules(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsCorrectionWhen2LevelsDeepAndSalesLocationIsCountryOfHostAndVesselFlagIsCountryOfHostAndLandingCountryIsCountryOfHost() throws Exception {
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

        //mock
        doReturn(true).when(reportHelper).isReportCorrected(secondCorrection);
        doReturn(referencedIDOfSecondCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        doReturn(firstCorrection).when(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        doReturn(true).when(reportHelper).isReportCorrected(firstCorrection);
        doReturn(referencedIDOfFirstCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        doReturn(original).when(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection);

        verify(reportHelper).isReportCorrected(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection);

        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(parameterService).getParameterValue(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, reportHelper, parameterService, rulesService);
    }

}