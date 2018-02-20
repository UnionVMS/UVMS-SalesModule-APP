package eu.europa.ec.fisheries.uvms.sales.service.arquillian.ghost.server;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.sales.service.arquillian.test.producer.MessageProducer;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@Slf4j
@MessageDriven(mappedName = MessageConstants.QUEUE_CONFIG, activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSConfigEvent"),
        @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = MessageConstants.QUEUE_CONFIG),
        @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = MessageConstants.CONNECTION_FACTORY)
})
public class ConfigModuleGhostServer implements MessageListener {

    @EJB
    private MessageProducer producer;

    @Override
    public void onMessage(Message message) {
        try {
            log.error("Configuration ghost server received message, sending back message on queue: "
                            + message.getJMSReplyTo() + " with correlationId: " + message.getJMSMessageID());

            producer.sendMessage(PULL_SETTINGS_RESPONSE, message.getJMSReplyTo(), message.getJMSMessageID());

        } catch (JMSException e) {
            log.error("Cannot send response from config.", e);
        }
    }

    private static final String PULL_SETTINGS_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:PullSettingsResponse xmlns:ns2=\"urn:module.config.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <status>OK</status>\n" +
            "    <settings>\n" +
            "        <id>94</id>\n" +
            "        <key>sales.ebc.proxy.endpoint</key>\n" +
            "        <value>http://sdw-wsrest.ecb.europa.eu/service/data/EXR/D..EUR.SP00.A</value>\n" +
            "        <description>The url of the REST service of the European Central Bank, to retrieve currency rates.</description>\n" +
            "        <module>sales</module>\n" +
            "        <global>false</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>95</id>\n" +
            "        <key>currency</key>\n" +
            "        <value>EUR</value>\n" +
            "        <description>The currency of the country where the software is deployed. In ISO 4217 format.</description>\n" +
            "        <module>sales</module>\n" +
            "        <global>false</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>96</id>\n" +
            "        <key>sales.flux_data_flow</key>\n" +
            "        <value>urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2</value>\n" +
            "        <description>The FLUX data flow property</description>\n" +
            "        <module>sales</module>\n" +
            "        <global>false</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>1</id>\n" +
            "        <key>measurementSystem</key>\n" +
            "        <value>metric</value>\n" +
            "        <description>Choise of metric system, typically metric or imperial.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>2</id>\n" +
            "        <key>coordinateFormat</key>\n" +
            "        <value>degreesMinutesSeconds</value>\n" +
            "        <description>Coordinate system.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>3</id>\n" +
            "        <key>dateTimeFormat</key>\n" +
            "        <value>YYYY-MM-DD HH:mm:ss</value>\n" +
            "        <description>Choice of datetime format.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>4</id>\n" +
            "        <key>defaultHomePage</key>\n" +
            "        <value>reporting</value>\n" +
            "        <description>Default home page.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>5</id>\n" +
            "        <key>availableLanguages</key>\n" +
            "        <value>en-gb</value>\n" +
            "        <description>List of available language codes, comma-separated.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>6</id>\n" +
            "        <key>distanceUnit</key>\n" +
            "        <value>nm</value>\n" +
            "        <description>Unit used for distances.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>7</id>\n" +
            "        <key>speedUnit</key>\n" +
            "        <value>kts</value>\n" +
            "        <description>Unit used for speed.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>8</id>\n" +
            "        <key>maxSpeed</key>\n" +
            "        <value>15</value>\n" +
            "        <description>Maximum allowed speed, measured in nautical miles.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>9</id>\n" +
            "        <key>timezone</key>\n" +
            "        <value>0</value>\n" +
            "        <description>Global timezone (offset from UTC in minutes).</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>11</id>\n" +
            "        <key>currency</key>\n" +
            "        <value>EUR</value>\n" +
            "        <description>The currency of the country where the software is deployed. In ISO 4217 format.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "    <settings>\n" +
            "        <id>10</id>\n" +
            "        <key>flux_local_nation_code</key>\n" +
            "        <value>BEL</value>\n" +
            "        <description>The nation code of the country where this instance of UnionVMS is deployed.</description>\n" +
            "        <global>true</global>\n" +
            "    </settings>\n" +
            "</ns2:PullSettingsResponse>\n";
}
