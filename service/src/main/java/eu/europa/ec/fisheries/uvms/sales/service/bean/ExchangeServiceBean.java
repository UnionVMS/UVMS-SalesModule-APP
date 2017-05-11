package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesMessage;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ExchangeHelper;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ExchangeServiceBean implements ExchangeService {

    @EJB
    private SalesMessageProducer salesMessageProducer;

    @EJB
    private ExchangeHelper exchangeHelper;

//    @Override
//    public void sendToExchange(FLUXSalesResponseMessage toBeSent, String recipient) throws ServiceException {
//        String marshalledMessage = exchangeHelper.marshallToString(toBeSent);
//        SendSalesMessage sendSalesMessage = exchangeHelper.wrapInExchangeModel(marshalledMessage, recipient, ExchangeModuleMethod.SEND_SALES_RESPONSE);
//        String marshalledExchangeWrapper = exchangeHelper.marshallToString(sendSalesMessage);
//        exchangeHelper.sendToExchange(marshalledExchangeWrapper, salesMessageProducer);
//    }

    @Override
    public void sendToExchange(Object toBeSent, String recipient) throws ServiceException {
        sendToExchange(toBeSent, recipient, ExchangeModuleMethod.SEND_SALES_MESSAGE);
    }

    @Override
    public void sendToExchange(Object toBeSent, String recipient, ExchangeModuleMethod method) throws ServiceException {
        String marshalledMessage = exchangeHelper.marshallToString(toBeSent);
        SendSalesMessage sendSalesMessage = exchangeHelper.wrapInExchangeModel(marshalledMessage, recipient, method);
        String marshalledExchangeWrapper = exchangeHelper.marshallToString(sendSalesMessage);
        exchangeHelper.sendToExchange(marshalledExchangeWrapper, salesMessageProducer);
    }
}
