package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;

import javax.ejb.Local;
import javax.jms.TextMessage;

@Local
public interface SalesMessageProducer {

    /** Sends a message to a module. The module gets 60 seconds to answer.
     * @param text the message to be sent
     * @param module the module to which the message needs to be sent
     * @return correlation id
     * @throws MessageException when something goes wrong delivering the message **/
    String sendModuleMessage(String text, Union module) throws MessageException;

    /** Sends a message to a module. The module gets 60 seconds to answer.
     * @param text the message to be sent
     * @param module the module to which the message needs to be sent
     * @param messageSelector the messageSelector message property to facilitate the use of message selector MDBs
     * @return correlation id
     * @throws MessageException when something goes wrong delivering the message **/
    String sendModuleMessage(String text, Union module, String messageSelector) throws MessageException;

    /** Sends a message to a module.
     * @param text the message to be sent
     * @param module the module to which the message needs to be sent
     * @param timeout the max time in milliseconds it can take before aborting
     * @return correlation id
     * @throws MessageException when something goes wrong delivering the message **/
    String sendModuleMessage(String text, Union module, long timeout) throws MessageException;

    /** Sends a message to a module.
     * @param text the message to be sent
     * @param module the module to which the message needs to be sent
     * @param timeout the max time in milliseconds it can take before aborting
     * @param messageSelector the messageSelector message property to facilitate the use of message selector MDBs
     * @return correlation id
     * @throws MessageException when something goes wrong delivering the message **/
    String sendModuleMessage(String text, Union module, long timeout, String messageSelector) throws MessageException;

    void sendModuleErrorMessage(EventMessage message) throws MessageException;

    void sendModuleResponseMessage(TextMessage message, String text) throws MessageException;
}
