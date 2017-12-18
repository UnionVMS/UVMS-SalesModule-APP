package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.schema.sales.proxy.ecb.types.v1.EcbProxyBaseRequest;
import eu.europa.ec.fisheries.schema.sales.proxy.ecb.types.v1.GetExchangeRateResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;

@Slf4j
@MessageDriven(mappedName = "java:/jms/queue/UVMSSalesEcbProxy", activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSSalesEcbProxy")
})
public class ProxyMessageReceiverMock implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyMessageReceiverMock.class);

    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void initialize() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
    }

    @Override
    public void onMessage(Message message) {

        TextMessage requestMessage = (TextMessage) message;

        EcbProxyBaseRequest request = null;
        try {
            request = JAXBMarshaller.unmarshallTextMessage(requestMessage, EcbProxyBaseRequest.class);

        } catch (SalesMarshallException e) {
            log.error("Unable to convert request message to EcbProxyBaseRequest. Reason: " + e.getMessage());
            return;
        }

        switch (request.getMethod()) {
            case GET_EXCHANGE_RATE:
                sendResponse(requestMessage);
                break;
            default:
                log.info("Request method is not supported");
                break;
            }
    }

    private void sendResponse(TextMessage requestMessage) {
        Connection connection = null;
        Session session = null;
        try {
            String documentCurrency = "DKK";
            String localCurrency = "EUR";
            BigDecimal exchangeRate = BigDecimal.valueOf(1.4321);
            LocalDate date = new LocalDate(2017, 3, 5);

		    GetExchangeRateResponse getExchangeRateResponse = new GetExchangeRateResponse()
				.withSourceCurrency(documentCurrency)
				.withDate(date.toDateTimeAtStartOfDay())
				.withTargetCurrency(localCurrency)
				.withExchangeRate(exchangeRate);

            String strGetExchangeRateResponse = JAXBMarshaller.marshallJaxBObjectToString(getExchangeRateResponse);

            connection = connectionFactory.createConnection();
            assertNotNull(connection);
            session = JMSUtils.connectToQueue(connection);
            assertNotNull(session);
            TextMessage getExchangeRateResponseMessage = session.createTextMessage(strGetExchangeRateResponse);
            getExchangeRateResponseMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
            getProducer(session, requestMessage.getJMSReplyTo()).send(getExchangeRateResponseMessage);

        } catch (Exception e) {
            log.error("Unable to send GetExchangeRateResponse. Reason: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(30000L);
        return producer;
    }

}
