package eu.europa.ec.fisheries.uvms.sales.message.producer;

import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;

import javax.ejb.Local;

@Local
public interface SalesMessageProducer {

    /** Sends a message to a module.
     * @param text the message to be sent
     * @param module the module to which the message needs to be sent
     * @return correlation id
     * @throws MessageException when something goes wrong delivering the message **/
    String sendModuleMessage(String text, Union module) throws MessageException;

    void sendModuleErrorMessage(EventMessage message);
}
