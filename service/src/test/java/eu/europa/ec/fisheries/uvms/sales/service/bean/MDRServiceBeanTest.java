package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.jms.TextMessage;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({JAXBMarshaller.class, MdrModuleMapper.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"})
public class MDRServiceBeanTest {

    private static final long TIMEOUT = 30000;

    @InjectMocks
    private MDRServiceBean mdrServiceBean;

    @Mock
    private MessageConsumer consumer;

    @Mock
    private SalesMessageProducer producer;

    @Mock
    private TextMessage textMessage;

    @Test
    public void findCodeList() throws Exception {
        //data set
        MDRCodeListKey mdrCodeListKey = MDRCodeListKey.FLAG_STATES;
        String mdrRequest = "test";
        String correlationId = "bla";
        String textMessageText = "testyedetest";
        MdrGetCodeListResponse mdrGetCodeListResponse = new MdrGetCodeListResponse();
        List<ObjectRepresentation> expectedDatasets = Lists.newArrayList(new ObjectRepresentation(), new ObjectRepresentation());
        mdrGetCodeListResponse.setDataSets(expectedDatasets);

        //mock
        mockStatic(JAXBMarshaller.class);
        mockStatic(MdrModuleMapper.class);

        when(MdrModuleMapper.createFluxMdrGetCodeListRequest("TERRITORY")).thenReturn(mdrRequest);
        when(producer.sendModuleMessage(mdrRequest, Union.MDR)).thenReturn(correlationId);
        when(consumer.getMessage(correlationId, TextMessage.class, TIMEOUT)).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn(textMessageText);
        when(JAXBMarshaller.unmarshallTextMessage(textMessageText, MdrGetCodeListResponse.class)).thenReturn(mdrGetCodeListResponse);

        //execute
        List<ObjectRepresentation> result = mdrServiceBean.findCodeList(mdrCodeListKey);

        //verify
        verify(producer).sendModuleMessage(mdrRequest, Union.MDR);
        verify(consumer).getMessage(correlationId, TextMessage.class, TIMEOUT);
        verify(textMessage).getText();

        verifyStatic();
        MdrModuleMapper.createFluxMdrGetCodeListRequest("TERRITORY");
        JAXBMarshaller.unmarshallTextMessage(textMessageText, MdrGetCodeListResponse.class);

        assertEquals(expectedDatasets, result);
    }

}