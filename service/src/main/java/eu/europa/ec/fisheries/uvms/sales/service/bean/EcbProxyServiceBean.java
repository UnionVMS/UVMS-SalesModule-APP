package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.proxy.ecb.types.v1.GetExchangeRateResponse;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.EcbProxyRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.math.BigDecimal;

@Stateless
public class EcbProxyServiceBean implements EcbProxyService {

    @EJB
    private SalesMessageProducer messageProducer;

    @EJB
    private SalesMessageConsumer receiver;

    @Override
    public BigDecimal findExchangeRate(String sourceCurrency, String targetCurrency, DateTime date) {

        String request = null;
        try {
            request = EcbProxyRequestMapper.createGetExchangeRateRequest(sourceCurrency, targetCurrency, date);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Could not create the request for the ECB proxy", e);
        }

        try {
            GetExchangeRateResponse response = callEcbProxy(request, GetExchangeRateResponse.class);
            return response.getExchangeRate();
        } catch (SalesServiceException e) {
            throw new SalesServiceException("Could not convert the currency, because something went wrong contacting the ECB Proxy module. Sent message: " + request, e);
        }

    }

    private <T> T callEcbProxy(String request, Class<T> returnType) {
        try {
            String messageId = messageProducer.sendModuleMessage(request, Union.ECB_PROXY);
            TextMessage responseText = receiver.getMessage(messageId, TextMessage.class);
            return unmarshallTextMessage(responseText, returnType);
        } catch (MessageException e) {
            throw new SalesServiceException("Could not contact the ECB proxy Module", e);
        }
    }

    private <T> T unmarshallTextMessage(TextMessage responseText, Class<T> returnType) {
        try {
            return JAXBMarshaller.unmarshallTextMessage(responseText, returnType);
        } catch (SalesMarshallException e) {
            try {
                throw new SalesServiceException("Could not interpret the response of the ECB proxy. The repsonse was: " + responseText.getText(), e);
            } catch (JMSException e1) {
                throw new SalesServiceException("Could not interpret the response of the ECB proxy.", e);
            }
        }
    }
}