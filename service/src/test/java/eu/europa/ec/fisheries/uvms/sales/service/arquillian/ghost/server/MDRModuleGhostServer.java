package eu.europa.ec.fisheries.uvms.sales.service.arquillian.ghost.server;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;

import static org.junit.Assert.assertNotNull;

@Slf4j
@MessageDriven(mappedName = "java:/jms/queue/UVMSSalesEcbProxy", activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSMdrEvent")
})
public class MDRModuleGhostServer implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(EcbProxyGhostServer.class);

    private ConnectionFactory connectionFactory;
    private Queue replyToSalesQueue;

    @PostConstruct
    public void initialize() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        replyToSalesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }

    @Override
    public void onMessage(Message message) {
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
            TextMessage mdrGetCodeListResponse = session.createTextMessage(getMockedSettingsResponse());
            mdrGetCodeListResponse.setJMSCorrelationID(requestMessage.getJMSMessageID());
            getProducer(session, requestMessage.getJMSReplyTo()).send(mdrGetCodeListResponse);

        } catch (Exception e) {
            LOG.error("Unable to send MDR response message. Reason: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    private MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        MessageProducer producer = session.createProducer(destination);
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

    public String getMockedSettingsResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:MdrGetCodeListResponse xmlns:ns2=\"http://europa.eu/ec/fisheries/uvms/activity/model/schemas\">\n" +
                "    <method>MDR_CODE_LIST_RESP</method>\n" +
                "    <acronym>FLUX_GP_PURPOSE</acronym>\n" +
                "    <dataSet>\n" +
                "        <field>\n" +
                "            <columnName>id</columnName>\n" +
                "            <columnValue>1000</columnValue>\n" +
                "            <columnDataType>long</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>validity</columnName>\n" +
                "            <columnValue>DateRange(startDate=Sun Jan 01 00:00:00 UTC 1989, endDate=Fri Dec 31 00:00:00 UTC 2100)</columnValue>\n" +
                "            <columnDataType>class eu.europa.ec.fisheries.uvms.domain.DateRange</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>version</columnName>\n" +
                "            <columnValue>1.0</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>code</columnName>\n" +
                "            <columnValue>9</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>description</columnName>\n" +
                "            <columnValue>ORIGINAL</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_CODE_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_EN_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_VERSION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "    </dataSet>\n" +
                "    <dataSet>\n" +
                "        <field>\n" +
                "            <columnName>id</columnName>\n" +
                "            <columnValue>1001</columnValue>\n" +
                "            <columnDataType>long</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>validity</columnName>\n" +
                "            <columnValue>DateRange(startDate=Sun Jan 01 00:00:00 UTC 1989, endDate=Fri Dec 31 00:00:00 UTC 2100)</columnValue>\n" +
                "            <columnDataType>class eu.europa.ec.fisheries.uvms.domain.DateRange</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>version</columnName>\n" +
                "            <columnValue>1.0</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>code</columnName>\n" +
                "            <columnValue>1</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>description</columnName>\n" +
                "            <columnValue>CANCELLATION</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_CODE_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_EN_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_VERSION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "    </dataSet>\n" +
                "    <dataSet>\n" +
                "        <field>\n" +
                "            <columnName>id</columnName>\n" +
                "            <columnValue>1002</columnValue>\n" +
                "            <columnDataType>long</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>validity</columnName>\n" +
                "            <columnValue>DateRange(startDate=Sun Jan 01 00:00:00 UTC 1989, endDate=Fri Dec 31 00:00:00 UTC 2100)</columnValue>\n" +
                "            <columnDataType>class eu.europa.ec.fisheries.uvms.domain.DateRange</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>version</columnName>\n" +
                "            <columnValue>1.0</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>code</columnName>\n" +
                "            <columnValue>5</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>description</columnName>\n" +
                "            <columnValue>REPLACE</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_CODE_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_EN_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_VERSION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "    </dataSet>\n" +
                "    <dataSet>\n" +
                "        <field>\n" +
                "            <columnName>id</columnName>\n" +
                "            <columnValue>1003</columnValue>\n" +
                "            <columnDataType>long</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>validity</columnName>\n" +
                "            <columnValue>DateRange(startDate=Sun Jan 01 00:00:00 UTC 1989, endDate=Fri Dec 31 00:00:00 UTC 2100)</columnValue>\n" +
                "            <columnDataType>class eu.europa.ec.fisheries.uvms.domain.DateRange</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>version</columnName>\n" +
                "            <columnValue>1.0</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>code</columnName>\n" +
                "            <columnValue>3</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>description</columnName>\n" +
                "            <columnValue>DELETE</columnValue>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_CODE_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_EN_DESCRIPTION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <columnName>APP_VERSION_STR</columnName>\n" +
                "            <columnDataType>class java.lang.String</columnDataType>\n" +
                "        </field>\n" +
                "    </dataSet>\n" +
                "    <validation>\n" +
                "        <valid>OK</valid>\n" +
                "        <message>Validation is OK.</message>\n" +
                "    </validation>\n" +
                "</ns2:MdrGetCodeListResponse>\n";
    }

}
