package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.FLUXSalesResponseMessageHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({JAXBMarshaller.class, RulesModuleRequestMapper.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"})
public class RulesServiceBeanTest {

    @InjectMocks
    private RulesServiceBean rulesServiceBean;

    @Mock
    private ConfigService configService;

    @Mock
    private SalesMessageProducer messageProducer;

    @Mock
    private FLUXSalesResponseMessageHelper responseHelper;

    @Mock
    private ReportHelper reportHelper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendResponseToRules() throws Exception {
        //data set
        String responseAsString = "response";
        String request = "request";
        String pluginToSendResponseThrough = "FLUX";
        String recipient = "FRA";
        String fluxDataFlow = "FLUX";
        String responseGuid = "abc";
        String messageSelector = "SendSalesResponseRequest";
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();

        //mock
        mockStatic(JAXBMarshaller.class);
        mockStatic(RulesModuleRequestMapper.class);

        when(JAXBMarshaller.marshallJaxBObjectToString(response)).thenReturn(responseAsString);
        when(configService.getParameter(ParameterKey.FLUX_DATA_FLOW)).thenReturn(fluxDataFlow);
        when(responseHelper.getId(response)).thenReturn(responseGuid);
        when(RulesModuleRequestMapper.createSendSalesResponseRequest(eq(responseAsString), eq(responseGuid), eq(recipient), eq(pluginToSendResponseThrough), eq(fluxDataFlow), isA(Date.class))).thenReturn(request);

        //execute
        rulesServiceBean.sendResponseToRules(response, recipient, pluginToSendResponseThrough);

        //verify and assert
        verifyStatic();
        JAXBMarshaller.marshallJaxBObjectToString(response);

        verify(configService).getParameter(ParameterKey.FLUX_DATA_FLOW);
        verify(responseHelper).getId(response);
        verify(messageProducer).sendModuleMessage(request, Union.RULES,messageSelector);

        verifyStatic();
        RulesModuleRequestMapper.createSendSalesResponseRequest(eq(responseAsString), eq(responseGuid), eq(recipient), eq(pluginToSendResponseThrough), eq(fluxDataFlow), isA(Date.class));

        verifyNoMoreInteractions(messageProducer, reportHelper, responseHelper, configService);
    }

    @Test
    public void testSendReportToRules() throws Exception {
        //data set
        String reportAsString = "report";
        String request = "request";
        String pluginToSendResponseThrough = "FLUX";
        String recipient = "FRA";
        String fluxDataFlow = "FLUX";
        String reportGuid = "abc";
        FLUXSalesReportMessage report = new FLUXSalesReportMessage();

        //mock
        mockStatic(JAXBMarshaller.class);
        mockStatic(RulesModuleRequestMapper.class);

        when(JAXBMarshaller.marshallJaxBObjectToString(isA(Report.class))).thenReturn(reportAsString);
        when(configService.getParameter(ParameterKey.FLUX_DATA_FLOW)).thenReturn(fluxDataFlow);
        when(reportHelper.getId(report)).thenReturn(reportGuid);
        when(RulesModuleRequestMapper.createSendSalesReportRequest(eq(reportAsString), eq(reportGuid), eq(recipient), eq(pluginToSendResponseThrough), eq(fluxDataFlow), isA(Date.class))).thenReturn(request);

        //execute
        rulesServiceBean.sendReportToRules(report, recipient, pluginToSendResponseThrough);

        //verify and assert
        verifyStatic();
        JAXBMarshaller.marshallJaxBObjectToString(isA(Report.class));

        verify(configService).getParameter(ParameterKey.FLUX_DATA_FLOW);
        verify(reportHelper).getId(report);
        verify(messageProducer).sendModuleMessage(request, Union.RULES);

        verifyStatic();
        RulesModuleRequestMapper.createSendSalesReportRequest(eq(reportAsString), eq(reportGuid), eq(recipient), eq(pluginToSendResponseThrough), eq(fluxDataFlow), isA(Date.class));

        verifyNoMoreInteractions(messageProducer, reportHelper, responseHelper, configService);
    }

}