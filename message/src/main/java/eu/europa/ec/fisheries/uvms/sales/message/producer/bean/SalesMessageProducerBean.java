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
import javax.jms.DeliveryMode;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

@Stateless
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
            rulesMessageProducerBean.sendResponseMessageToSender(originalJMSMessage, messageToBeSent);

        } catch (Exception e) {
            String errorMessage = "[ Error when returning module request. ] " + e.getMessage();
            LOG.error(errorMessage);
            throw new MessageException(errorMessage);
        }
    }

    @Override
    public String sendModuleMessage(String text, Union module, String messageSelector) throws MessageException {
        return sendModuleMessage(text, module, TIME_TO_LIVE, messageSelector);
    }

    @Override
    public String sendModuleMessage(String text, Union module) throws MessageException {
        return sendModuleMessage(text, module, TIME_TO_LIVE, null);
    }

    @Override
    public String sendModuleMessage(String text, Union module, long messageTimeToLiveMillis) throws MessageException {
        return sendModuleMessage(text, module, messageTimeToLiveMillis, null);
    }

    @Override
    public String sendModuleMessage(String text, Union module, long messageTimeToLiveMillis, String messageSelector) throws MessageException {
        try {
            switch (module) {
                case ASSET:
                    return assetMessageProducerBean.sendModuleMessageNonPersistent(text, replyToSalesQueue, messageTimeToLiveMillis);
                case ECB_PROXY:
                    return ecbProxyMessageProducerBean.sendModuleMessageNonPersistent(text, replyToSalesQueue, messageTimeToLiveMillis);
                case RULES:
                    Map<String, String> messageProperties = new HashMap<>();
                    if (messageSelector != null) {
                        messageProperties.put("messageSelector", messageSelector);
                    }
                    return rulesMessageProducerBean.sendModuleMessageWithProps(text, replyToSalesQueue, messageProperties);
                case MDR:
                    return mdrMessageProducerBean.sendModuleMessageNonPersistent(text, replyToSalesQueue, messageTimeToLiveMillis);
                default:
                    throw new UnsupportedOperationException("Sales has no functionality implemented to talk with " + module);
            }

        } catch (Exception e) {
            LOG.error(String.format("[ Error when sending a message to %s. ] {}", module), e.getMessage());
            throw new MessageException(e.getMessage());
        }
    }

    @Override
    public void sendModuleErrorMessage(EventMessage message) throws MessageException {
        try {
            LOG.debug("Sending error message back from Sales module to recipient on JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            rulesMessageProducerBean.sendResponseMessageToSender(message.getJmsMessage(), message.getErrorMessage(), 60000, DeliveryMode.NON_PERSISTENT);
        } catch (Exception e) {
            String errorMessage = "[ Error when returning Error message to recipient. ] " + e.getMessage();
            LOG.error(errorMessage);
            throw new MessageException(errorMessage);
        }
    }

}
