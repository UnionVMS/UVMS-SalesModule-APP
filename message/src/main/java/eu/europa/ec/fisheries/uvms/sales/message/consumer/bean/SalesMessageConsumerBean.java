/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.SalesMessageConstants;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.helper.bean.JMSConnectorBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.Queue;
import javax.jms.Session;

@Stateless
public class SalesMessageConsumerBean implements SalesMessageConsumer {

    private static final long TIMEOUT = 10000;

    final static Logger LOG = LoggerFactory.getLogger(SalesMessageConsumerBean.class);

    private Queue responseQueue;

    @EJB
    private JMSConnectorBean connector;

    @PostConstruct
    public void init() {
        responseQueue = JMSUtils.lookupQueue(connector.getContext(), SalesMessageConstants.INTERNAL_QUEUE_JNDI);
    }

    @Override
    public <T> T getMessage(String correlationId, Class type) throws MessageException {
        return getMessage(correlationId, type, TIMEOUT);
    }
    @Override
    public <T> T getMessage(String correlationId, Class type, long timeout) throws MessageException {
        try(Session session = connector.getNewSession()) {

            if (correlationId == null || correlationId.isEmpty()) {
                LOG.error("No CorrelationID provided when listening to JMS message, aborting");
                throw new MessageException("No CorrelationID provided!");
            }

            LOG.debug("Sales module created listener and listens to JMS message with CorrelationID: " + correlationId);
            T response = (T) session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'").receive(timeout);

            if (response == null) {
                throw new MessageException("Timeout reached or message null in SalesMessageConsumerBean.");
            }
            return response;

        } catch (Exception e) {
            LOG.error("Error when getting message {}", e.getMessage());
            throw new MessageException("Error when retrieving message: ", e);
        }
    }
}
