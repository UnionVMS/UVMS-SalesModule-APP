package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;

import static org.junit.Assert.assertNotNull;

@MessageDriven(mappedName = "java:/jms/queue/UVMSSalesEcbProxy", activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSAssetEvent")
})
public class AssetMessageReceiverMock implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMessageReceiverMock.class);

    private ConnectionFactory connectionFactory;
    private Queue replyToSalesQueue;

    @PostConstruct
    public void initialize() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        replyToSalesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }

    @Override
    public void onMessage(Message message) {
        LOG.info("onMessage");
        TextMessage requestMessage = (TextMessage) message;
        validateMandatoryJMSHeaderProperties(requestMessage);
        sendResponse(requestMessage);
    }

    private void sendResponse(TextMessage requestMessage) {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            assertNotNull(connection);
            session = JMSUtils.connectToQueue(connection);
            assertNotNull(session);

            String messageBody = requestMessage.getText();
            LOG.info("messageBody: " + messageBody);
            String messageToSend = null;
            if (messageBody.contains("GBR000C16061")) {
                messageToSend = getMockedAssetResponse_OK();
            } else {
                messageToSend = getMockedAssetResponse_NOK();
            }

            TextMessage assetFault = session.createTextMessage(messageToSend);
            assetFault.setJMSCorrelationID(requestMessage.getJMSMessageID());
            getProducer(session, requestMessage.getJMSReplyTo()).send(assetFault);

        } catch (Exception e) {
            LOG.error("Unable to send Asset response message. Reason: " + e.getMessage());
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

    private void validateMandatoryJMSHeaderProperties(TextMessage requestMessage) {
        try {
            if (!(requestMessage.getJMSExpiration() > 0)) {
                throw new IllegalArgumentException("Message expiration time is mandatory");
            }
            if (!replyToSalesQueue.equals(requestMessage.getJMSReplyTo())) {
                throw new IllegalArgumentException("Invalid message reply to destination");
            }
        } catch (JMSException e) {
            throw new IllegalArgumentException("Unable to obtain message header property");
        }
    }

    public String getMockedAssetResponse_NOK() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
		       "<ns2:AssetFault xmlns:ns2=\"types.asset.wsdl.fisheries.ec.europa.eu\">\n" +
		       "   <code>1700</code>\n" +
		       "   <fault>Exception when getting asset from source : INTERNAL Error message: No asset found for BEL123456799</fault>\n" +
		       "</ns2:AssetFault>\n";
    }

    public String getMockedAssetResponse_OK() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:GetAssetModuleResponse xmlns:ns2=\"module.asset.wsdl.fisheries.ec.europa.eu\">\n" +
                "    <asset>\n" +
                "        <assetId>\n" +
                "            <type>GUID</type>\n" +
                "            <value>080e6179-68ee-41dd-83c2-a807da3c81a2</value>\n" +
                "            <guid>080e6179-68ee-41dd-83c2-a807da3c81a2</guid>\n" +
                "        </assetId>\n" +
                "        <active>true</active>\n" +
                "        <source>INTERNAL</source>\n" +
                "        <eventHistory>\n" +
                "            <eventId>02ae8bca-1d29-45c8-9ec6-b5e37922a035</eventId>\n" +
                "            <eventCode>MOD</eventCode>\n" +
                "            <eventDate>2017-10-09T12:25:50.196Z</eventDate>\n" +
                "        </eventHistory>\n" +
                "        <name>PIETER</name>\n" +
                "        <countryCode>SWE</countryCode>\n" +
                "        <hasIrcs>Y</hasIrcs>\n" +
                "        <ircs>MXHF6</ircs>\n" +
                "        <externalMarking>PD-657</externalMarking>\n" +
                "        <cfr>GBR000C16061</cfr>\n" +
                "        <imo>1234567</imo>\n" +
                "        <hasLicense>false</hasLicense>\n" +
                "        <grossTonnageUnit>LONDON</grossTonnageUnit>\n" +
                "    </asset>\n" +
                "</ns2:GetAssetModuleResponse>\n";
    }

}
