package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.jms.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Slf4j
@Singleton
public class SalesServiceTestHelper {

    private static final long TIMEOUT = 60000;

    private Queue rulesEventQueue;
    private Queue salesEventQueue;
    private Queue replyToRulesQueue;
    private Queue replyToSalesQueue;

    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void setup() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        rulesEventQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_MODULE_RULES);
        salesEventQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES_EVENT);
        replyToRulesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_RULES);
        replyToSalesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }

    public String sendMessageToSalesMessageConsumerBean(String messageToSend, Destination replyToQueue) {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = JMSUtils.connectToQueue(connection);
            TextMessage textMessage = session.createTextMessage(messageToSend);
            textMessage.setJMSReplyTo(replyToQueue);
            getProducer(session, salesEventQueue).send(textMessage);
            return textMessage.getJMSMessageID();

        } catch (Exception e) {
            fail("Test should not fail for consume JMS message exception: " + e.getMessage());
            return null;
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    public TextMessage receiveTextMessage(Destination receiveFromDestination, String correlationId) {
        Connection connection = null;
        Session session = null;
        TextMessage textMessage = null;
        try {
            connection = connectionFactory.createConnection();
            session = JMSUtils.connectToQueue(connection);
            Message receivedMessage = null;
            if (correlationId != null) {
                receivedMessage = session.createConsumer(receiveFromDestination, "JMSCorrelationID='" + correlationId + "'").receive(TIMEOUT);
            } else {
                receivedMessage = session.createConsumer(receiveFromDestination).receive(TIMEOUT);
            }
            if (receivedMessage == null) {
                log.error("Message consumer timeout is reached");
                return null;
            }

            assertTrue((receivedMessage.getJMSExpiration() > 0));

            return (TextMessage) receivedMessage;

        } catch (Exception e) {
            fail("Test should not fail for UniqueIdReceived consumer JMS message exception: " + e.getMessage());
            return null;
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    /**
     * Asynchronous message consumption
     * */
    public TextMessage receiveMessageFromRulesEventQueue() {
        String correlationId = null;
        return receiveTextMessage(rulesEventQueue, correlationId);
    }

    /**
     * Synchronous blocking timeout message consumption
     * */
    public TextMessage receiveMessageFromReplyToRulesQueue(String correlationId) {
        return receiveTextMessage(replyToRulesQueue, correlationId);
    }

    public <T> T getSalesModelBean(String textMessage, Class clazz) throws Exception {
        return JAXBMarshaller.unmarshallString(textMessage, clazz);
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }

}
