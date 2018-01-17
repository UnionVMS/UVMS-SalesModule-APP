package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageProducer;

import javax.jms.Destination;
import javax.jms.TextMessage;
import java.util.Map;

public interface ExtendedMessageProducer extends MessageProducer {

    void sendModuleResponseMessageLatest(final TextMessage message, final String text) throws MessageException;

    String sendModuleMessageWithProps(final String text, final Destination replyTo, Map<String, String> props, final int jmsDeliveryMode, final long timeToLiveInMillis) throws MessageException;

    String sendModuleMessageNonePersistent(final String text, final Destination replyTo, final long timeToLiveInMillis) throws MessageException;
}
