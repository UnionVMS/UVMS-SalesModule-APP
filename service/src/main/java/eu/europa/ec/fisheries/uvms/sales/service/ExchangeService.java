package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import javax.ejb.Local;

/**
 * With this service, you can communicate send messages via Exchange
 */
@Local
public interface ExchangeService {

    /**
     * Sends a response message to exchange
     *
     * @param toBeSent the message to be sent to Exchange over JMS
     * @throws ServiceException when something goes wrong
     */
    void sendToExchange(Object toBeSent, String recipient, ExchangeModuleMethod method) throws ServiceException;

    /**
     * Sends a response message to exchange
     *
     * @param toBeSent the message to be sent to Exchange over JMS
     * @throws ServiceException when something goes wrong
     */
    void sendToExchange(Object toBeSent, String recipient) throws ServiceException;
}