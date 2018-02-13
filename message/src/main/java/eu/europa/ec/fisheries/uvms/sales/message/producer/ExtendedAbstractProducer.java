package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;
import java.util.Map;

public abstract class ExtendedAbstractProducer extends AbstractProducer  implements ExtendedMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedAbstractProducer.class);

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleResponseMessageLatest(final TextMessage message, final String text) throws MessageException {
        try (Connection connection = getConnectionFactory().createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             javax.jms.MessageProducer producer = session.createProducer(message.getJMSReplyTo())) {

            LOGGER.info("Sending message back to recipient from with correlationId {} on queue: {}",
                    message.getJMSMessageID(), message.getJMSReplyTo());

            final TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());
            producer.send(response);
        } catch (final Exception e) {
            LOGGER.error("[ Error when returning request. ] {} {}", e.getMessage(), e.getStackTrace());
            throw new MessageException("[ Error when sending message. ]", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessageWithProps(final String text, final Destination replyTo, Map<String, String> props, final int jmsDeliveryMode, final long timeToLiveInMillis) throws MessageException {

        try (Connection connection = getConnectionFactory().createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             javax.jms.MessageProducer producer = session.createProducer(getDestination())) {

            LOGGER.info("Sending message with replyTo: [{}]", replyTo);
            LOGGER.debug("Message content : [{}]", text);

            if (connection == null) {
                throw new MessageException("[ Connection or session is null, cannot send message ] ");
            }

            TextMessage message = session.createTextMessage();

            if (MapUtils.isNotEmpty(props)) {
                for (Object o : props.entrySet()) {
                    Map.Entry<String, String> entry = (Map.Entry) o;
                    message.setStringProperty(entry.getKey(), entry.getValue());
                }
            }

            message.setJMSReplyTo(replyTo);
            message.setText(text);

            producer.setDeliveryMode(jmsDeliveryMode);
            producer.setTimeToLive(timeToLiveInMillis);
            producer.send(message);
            LOGGER.info("Message with {} has been successfully sent.", message.getJMSMessageID());
            return message.getJMSMessageID();

        } catch (final Exception e) {
            LOGGER.error("[ Error when sending message. ] {}", e.getMessage());
            throw new MessageException("[ Error when sending message. ]", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessageNonePersistent(final String text, final Destination replyTo, final long timeToLiveInMillis) throws MessageException {
        return sendModuleMessageWithProps(text, replyTo, null, DeliveryMode.NON_PERSISTENT, timeToLiveInMillis);
    }

}
