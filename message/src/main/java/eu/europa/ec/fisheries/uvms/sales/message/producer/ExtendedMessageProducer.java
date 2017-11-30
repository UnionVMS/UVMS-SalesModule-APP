package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageProducer;

import javax.jms.Destination;

public interface ExtendedMessageProducer extends MessageProducer {

    String sendModuleMessage(final String text, final Destination replyTo, final long timeToLiveInMillis, final int jmsDeliveryMode) throws MessageException;

}
