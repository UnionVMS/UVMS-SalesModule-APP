/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.InvalidMessageReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.QueryReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.ReportReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface EventService {

    /**
     * Persist a received report, and send back a response. When needed, forward the report.
     * @param event the event received event
     */
    void createReport(@Observes @ReportReceivedEvent EventMessage event);

    /**
     * Execute the received query, and send back a response.
     * @param event the event received event
     */
    void executeQuery(@Observes @QueryReceivedEvent EventMessage event) throws ServiceException;

    /**
     * Create a response for a received message that is deemed invalid (by the Rules component, for example)
     * @param event the event received event
     */
    void respondToInvalidMessage(@Observes @InvalidMessageReceivedEvent EventMessage event) throws ServiceException;

    /**
     * Sends back an error message over the queue
     * @param event
     */
    void returnError(@Observes @ErrorEvent EventMessage event);
}
