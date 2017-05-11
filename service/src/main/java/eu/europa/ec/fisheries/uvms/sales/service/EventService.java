/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.MessageReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.QueryReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;

/**
 *
 * @author jojoha
 */
@Local
public interface EventService {

    public void createReport(@Observes @MessageReceivedEvent EventMessage message);

    public void returnError(@Observes @ErrorEvent EventMessage message);

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void executeQuery(@Observes @QueryReceivedEvent EventMessage message) throws ServiceException;
}
