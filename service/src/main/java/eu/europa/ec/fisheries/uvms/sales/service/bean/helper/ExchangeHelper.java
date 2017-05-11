package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesMessage;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;

import javax.ejb.Stateless;

@Stateless
public class ExchangeHelper {

    public String marshallToString(Object fluxSalesResponseMessage) throws ServiceException {
        try {
            return JAXBMarshaller.marshallJaxBObjectToString(fluxSalesResponseMessage);
        } catch (SalesMarshallException e) {
            throw new ServiceException("Something went wrong during the marshalling to string", e);
        }
    }

    public void sendToExchange(String exchangeWrapper, SalesMessageProducer salesMessageProducer) throws ServiceException {
        try {
            salesMessageProducer.sendModuleMessage(exchangeWrapper, Union.EXCHANGE);
        } catch (MessageException e) {
            throw new ServiceException("Something went wrong when sending a message to Exchange", e);
        }
    }

    public SendSalesMessage wrapInExchangeModel(String marshalledResponse, String recipient, ExchangeModuleMethod method) {
        SendSalesMessage sendSalesMessage = new SendSalesMessage();
        sendSalesMessage.setMethod(method);
        sendSalesMessage.setMessage(marshalledResponse);
        sendSalesMessage.setRecipient(recipient);
        return sendSalesMessage;
    }
}
