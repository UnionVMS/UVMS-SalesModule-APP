package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ConfigMessageProducerBean implements ConfigMessageProducer {

    static final Logger LOG = LoggerFactory.getLogger(ConfigMessageProducerBean.class);

    @EJB
    private SalesMessageProducer salesMessageProducer;

    @Override
    public String sendConfigMessage(String configMessage) throws ConfigMessageException {
        try {
            return salesMessageProducer.sendModuleMessage(configMessage, Union.CONFIG);
        } catch (MessageException e) {
            LOG.error("Something went wrong sending a config messages from the sales module", e);
            throw new ConfigMessageException("Something went wrong sending a config messages from the sales module");
        }
    }

}
