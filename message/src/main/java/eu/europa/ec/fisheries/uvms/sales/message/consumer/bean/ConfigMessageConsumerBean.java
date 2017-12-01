package eu.europa.ec.fisheries.uvms.sales.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConsumer;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ConfigMessageConsumerBean implements ConfigMessageConsumer {

    private static final long TIMEOUT = 10000;

    static final Logger LOG = LoggerFactory.getLogger(ConfigMessageConsumerBean.class);

    @EJB
    private MessageConsumer salesMessageConsumer;

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return salesMessageConsumer.getMessage(correlationId, type, TIMEOUT);
        } catch (MessageException e) {
            LOG.error("Something went wrong retrieving a config messages from the sales module", e);
            throw new ConfigMessageException("Something went wrong retrieving a config messages from the sales module");
        }
    }

}
