/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.QueryReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.ReportReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface EventService {

    void createReport(@Observes @ReportReceivedEvent EventMessage message);

    void returnError(@Observes @ErrorEvent EventMessage message);

    void executeQuery(@Observes @QueryReceivedEvent EventMessage message) throws ServiceException;
}
