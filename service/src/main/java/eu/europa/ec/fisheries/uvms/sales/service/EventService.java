/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.message.event.*;
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
    void executeQuery(@Observes @QueryReceivedEvent EventMessage event);

    /**
     * Create a response for a received message that is deemed invalid (by the Rules component, for example)
     * @param event the event received event
     */
    void respondToInvalidMessage(@Observes @InvalidMessageReceivedEvent EventMessage event);

    /**
     * Sends back an error message over the queue
     * @param event
     */
    void returnError(@Observes @ErrorEvent EventMessage event);

    /**
     * Queries the DB for a specific extId and sends back the result via JMS.
     * @param event the incoming event
     */
    void respondToFindReportMessage(@Observes @FindReportReceivedEvent EventMessage event);

    void respondToUniqueIdMessage(@Observes @UniqueIdReceivedEvent EventMessage event);
}
