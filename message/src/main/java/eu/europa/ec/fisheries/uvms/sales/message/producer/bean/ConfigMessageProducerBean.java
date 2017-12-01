package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.ExtendedAbstractProducer;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.DeliveryMode;
import javax.jms.Queue;

@Stateless
public class ConfigMessageProducerBean extends ExtendedAbstractProducer implements ConfigMessageProducer {

    private static final long TIME_TO_LIVE = 60000L;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendConfigMessage(String configMessage) throws ConfigMessageException {
        try {
            Queue replyToQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
            return sendModuleMessage(configMessage, replyToQueue, TIME_TO_LIVE, DeliveryMode.NON_PERSISTENT);

        } catch (Exception e) {
            throw new ConfigMessageException("Something went wrong sending a config messages from the sales module");
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_CONFIG;
    }

}
