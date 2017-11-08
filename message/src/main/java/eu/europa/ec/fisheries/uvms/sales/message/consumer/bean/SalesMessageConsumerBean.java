/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;

import static com.google.common.base.Preconditions.checkNotNull;

@Stateless
public class SalesMessageConsumerBean implements SalesMessageConsumer {

    private static final long TIMEOUT = 30000;

    static final Logger LOG = LoggerFactory.getLogger(SalesMessageConsumerBean.class);

    private ConnectionFactory connectionFactory;
    private Queue responseQueue;

    @PostConstruct
    public void init() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        responseQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }

    @Override
    public <T> T getMessage(String correlationId, Class type) throws MessageException {
        return getMessage(correlationId, type, TIMEOUT);
    }

    @Override
    public <T> T getMessage(String correlationId, Class type, long timeout) throws MessageException {
        checkNotNull(correlationId);

        Connection connection=null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            LOG.debug("Sales module created listener and listens to JMS message with CorrelationID: " + correlationId);
            T response = (T) session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'").receive(timeout);
            if (response == null) {
                throw new MessageException("[ Timeout reached or message null in SalesMessageConsumerBean. ]");
            }

            return response;

        } catch (Exception e) {
            throw new MessageException("Error when retrieving message: ", e);

        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }
}
