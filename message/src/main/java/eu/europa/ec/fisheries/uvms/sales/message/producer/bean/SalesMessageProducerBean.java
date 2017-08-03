/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import eu.europa.ec.fisheries.uvms.message.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.SalesMessageConstants;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.message.helper.bean.JMSConnectorBean;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.*;
import javax.naming.InitialContext;

@Stateless
public class SalesMessageProducerBean implements SalesMessageProducer {

    private final static Logger LOG = LoggerFactory.getLogger(SalesMessageProducerBean.class);
    private static final long TIME_TO_LIVE = 60000L;

    private Queue salesQueue;
    private Queue assetQueue;
    private Queue ecbProxyQueue;
    private Queue configQueue;
    private Queue rulesQueue;
    private Queue mdrQueue;

    @EJB
    private JMSConnectorBean connector;

    @PostConstruct
    public void init() {
        InitialContext ctx;
        try {
            ctx = new InitialContext();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get InitialContext", e);
        }

        this.assetQueue = JMSUtils.lookupQueue(ctx, MessageConstants.QUEUE_ASSET_EVENT);
        this.salesQueue = JMSUtils.lookupQueue(ctx, SalesMessageConstants.INTERNAL_QUEUE_JNDI);
        this.ecbProxyQueue = JMSUtils.lookupQueue(ctx, SalesMessageConstants.QUEUE_ECB_PROXY);
        this.configQueue = JMSUtils.lookupQueue(ctx, MessageConstants.QUEUE_CONFIG);
        this.rulesQueue = JMSUtils.lookupQueue(ctx, MessageConstants.QUEUE_MODULE_RULES);
        this.mdrQueue = JMSUtils.lookupQueue(ctx, MessageConstants.QUEUE_MDR_EVENT);
    }

    @Override
    public String sendModuleMessage(String text, Union module) throws MessageException {
        try {
            Session session = connector.getNewSession();
            TextMessage jmsMessage = createJMSMessage(text, session);

            switch (module) {
                case ASSET:
                    getProducer(session, assetQueue).send(jmsMessage);
                    break;
                case ECB_PROXY:
                    getProducer(session, ecbProxyQueue).send(jmsMessage);
                    break;
                case CONFIG:
                    getProducer(session, configQueue).send(jmsMessage);
                    break;
                case RULES:
                    getProducer(session, rulesQueue).send(jmsMessage);
                    break;
                case MDR:
                    getProducer(session, mdrQueue).send(jmsMessage);
                    break;
                default:
                    throw new UnsupportedOperationException("Sales has no functionality implemented to talk with " + module);
            }

            return jmsMessage.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending a message to " + module+ ". ] {}", e.getMessage());
            throw new MessageException(e.getMessage());
        }
    }

    @Override
    public void sendModuleErrorMessage(EventMessage message) {
        try {
            Session session = connector.getNewSession();

            LOG.debug("Sending error message back from Sales module to recipient on JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());

            String data = message.getErrorMessage();

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (JMSException e) {
            LOG.error("Error when returning Error message to recipient", e);
        }
    }

    private TextMessage createJMSMessage(String text, Session session) throws JMSException {
        TextMessage jmsMessage = session.createTextMessage();
        jmsMessage.setJMSReplyTo(salesQueue);
        jmsMessage.setText(text);
        return jmsMessage;
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        producer.setTimeToLive(TIME_TO_LIVE);
        return producer;
    }

}
