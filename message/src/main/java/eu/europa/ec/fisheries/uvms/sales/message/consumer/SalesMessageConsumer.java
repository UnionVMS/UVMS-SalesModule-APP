package eu.europa.ec.fisheries.uvms.sales.message.consumer;


import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;

public interface SalesMessageConsumer {
    <T> T getMessage(String correlationId, Class type) throws MessageException;
    <T> T getMessage(String correlationId, Class type, long timeout) throws MessageException;
}
