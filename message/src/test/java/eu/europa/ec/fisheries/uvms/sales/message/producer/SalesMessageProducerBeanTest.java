package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.helper.bean.JMSConnectorBean;
import eu.europa.ec.fisheries.uvms.sales.message.producer.bean.SalesMessageProducerBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.DeliveryMode;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesMessageProducerBeanTest {

    @InjectMocks
    private SalesMessageProducerBean messageProducerBean;

    @Mock
    private Queue salesQueue;

    @Mock
    private Queue assetQueue;

    @Mock
    private Queue ecbProxyQueue;

    @Mock
    private Queue configQueue;

    @Mock
    private JMSConnectorBean connector;

    @Mock
    private Session session;

    @Mock
    private TextMessage jmsMessage;

    @Mock
    private javax.jms.MessageProducer producer;

    @Test
    public void sendModuleMessageWhenContactingAsset() throws Exception {
        //data set
        String text = "testMessage";
        String expectedCorrelationId = "corr";

        //mock
        when(connector.getNewSession()).thenReturn(session);
        when(session.createTextMessage()).thenReturn(jmsMessage);
        when(session.createProducer(assetQueue)).thenReturn(producer);
        when(jmsMessage.getJMSMessageID()).thenReturn(expectedCorrelationId);

        //execute
        String actualCorrelationId = messageProducerBean.sendModuleMessage(text, Union.ASSET);

        //verify and assert
        verify(connector).getNewSession();
        verify(session).createTextMessage();
        verify(jmsMessage).setJMSReplyTo(salesQueue);
        verify(jmsMessage).setText(text);
        verify(session).createProducer(assetQueue);
        verify(producer).setDeliveryMode(DeliveryMode.PERSISTENT);
        verify(producer).setTimeToLive(60000L);
        verify(producer).send(jmsMessage);
        verify(jmsMessage).getJMSMessageID();

        verifyNoMoreInteractions(salesQueue, assetQueue, connector, session, jmsMessage, producer);

        assertEquals(expectedCorrelationId, actualCorrelationId);
    }

    @Test
    public void sendModuleMessageWhenContactingEcbProxy() throws Exception {
        //data set
        String text = "testMessage";
        String expectedCorrelationId = "corr";

        //mock
        when(connector.getNewSession()).thenReturn(session);
        when(session.createTextMessage()).thenReturn(jmsMessage);
        when(session.createProducer(ecbProxyQueue)).thenReturn(producer);
        when(jmsMessage.getJMSMessageID()).thenReturn(expectedCorrelationId);

        //execute
        String actualCorrelationId = messageProducerBean.sendModuleMessage(text, Union.ECB_PROXY);

        //verify and assert
        verify(connector).getNewSession();
        verify(session).createTextMessage();
        verify(jmsMessage).setJMSReplyTo(salesQueue);
        verify(jmsMessage).setText(text);
        verify(session).createProducer(ecbProxyQueue);
        verify(producer).setDeliveryMode(DeliveryMode.PERSISTENT);
        verify(producer).setTimeToLive(60000L);
        verify(producer).send(jmsMessage);
        verify(jmsMessage).getJMSMessageID();

        verifyNoMoreInteractions(salesQueue, ecbProxyQueue, connector, session, jmsMessage, producer);

        assertEquals(expectedCorrelationId, actualCorrelationId);
    }

    @Test
    public void sendModuleMessageWhenContactingConfig() throws Exception {
        //data set
        String text = "testMessage";
        String expectedCorrelationId = "corr";

        //mock
        when(connector.getNewSession()).thenReturn(session);
        when(session.createTextMessage()).thenReturn(jmsMessage);
        when(session.createProducer(configQueue)).thenReturn(producer);
        when(jmsMessage.getJMSMessageID()).thenReturn(expectedCorrelationId);

        //execute
        String actualCorrelationId = messageProducerBean.sendModuleMessage(text, Union.CONFIG);

        //verify and assert
        verify(connector).getNewSession();
        verify(session).createTextMessage();
        verify(jmsMessage).setJMSReplyTo(salesQueue);
        verify(jmsMessage).setText(text);
        verify(session).createProducer(configQueue);
        verify(producer).setDeliveryMode(DeliveryMode.PERSISTENT);
        verify(producer).setTimeToLive(60000L);
        verify(producer).send(jmsMessage);
        verify(jmsMessage).getJMSMessageID();

        verifyNoMoreInteractions(salesQueue, configQueue, connector, session, jmsMessage, producer);

        assertEquals(expectedCorrelationId, actualCorrelationId);
    }

}