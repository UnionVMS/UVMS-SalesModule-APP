package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SalesMessageProducerBean implements SalesMessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SalesMessageProducerBean.class);
    private static final long TIME_TO_LIVE = 60000L;

    private Queue replyToSalesQueue;

    @EJB
    RulesMessageProducerBean rulesMessageProducerBean;

    @EJB
    AssetMessageProducerBean assetMessageProducerBean;

    @EJB
    MDRMessageProducerBean mdrMessageProducerBean;

    @EJB
    ECBProxyMessageProducerBean ecbProxyMessageProducerBean;

    @PostConstruct
    public void init() {
        this.replyToSalesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }

    @Override
    public void sendModuleResponseMessage(TextMessage originalJMSMessage, String messageToBeSent) throws MessageException {
        try {
            LOG.info("Sending message back to recipient from Sales with correlationId {} on queue: {}", originalJMSMessage.getJMSMessageID(),
                    originalJMSMessage.getJMSReplyTo());
            rulesMessageProducerBean.sendModuleResponseMessage(originalJMSMessage, messageToBeSent, TIME_TO_LIVE, DeliveryMode.NON_PERSISTENT);

        } catch (Exception e) {
            LOG.error("[ Error when returning module request. ] {}", e.getMessage()); //TODO: check error handling
        }
    }

    @Override
    public String sendModuleMessage(String text, Union module) throws MessageException {
        return sendModuleMessage(text, module, TIME_TO_LIVE);
    }

    @Override
    public String sendModuleMessage(String text, Union module, long timeout) throws MessageException {
        try {
            switch (module) {
                case ASSET:
                    return assetMessageProducerBean.sendModuleMessage(text, replyToSalesQueue, timeout, DeliveryMode.NON_PERSISTENT);
                case ECB_PROXY:
                    return ecbProxyMessageProducerBean.sendModuleMessage(text, replyToSalesQueue, timeout, DeliveryMode.NON_PERSISTENT);
                case RULES:
                    return rulesMessageProducerBean.sendModuleMessage(text, replyToSalesQueue, timeout, DeliveryMode.NON_PERSISTENT);
                case RULES_RESPONSE:
// Verify not supported
//                    getProducer(session, rulesQueue, timeout).send(jmsMessage);
                    throw new UnsupportedOperationException("Sales has no functionality implemented to talk with " + module);
                case MDR:
                    return mdrMessageProducerBean.sendModuleMessage(text, replyToSalesQueue, timeout, DeliveryMode.NON_PERSISTENT);
                default:
                    throw new UnsupportedOperationException("Sales has no functionality implemented to talk with " + module);
            }

        } catch (Exception e) {
            LOG.error("[ Error when sending a message to " + module+ ". ] {}", e.getMessage());
            throw new MessageException(e.getMessage());
        }
    }

    @Override
    public void sendModuleErrorMessage(EventMessage message) {
        try {
            LOG.debug("Sending error message back from Sales module to recipient on JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            rulesMessageProducerBean.sendModuleResponseMessage(message.getJmsMessage(), message.getErrorMessage(), TIME_TO_LIVE, DeliveryMode.NON_PERSISTENT);

        } catch (JMSException e) {
            LOG.error("Error when returning Error message to recipient", e);
        }
    }

}
