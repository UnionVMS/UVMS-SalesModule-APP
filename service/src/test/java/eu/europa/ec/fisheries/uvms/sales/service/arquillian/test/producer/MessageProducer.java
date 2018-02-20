package eu.europa.ec.fisheries.uvms.sales.service.arquillian.test.producer;

import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.*;

@Slf4j
@Stateless
public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);
    private static final long TIME_TO_LIVE_30_SECONDS = 30000;
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void initialize() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
    }

    public void sendMessage(String message, Destination destination, String correlationId) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = JMSUtils.connectToQueue(connection);
             javax.jms.MessageProducer producer = session.createProducer(destination)) {

            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);
            textMessage.setJMSCorrelationID(correlationId);

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(TIME_TO_LIVE_30_SECONDS);
            producer.send(textMessage);

        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] ", e);
            throw new RuntimeException(e);
        }
    }

}
