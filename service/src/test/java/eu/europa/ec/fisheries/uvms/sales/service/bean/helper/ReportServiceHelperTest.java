package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.OutgoingMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import eu.europa.ec.fisheries.uvms.sales.service.mother.ReportMother;
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
    private ConfigService configService;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Mock
    private OutgoingMessageService outgoingMessageService;

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
        verify(outgoingMessageService).sendResponse(responseToSender, senderOfReport, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsNotAFirstSaleOrNegotiatedSale() throws Exception {
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(false).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
    }

    @Test
    public void testForwardReportToOtherRelevantPartiesWhenReportIsADeleteWhichRefersToANonExistingReport() throws Exception {
        //data set
        Report delete = ReportMother.withId("delete");
        String referencedID = "abc";
        String pluginToSendResponseThrough = "FLUX";

        //mock
        doReturn(false).when(reportHelper).isReportCorrected(delete);
        doReturn(true).when(reportHelper).isReportDeleted(delete);
        doReturn(referencedID).when(reportHelper).getFLUXReportDocumentReferencedId(delete);
        doReturn("delete").when(reportHelper).getId(delete);
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(referencedID, true);


        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(delete, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(delete);
        verify(reportHelper, times(2)).isReportDeleted(delete);
        verify(reportHelper, times(2)).getFLUXReportDocumentReferencedId(delete);
        verify(reportDomainModel).findByExtId(referencedID, true);
        verify(reportHelper).getId(delete);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService);
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
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(report);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(report);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(report);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(report);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(report);
        verify(reportHelper).getSalesLocationCountry(report);
        verify(reportHelper).getLandingCountry(report);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(report);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(Optional.of(original)).when(reportDomainModel).findByExtId(referencedID, true);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID, true);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(original);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(Optional.of(original)).when(reportDomainModel).findByExtId(referencedID, true);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(deletion, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(deletion);
        verify(reportHelper).isReportDeleted(deletion);
        verify(reportHelper).getFLUXReportDocumentReferencedId(deletion);
        verify(reportDomainModel).findByExtId(referencedID, true);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(original);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(Optional.of(original)).when(reportDomainModel).findByExtId(referencedID, true);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(correction, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(correction);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correction);
        verify(reportDomainModel).findByExtId(referencedID, true);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(original);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService);
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
        doReturn(Optional.of(original)).when(reportDomainModel).findByExtId(referencedID, true);
        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(deletion, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(deletion);
        verify(reportHelper).isReportDeleted(deletion);
        verify(reportHelper).getFLUXReportDocumentReferencedId(deletion);
        verify(reportDomainModel).findByExtId(referencedID, true);
        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(original);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService);
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
        doReturn(Optional.of(firstCorrection)).when(reportDomainModel).findByExtId(referencedIDOfSecondCorrection, true);

        doReturn(true).when(reportHelper).isReportCorrected(firstCorrection);
        doReturn(referencedIDOfFirstCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        doReturn(Optional.of(original)).when(reportDomainModel).findByExtId(referencedIDOfFirstCorrection, true);

        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection, true);

        verify(reportHelper).isReportCorrected(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection, true);

        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(original);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, vesselFlagState, pluginToSendResponseThrough);
        verify(outgoingMessageService).forwardReport(fluxSalesReportMessage, landingCountry, pluginToSendResponseThrough);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService, outgoingMessageService);
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
        doReturn(Optional.of(firstCorrection)).when(reportDomainModel).findByExtId(referencedIDOfSecondCorrection, true);

        doReturn(true).when(reportHelper).isReportCorrected(firstCorrection);
        doReturn(referencedIDOfFirstCorrection).when(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        doReturn(Optional.of(original)).when(reportDomainModel).findByExtId(referencedIDOfFirstCorrection, true);

        doReturn(false).when(reportHelper).isReportCorrected(original);
        doReturn(false).when(reportHelper).isReportDeleted(original);
        doReturn(countryOfHost).when(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        doReturn(vesselFlagState).when(reportHelper).getVesselFlagState(original);
        doReturn(salesLocationCountry).when(reportHelper).getSalesLocationCountry(original);
        doReturn(landingCountry).when(reportHelper).getLandingCountry(original);
        doReturn(true).when(reportHelper).isFirstSaleOrNegotiatedSale(original);

        //execute
        reportServiceHelper.forwardReportToOtherRelevantParties(secondCorrection, pluginToSendResponseThrough);

        //verify
        verify(reportHelper).isReportCorrected(secondCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(secondCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfSecondCorrection, true);

        verify(reportHelper).isReportCorrected(firstCorrection);
        verify(reportHelper).getFLUXReportDocumentReferencedId(firstCorrection);
        verify(reportDomainModel).findByExtId(referencedIDOfFirstCorrection, true);

        verify(reportHelper).isReportCorrected(original);
        verify(reportHelper).isReportDeleted(original);
        verify(configService).getParameter(ParameterKey.FLUX_LOCAL_NATION_CODE);
        verify(reportHelper).getVesselFlagState(original);
        verify(reportHelper).getSalesLocationCountry(original);
        verify(reportHelper).getLandingCountry(original);
        verify(reportHelper).isFirstSaleOrNegotiatedSale(original);
        verifyNoMoreInteractions(reportDomainModel, fluxSalesResponseMessageFactory, reportHelper, configService);
    }

}