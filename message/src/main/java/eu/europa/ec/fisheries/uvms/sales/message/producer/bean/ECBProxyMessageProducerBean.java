package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.sales.message.producer.ExtendedAbstractProducer;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Slf4j
@Stateless
@LocalBean
public class ECBProxyMessageProducerBean extends ExtendedAbstractProducer {

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_ECB_PROXY;
    }

}
