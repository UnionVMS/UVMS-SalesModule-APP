package eu.europa.ec.fisheries.uvms.sales.service.integrationtest.test.helper;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.jms.*;

import static org.junit.Assert.*;

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

        try (Connection connection = connectionFactory.createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             MessageProducer producer = session.createProducer(salesEventQueue)) {

            TextMessage textMessage = session.createTextMessage(messageToSend);
            textMessage.setJMSReplyTo(replyToQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(60000L);
            producer.send(textMessage);
            return textMessage.getJMSMessageID();

        } catch (Exception e) {
            fail("Test should not fail for consume JMS message exception: " + e.getMessage());
            throw new RuntimeException("Unable to send message to sales message consumber bean. Reason: " + e.getMessage());
        }
    }

    public TextMessage getTextMessageWithReplyTo(Destination replyToDestination) {

        try (Connection connection = connectionFactory.createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             MessageProducer producer = session.createProducer(rulesEventQueue)) { // for testing sake

            TextMessage textMessage = session.createTextMessage("Dummy Sales service Arquillian test message");
            textMessage.setJMSReplyTo(replyToDestination);

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(10L);
            producer.send(textMessage);
            return textMessage;

        } catch (Exception e) {
            log.error("Test should not fail for JMS message exception: " + e.getMessage());
            throw new RuntimeException("Unable to get message with replyTo. Reason: " + e.getMessage());
        }
    }

    public TextMessage receiveTextMessage(Destination receiveFromDestination) {
        boolean hasMessageExpirySet = true;
        return receiveTextMessage(receiveFromDestination, hasMessageExpirySet);
    }

    public TextMessage receiveTextMessage(Destination receiveFromDestination, String correlationId) {
        boolean hasMessageExpirySet = true;
        return receiveTextMessage(receiveFromDestination, correlationId, hasMessageExpirySet);
    }

    public TextMessage receiveTextMessageNoMessageExpiry(Destination receiveFromDestination) {
        boolean hasMessageExpirySet = false;
        return receiveTextMessage(receiveFromDestination, hasMessageExpirySet);
    }

    public TextMessage receiveTextMessageNoMessageExpiry(Destination receiveFromDestination, String correlationId) {
        boolean hasMessageExpirySet = false;
        return receiveTextMessage(receiveFromDestination, correlationId, hasMessageExpirySet);
    }

    private TextMessage receiveTextMessage(Destination receiveFromDestination, String correlationId, boolean hasMessageExpirySet) {
        assertNotNull(correlationId);
        try (Connection connection = connectionFactory.createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             MessageConsumer consumer = session.createConsumer(receiveFromDestination, "JMSCorrelationID='" + correlationId + "'")) {

            Message receivedMessage = consumer.receive(TIMEOUT);
            if (receivedMessage == null) {
                log.error("Message consumer timeout is reached");
                return null;
            }

            assertEquals(hasMessageExpirySet, (receivedMessage.getJMSExpiration() > 0));

            return (TextMessage) receivedMessage;

        } catch (Exception e) {
            fail("Test should not fail for UniqueIdReceived consumer JMS message exception: " + e.getMessage());
            return null;
        }
    }

    private TextMessage receiveTextMessage(Destination receiveFromDestination, boolean hasMessageExpirySet) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             MessageConsumer consumer = session.createConsumer(receiveFromDestination)) {

            Message receivedMessage = consumer.receive(TIMEOUT);
            if (receivedMessage == null) {
                log.error("Message consumer timeout is reached");
                return null;
            }

            assertEquals(hasMessageExpirySet, (receivedMessage.getJMSExpiration() > 0));

            return (TextMessage) receivedMessage;

        } catch (Exception e) {
            fail("Test should not fail for UniqueIdReceived consumer JMS message exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * Asynchronous message consumption
     * */
    public TextMessage receiveMessageFromRulesEventQueue() {
        return receiveTextMessageNoMessageExpiry(rulesEventQueue);
    }

    /**
     * Synchronous blocking timeout message consumption
     * */
    public TextMessage receiveMessageFromReplyToRulesQueue(String correlationId) {
        return receiveTextMessageNoMessageExpiry(replyToRulesQueue, correlationId);
    }

    public <T> T getSalesModelBean(String textMessage, Class clazz) throws Exception {
        return JAXBMarshaller.unmarshallString(textMessage, clazz);
    }

    public Destination getReplyToRulesQueue() {
        return replyToRulesQueue;
    }

}
