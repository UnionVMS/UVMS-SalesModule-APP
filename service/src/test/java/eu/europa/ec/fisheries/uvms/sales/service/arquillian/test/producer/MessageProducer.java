package eu.europa.ec.fisheries.uvms.sales.service.arquillian.test.producer;

import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.*;

@Stateless
public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    private ConnectionFactory connectionFactory;
    //private Queue sales;

    @PostConstruct
    public void initialize() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        //sales = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }

    public void sendMessage(String message, Destination destination, String correlationId) {
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);
            textMessage.setJMSCorrelationID(correlationId);

            getProducer(session, destination).send(textMessage);
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] ", e);
            throw new RuntimeException(e);
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(30000L);
        return producer;
    }
}
