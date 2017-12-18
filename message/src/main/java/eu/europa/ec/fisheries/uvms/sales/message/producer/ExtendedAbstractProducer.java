package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

public abstract class ExtendedAbstractProducer extends AbstractProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedAbstractProducer.class);

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(final String text, final Destination replyTo, final long timeToLiveInMillis, final int jmsDeliveryMode) throws MessageException {

        Connection connection = null;

        try {
            connection = getConnectionFactory().createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            LOGGER.info("Sending message with replyTo: [{}]", replyTo);
            LOGGER.debug("Message content : [{}]", text);

            if (connection == null || session == null) {
                throw new MessageException("[ Connection or session is null, cannot send message ] ");
            }

            final TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(replyTo);
            message.setText(text);

            MessageProducer producer = session.createProducer(getDestination());
            producer.setDeliveryMode(jmsDeliveryMode);
            producer.setTimeToLive(timeToLiveInMillis);
            producer.send(message);

            LOGGER.info("Message with {} has been successfully sent.", message.getJMSMessageID());
            return message.getJMSMessageID();

        } catch (final JMSException e) {
            LOGGER.error("[ Error when sending message. ] {}", e.getMessage());
            throw new MessageException("[ Error when sending message. ]", e);
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleResponseMessage(final TextMessage message, final String text, final long timeToLiveInMillis, final int jmsDeliveryMode) {
        Connection connection = null;
        try {
            connection = getConnectionFactory().createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            LOGGER.info("Sending message back to recipient from  with correlationId {} on queue: {}",
                    message.getJMSMessageID(), message.getJMSReplyTo());

            final TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());

            MessageProducer producer = session.createProducer(message.getJMSReplyTo());
            producer.setDeliveryMode(jmsDeliveryMode);
            producer.setTimeToLive(timeToLiveInMillis);
            producer.send(response);

        } catch (final JMSException e) {
            LOGGER.error("[ Error when returning request. ] {} {}", e.getMessage(), e.getStackTrace());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

}
