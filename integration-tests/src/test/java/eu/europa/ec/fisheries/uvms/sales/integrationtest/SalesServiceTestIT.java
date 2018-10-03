package eu.europa.ec.fisheries.uvms.sales.integrationtest;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.config.module.v1.PullSettingsResponse;
import eu.europa.ec.fisheries.schema.rules.module.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.deployment.StandardTestDeployment;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.test.factory.SalesTestMessageFactory;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.test.helper.SalesServiceTestHelper;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.ValidationQualityAnalysisMapper;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
import eu.europa.ec.fisheries.uvms.sales.service.EventService;
import eu.europa.ec.fisheries.uvms.sales.service.MDRService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.apache.commons.lang.StringUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.ejb.EJB;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class SalesServiceTestIT extends StandardTestDeployment {

    static final Logger LOG = LoggerFactory.getLogger(SalesServiceTestIT.class);

    @EJB
    private SalesServiceTestHelper salesServiceTestHelper;

    @EJB
    private SalesTestMessageFactory salesTestMessageFactory;

    @EJB
    private EventService eventService;

    @EJB
    private ConfigMessageProducer configMessageProducer;

    @EJB
    private ConfigMessageConsumer configMessageConsumer;

    @EJB
    private AssetService assetService;

    @EJB
    private EcbProxyService ecbProxyService;

    @EJB
    private MDRService mdrService;


    //==================================
    // Sales Message consumer test cases
    //==================================

    //---------------------------------------------------------------------------------
    // Save report with original + Save report with corrected -- Sales message consumer
    //---------------------------------------------------------------------------------

    @InSequence(1)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Save_Report_Original_And_Correction() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testSalesMessageConsumer_Save_Report_Original_And_Correction - Started");
        //wait until config had the chance to sync
        Thread.sleep(30000L);

        testSalesMessageConsumer_Save_Report_Original();

        testSalesMessageConsumer_Save_Report_Corrected();
        LOG.info("testSalesMessageConsumer_Save_Report_Original_And_Correction - Finished");
    }

    //------------------------------------------------------------------------------------
    // Save report with corrected and original not processed yet -- Sales message consumer
    //------------------------------------------------------------------------------------

    @InSequence(2)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Save_Report_Correction_And_Original_Not_Processed_Yet() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testSalesMessageConsumer_Save_Report_Correction_And_Original_Not_Processed_Yet - Started");

        // Report corrected should be saved, even if the original referenced report is not processed yet
        testSalesMessageConsumer_Save_Report_Corrected();
        LOG.info("testSalesMessageConsumer_Save_Report_Correction_And_Original_Not_Processed_Yet - Finished");
    }

    //--------------------------------------
    // Save report -- Sales message consumer
    //--------------------------------------

    @InSequence(3)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Save_Report() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testSalesMessageConsumer_Save_Report - Started");

        // Data
        String messageGuid = "d5da24ff-42b4-5e76-967f-ad97762a0311";
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        String salesReportRequest = salesTestMessageFactory.composeSalesReportRequestAsString(messageGuid, vesselFlagState, landingCountry);

        //Execute, save report for MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, receive FLUXSalesResponseMessage
        TextMessage textMessageSendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNotNull(textMessageSendSalesResponseRequest);
        SendSalesResponseRequest sendSalesResponseRequest = salesServiceTestHelper.getSalesModelBean(textMessageSendSalesResponseRequest.getText(), SendSalesResponseRequest.class);
        FLUXSalesResponseMessage fluxSalesResponseMessage = salesServiceTestHelper.getSalesModelBean(sendSalesResponseRequest.getRequest(), FLUXSalesResponseMessage.class);
        assertEquals(messageGuid, fluxSalesResponseMessage.getFLUXResponseDocument().getReferencedID().getValue());

        // Assert use case, find report by id, should be saved in database
        String findReportByIdRequestMessage = SalesModuleRequestMapper.createFindReportByIdRequest(messageGuid);

        Thread.sleep(5000L);

        // Execute
        String correlationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(findReportByIdRequestMessage, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, find report by Id for MessageConsumerBean should find existing FLUX sales report
        TextMessage textMessageFindReportByIdResponse = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(correlationId);
        assertNotNull(textMessageFindReportByIdResponse);
        FindReportByIdResponse findReportByIdResponse = salesServiceTestHelper.getSalesModelBean(textMessageFindReportByIdResponse.getText(), FindReportByIdResponse.class);
        assertTrue(StringUtils.isNotBlank(findReportByIdResponse.getReport()));
        FLUXSalesReportMessage fluxSalesReportMessage = salesServiceTestHelper.getSalesModelBean(findReportByIdResponse.getReport(), FLUXSalesReportMessage.class);
        assertEquals(messageGuid, fluxSalesReportMessage.getFLUXReportDocument().getIDS().get(0).getValue());
        assertEquals("BEL-SN-2007-7777777", fluxSalesReportMessage.getSalesReports().get(0).getIncludedSalesDocuments().get(0).getIDS().get(0).getValue());
        LOG.info("testSalesMessageConsumer_Save_Report - Finished");
    }

    //------------------------------------------------------------------
    // Save report with bean validation errors -- Sales message consumer
    //------------------------------------------------------------------

    @InSequence(4)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void trySalesMessageConsumer_Save_Report_Hibernate_Validator_ConstraintViolation_Exception() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("trySalesMessageConsumer_Save_Report_Hibernate_Validator_ConstraintViolation_Exception - Started");

        // Data
        String messageGuid = "d5da24ff-42b4-5e76-967f-ad97762a0312";

        // Invalid bean: violating condition for bean: document.fishingActivity.location.countryCode error: size must be between 0 and 3 whereas it has value: BEL_MOD
        String request = salesTestMessageFactory.composeFLUXSalesReportMessageAsString_BAD();

        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

        //Execute, trigger MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert
        // sendSalesResponseRequest
        TextMessage sendSalesResponseRequestMessage = salesServiceTestHelper.receiveMessageFromRulesEventQueue();

        // No SendSalesResponseRequest expected for bean validation error.
        assertNull(sendSalesResponseRequestMessage);


        // Assert case: report should not exist for FindReportReceivedEvent

        // Test data
        FindReportByIdRequest findReportByIdRequest = new FindReportByIdRequest();
        findReportByIdRequest.withMethod(SalesModuleMethod.FIND_REPORT_BY_ID).withId(messageGuid);

        TextMessage findReportByIdRequestMessage = salesServiceTestHelper.getTextMessageWithReplyTo(salesServiceTestHelper.getReplyToRulesQueue());
        assertNotNull(findReportByIdRequestMessage);
        EventMessage eventMessage = new EventMessage(findReportByIdRequest);
        eventMessage.setJmsMessage(findReportByIdRequestMessage);

        // Execute
        eventService.respondToFindReportMessage(eventMessage);
        TextMessage responseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(findReportByIdRequestMessage.getJMSMessageID());

        // Assert
        assertNotNull(responseMessage);
        String responseMessageBody = responseMessage.getText();
        LOG.debug("responseMessageBody: " + responseMessageBody);
        FindReportByIdResponse findReportByIdResponse = JAXBMarshaller.unmarshallString(responseMessageBody, FindReportByIdResponse.class);
        assertFalse(StringUtils.isNotBlank(findReportByIdResponse.getReport()));
        LOG.info("trySalesMessageConsumer_Save_Report_Hibernate_Validator_ConstraintViolation_Exception - Finished");
    }

    //-----------------------------------------------------
    // Respond to invalid message -- Sales message consumer
    //-----------------------------------------------------
    @InSequence(5)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Respond_To_Invalid_Message() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testSalesMessageConsumer_Respond_To_Invalid_Message - Started");

        // Test data
        String messageGuid = "d0c749bf-50d6-479a-b12e-61c2f2d66439";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        String sender = "BEL";
        String businessRuleId = "SALE-L00-00-0000";
        ValidationQualityAnalysisType validationQualityAnalysis = ValidationQualityAnalysisMapper.map(businessRuleId, "L00", "ERR", "Internal error.", new ArrayList<String>());
        String respondToInvalidMessageRequest = SalesModuleRequestMapper.createRespondToInvalidMessageRequest(messageGuid, Lists.newArrayList(validationQualityAnalysis), pluginToSendResponseThrough, sender, SalesIdType.GUID);

        //Execute, trigger MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(respondToInvalidMessageRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert
        // JMS out expected
        TextMessage sendSalesResponseRequestMessage = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        String sendSalesResponseRequestMessageBody = sendSalesResponseRequestMessage.getText();
        assertTrue(sendSalesResponseRequestMessageBody.contains("FLUXSalesResponseMessage"));
        assertTrue(sendSalesResponseRequestMessageBody.contains(messageGuid));
        assertTrue(sendSalesResponseRequestMessageBody.contains("NOK"));
        assertTrue(sendSalesResponseRequestMessageBody.contains(businessRuleId));


        // Assert use case: unique ID should be false for previous respondToInvalidMessageRequest processed OK
        String checkForUniqueIdRequest = SalesModuleRequestMapper.createCheckForUniqueIdRequest(Lists.newArrayList(messageGuid), SalesMessageIdType.SALES_REPORT);

        //Execute, trigger MessageConsumerBean for CheckForUniqueIdRequestMessage
        String checkForUniqueIdCorrelationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(checkForUniqueIdRequest, salesServiceTestHelper.getReplyToRulesQueue());

        //Assert
        TextMessage checkForUniqueIdResponseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(checkForUniqueIdCorrelationId);
        assertNotNull(checkForUniqueIdResponseMessage);
        CheckForUniqueIdResponse checkForUniqueIdResponse = JAXBMarshaller.unmarshallString(checkForUniqueIdResponseMessage.getText(), CheckForUniqueIdResponse.class);
        assertFalse(checkForUniqueIdResponse.isUnique());
        LOG.info("testSalesMessageConsumer_Respond_To_Invalid_Message - Finished");
    }

    //-----------------------------------------------------------
    // Save report with marshall errors -- Sales message consumer
    //-----------------------------------------------------------
    @InSequence(6)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void trySalesMessageConsumer_Save_Report_SalesMarshallException() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("trySalesMessageConsumer_Save_Report_SalesMarshallException - Started");

        // Data
        String salesReportRequest = "BAD_MESSAGE_CONTENT";

        //Execute, save report for MessageConsumerBean
        String jmsCorrelationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, receive error notification message for save report SalesMarshallException
        TextMessage textErrorNotificationResponse = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(jmsCorrelationId);
        assertNotNull(textErrorNotificationResponse);
        assertTrue(textErrorNotificationResponse.getText().contains("Invalid content in message"));

        // Assert, should not receive FLUXSalesResponseMessage for previously save report SalesMarshallException
        TextMessage textMessageSendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNull(textMessageSendSalesResponseRequest);
        LOG.info("trySalesMessageConsumer_Save_Report_SalesMarshallException - Finished");
    }

    //----------------------------------------------------------------------
    // Save report with original takeover document -- Sales message consumer
    //----------------------------------------------------------------------
    @InSequence(7)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Save_Report_Original_TakeOverDocument() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testSalesMessageConsumer_Save_Report_Original_TakeOverDocument - Started");

        // Data
        String messageGuid = "37eb22e6-077d-45ee-a596-2228c3e096e8";

        String request = salesTestMessageFactory.composeFLUXSalesReportMessageOriginalAsString();
        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

        //Execute, save report for MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, receive FLUXSalesResponseMessage
        TextMessage textMessageSendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNotNull(textMessageSendSalesResponseRequest);
        SendSalesResponseRequest sendSalesResponseRequest = salesServiceTestHelper.getSalesModelBean(textMessageSendSalesResponseRequest.getText(), SendSalesResponseRequest.class);
        FLUXSalesResponseMessage fluxSalesResponseMessage = salesServiceTestHelper.getSalesModelBean(sendSalesResponseRequest.getRequest(), FLUXSalesResponseMessage.class);
        assertEquals(messageGuid, fluxSalesResponseMessage.getFLUXResponseDocument().getReferencedID().getValue());


        // Assert use case, find report by id, should be saved in database

        String findReportByIdRequestMessage = SalesModuleRequestMapper.createFindReportByIdRequest(messageGuid);

        Thread.sleep(5000L);

        // Execute
        String correlationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(findReportByIdRequestMessage, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, find report by Id for MessageConsumerBean should find existing FLUX sales report
        TextMessage textMessageFindReportByIdResponse = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(correlationId);
        assertNotNull(textMessageFindReportByIdResponse);
        FindReportByIdResponse findReportByIdResponse = salesServiceTestHelper.getSalesModelBean(textMessageFindReportByIdResponse.getText(), FindReportByIdResponse.class);
        assertTrue(StringUtils.isNotBlank(findReportByIdResponse.getReport()));
        FLUXSalesReportMessage fluxSalesReportMessage = salesServiceTestHelper.getSalesModelBean(findReportByIdResponse.getReport(), FLUXSalesReportMessage.class);
        assertEquals(messageGuid, fluxSalesReportMessage.getFLUXReportDocument().getIDS().get(0).getValue());
        assertEquals("BEL-TOD-37eb22e6-077d-45ee-a596-2228c3e096e8", fluxSalesReportMessage.getSalesReports().get(0).getIncludedSalesDocuments().get(0).getIDS().get(0).getValue());
        LOG.info("testSalesMessageConsumer_Save_Report_Original_TakeOverDocument - Finshed");
    }

    @InSequence(8)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Save_Report_Original_And_Send_To_Other_Party() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testSalesMessageConsumer_Save_Report_Original_And_Send_To_Other_Party - Started");

        testSalesMessageConsumer_Save_Report_And_Send_To_Other_Party();
        LOG.info("testSalesMessageConsumer_Save_Report_Original_And_Send_To_Other_Party - Finished");
    }

    private void testSalesMessageConsumer_Save_Report_Original() throws Exception {
        // Data
        String messageGuid = "63ce0d5d-c313-45a3-986b-3e6fed6fecf2";

        String request = salesTestMessageFactory.composeFLUXSalesReportMessageBeforeCorrectionsAsString();
        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

        //Execute, save report for MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, receive FLUXSalesResponseMessage
        TextMessage textMessageSendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNotNull(textMessageSendSalesResponseRequest);
        SendSalesResponseRequest sendSalesResponseRequest = salesServiceTestHelper.getSalesModelBean(textMessageSendSalesResponseRequest.getText(), SendSalesResponseRequest.class);
        FLUXSalesResponseMessage fluxSalesResponseMessage = salesServiceTestHelper.getSalesModelBean(sendSalesResponseRequest.getRequest(), FLUXSalesResponseMessage.class);
        assertEquals(messageGuid, fluxSalesResponseMessage.getFLUXResponseDocument().getReferencedID().getValue());


        // Assert use case, find report by id, should be saved in database

        String findReportByIdRequestMessage = SalesModuleRequestMapper.createFindReportByIdRequest(messageGuid);

        Thread.sleep(5000L);

        // Execute
        String correlationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(findReportByIdRequestMessage, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, find report by Id for MessageConsumerBean should find existing FLUX sales report
        TextMessage textMessageFindReportByIdResponse = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(correlationId);
        assertNotNull(textMessageFindReportByIdResponse);
        FindReportByIdResponse findReportByIdResponse = salesServiceTestHelper.getSalesModelBean(textMessageFindReportByIdResponse.getText(), FindReportByIdResponse.class);
        assertTrue(StringUtils.isNotBlank(findReportByIdResponse.getReport()));
        FLUXSalesReportMessage fluxSalesReportMessage = salesServiceTestHelper.getSalesModelBean(findReportByIdResponse.getReport(), FLUXSalesReportMessage.class);
        assertEquals(messageGuid, fluxSalesReportMessage.getFLUXReportDocument().getIDS().get(0).getValue());
        assertEquals("BEL-SN-63ce0d5d-c313-45a3-986b-3e6fed6fecf2", fluxSalesReportMessage.getSalesReports().get(0).getIncludedSalesDocuments().get(0).getIDS().get(0).getValue());
    }

    private void testSalesMessageConsumer_Save_Report_And_Send_To_Other_Party() throws Exception {
        // Data
        String messageGuid = "15108560-8226-40ae-953e-df987e220249";

        String request = salesTestMessageFactory.composeFLUXSalesReportMessageSendToOtherPartyAsString();
        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

        // Execute, save report for MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, receive FLUXSalesResponseMessage
        TextMessage textMessageSendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNotNull(textMessageSendSalesResponseRequest);
        SendSalesResponseRequest sendSalesResponseRequest = salesServiceTestHelper.getSalesModelBean(textMessageSendSalesResponseRequest.getText(), SendSalesResponseRequest.class);
        FLUXSalesResponseMessage fluxSalesResponseMessage = salesServiceTestHelper.getSalesModelBean(sendSalesResponseRequest.getRequest(), FLUXSalesResponseMessage.class);
        assertEquals(messageGuid, fluxSalesResponseMessage.getFLUXResponseDocument().getReferencedID().getValue());


        // Receive report sent to other party
        TextMessage textMessageSendSalesReportRequest = salesServiceTestHelper.receiveMessageFromExchangeEventQueue();
        assertNotNull(textMessageSendSalesReportRequest);
        eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest sendSalesReportRequest = salesServiceTestHelper.getSalesModelBean(textMessageSendSalesReportRequest.getText(), eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest.class);
        Report fluxSalesReportMessageToOtherParty = salesServiceTestHelper.getSalesModelBean(sendSalesReportRequest.getReport(), Report.class);
        assertEquals(messageGuid, fluxSalesReportMessageToOtherParty.getFLUXSalesReportMessage().getFLUXReportDocument().getIDS().get(0).getValue());
        assertEquals("NLD", sendSalesReportRequest.getSenderOrReceiver());
    }

    private void testSalesMessageConsumer_Save_Report_Corrected() throws Exception {
        // Data
        String messageGuid = "b5bebb69-7290-4b97-bf8a-d4908681226e";

        String request = salesTestMessageFactory.composeFLUXSalesReportMessageCorrectionsAsString();
        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

        //Execute, save report for MessageConsumerBean
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, receive FLUXSalesResponseMessage
        TextMessage textMessageSendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNotNull(textMessageSendSalesResponseRequest);
        SendSalesResponseRequest sendSalesResponseRequest = salesServiceTestHelper.getSalesModelBean(textMessageSendSalesResponseRequest.getText(), SendSalesResponseRequest.class);
        FLUXSalesResponseMessage fluxSalesResponseMessage = salesServiceTestHelper.getSalesModelBean(sendSalesResponseRequest.getRequest(), FLUXSalesResponseMessage.class);
        assertEquals(messageGuid, fluxSalesResponseMessage.getFLUXResponseDocument().getReferencedID().getValue());


        // Assert use case, find report by id, should be saved in database

        Thread.sleep(5000);

        String findReportByIdRequestMessage = SalesModuleRequestMapper.createFindReportByIdRequest(messageGuid);

        // Execute
        String correlationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(findReportByIdRequestMessage, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert, find report by Id for MessageConsumerBean should find existing FLUX sales report
        TextMessage textMessageFindReportByIdResponse = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(correlationId);
        assertNotNull(textMessageFindReportByIdResponse);
        FindReportByIdResponse findReportByIdResponse = salesServiceTestHelper.getSalesModelBean(textMessageFindReportByIdResponse.getText(), FindReportByIdResponse.class);
        assertTrue(StringUtils.isNotBlank(findReportByIdResponse.getReport()));
        FLUXSalesReportMessage fluxSalesReportMessage = salesServiceTestHelper.getSalesModelBean(findReportByIdResponse.getReport(), FLUXSalesReportMessage.class);
        assertEquals(messageGuid, fluxSalesReportMessage.getFLUXReportDocument().getIDS().get(0).getValue());
        assertEquals("BEL-SN-63ce0d5d-c313-45a3-986b-3e6fed6fecf22", fluxSalesReportMessage.getSalesReports().get(0).getIncludedSalesDocuments().get(0).getIDS().get(0).getValue());
    }


    //==========================================
    // Configuration message consumer test cases
    //==========================================

    //---------------------------------------------------
    // Get configuration - Configuration message consumer
    //---------------------------------------------------
    @InSequence(8)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testConfigurationMessageConsumer_Pull_Settings_Configuration_From_Sales() throws Exception {
        // Execute
        String jmsMessageID = configMessageProducer.sendConfigMessage(salesTestMessageFactory.composePullSettingsRequest());

        // Assert
        TextMessage textMessage = configMessageConsumer.getConfigMessage(jmsMessageID, TextMessage.class);
        assertNotNull(textMessage);
        PullSettingsResponse pullSettingsResponse = eu.europa.ec.fisheries.uvms.config.model.mapper.JAXBMarshaller.unmarshallTextMessage(textMessage, PullSettingsResponse.class);
        assertNotNull(pullSettingsResponse);
        assertFalse(pullSettingsResponse.getSettings().isEmpty());
        assertNotNull(pullSettingsResponse.getStatus());
    }


    //===========================
    // Message service test cases
    //===========================

    //---------------------------------------------
    // MDR service find code list - Message service
    //---------------------------------------------

    @InSequence(9)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testMessageServiceMDR_Find_Code_List() throws Exception {
        // Execute
        List<ObjectRepresentation> codeList = mdrService.findCodeList(MDRCodeListKey.CONVERSION_FACTOR);

        // Assert
        assertEquals(4, codeList.size());
    }

    //------------------------------------------
    // Sales ECB proxy service - Message service
    //------------------------------------------

    @InSequence(10)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testMessageServiceSalesECBProxy_Find_Exchange_Rate() throws Exception {
        // Data
        String sourceCurrency = "DKK";
        String targetCurrency = "EUR";
        DateTime dateTime = new DateTime(2017, 12, 14, 8, 44);

        // Execute
        BigDecimal exchangeRate = ecbProxyService.findExchangeRate(sourceCurrency, targetCurrency, dateTime);

        // Assert
        assertNotNull(exchangeRate);
        assertEquals(BigDecimal.valueOf(1.4321), exchangeRate);
    }

    //--------------------------------
    // Asset service - Message service
    //--------------------------------

    @InSequence(11)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testMessageServiceAsset_Find_By_CFR() throws Exception {
        // Data
        String cfr = "GBR000C16061";

        // Execute
        Asset asset = assetService.findByCFR(cfr);

        // Assert
        assertNotNull(asset);
        assertEquals(cfr, asset.getCfr());
    }


    //==========================
    // Event services test cases
    //==========================

    //--------------------------------
    // Invalid message - Event service
    //--------------------------------

    @InSequence(12)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testEventService_Invalid_Message() throws Exception {
        // Test data
        String messageGuid = "d0c749bf-50d6-479a-b12e-61c2f2d66469";
        RespondToInvalidMessageRequest respondToInvalidMessageRequest = new RespondToInvalidMessageRequest();
        respondToInvalidMessageRequest.setPluginToSendResponseThrough("BELGIAN_SALES");
        respondToInvalidMessageRequest.setSender("BEL");
        respondToInvalidMessageRequest.setMessageGuid("d0c749bf-50d6-479a-b12e-61c2f2d66469");
        respondToInvalidMessageRequest.setTypeOfId(SalesIdType.GUID);

        // Execute
        EventMessage eventMessage = new EventMessage(respondToInvalidMessageRequest);
        eventService.respondToInvalidMessage(eventMessage);

        // Assert
        TextMessage sendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        String sendSalesResponseRequestBody = sendSalesResponseRequest.getText();
        assertTrue(sendSalesResponseRequestBody.contains("SendSalesResponseRequest"));
        assertTrue(sendSalesResponseRequestBody.contains(messageGuid));

        // Assert case: CheckForUniqueIdRequest, unsavedMessage should exist in database
        // Test data
        TextMessage requestMessage = salesServiceTestHelper.getTextMessageWithReplyTo(salesServiceTestHelper.getReplyToRulesQueue());
        assertNotNull(requestMessage);
        CheckForUniqueIdRequest checkForUniqueIdRequest = new CheckForUniqueIdRequest();
        checkForUniqueIdRequest.withMethod(SalesModuleMethod.CHECK_UNIQUE_ID).
                withType(SalesMessageIdType.SALES_REPORT).
                withIds(Lists.newArrayList(messageGuid));
        EventMessage checkForUniqueIdRequestEventMessage = new EventMessage(checkForUniqueIdRequest);
        checkForUniqueIdRequestEventMessage.setJmsMessage(requestMessage);

        // Execute
        eventService.respondToUniqueIdMessage(checkForUniqueIdRequestEventMessage);

        // Assert
        TextMessage responseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(requestMessage.getJMSMessageID());
        String responseMessageBody = responseMessage.getText();
        assertTrue(responseMessageBody.contains("CheckForUniqueIdResponse"));
        assertTrue(responseMessageBody.contains("unique=\"false\""));
    }

    //-----------------------------
    // Return error - Event service
    //-----------------------------

    @InSequence(13)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testEventService_Return_Error() throws Exception {
        // Test data
        TextMessage requestMessage = salesServiceTestHelper.getTextMessageWithReplyTo(salesServiceTestHelper.getReplyToRulesQueue());
        assertNotNull(requestMessage);
        EventMessage eventMessage = new EventMessage(requestMessage, "Invalid content in message: " + requestMessage);
        eventMessage.setJmsMessage(requestMessage);

        // Execute
        eventService.returnError(eventMessage);

        // Assert
        TextMessage responseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(requestMessage.getJMSMessageID());
        assertNotNull(responseMessage);
        String responseMessageBody = responseMessage.getText();
        assertTrue(responseMessageBody.contains("Invalid content in message"));
        assertTrue(responseMessageBody.contains(requestMessage.getJMSMessageID()));
    }

    //--------------------------
    // Unique id - Event service
    //--------------------------

    @InSequence(14)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testEventService_UniqueId() throws Exception {
        // Test data
        TextMessage requestMessage = salesServiceTestHelper.getTextMessageWithReplyTo(salesServiceTestHelper.getReplyToRulesQueue());
        assertNotNull(requestMessage);
        String messageGuid = "d5da24ff-42b4-5e76-967f-ad97762a0313";
        CheckForUniqueIdRequest checkForUniqueIdRequest = new CheckForUniqueIdRequest();
        checkForUniqueIdRequest.withMethod(SalesModuleMethod.CHECK_UNIQUE_ID).
                withType(SalesMessageIdType.SALES_REPORT).
                withIds(Lists.newArrayList(messageGuid));
        EventMessage checkForUniqueIdRequestEventMessage = new EventMessage(checkForUniqueIdRequest);
        checkForUniqueIdRequestEventMessage.setJmsMessage(requestMessage);

        // Execute
        eventService.respondToUniqueIdMessage(checkForUniqueIdRequestEventMessage);

        // Assert
        TextMessage responseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(requestMessage.getJMSMessageID());
        assertNotNull(responseMessage);
        String responseMessageBody = responseMessage.getText();
        assertTrue(responseMessageBody.contains("CheckForUniqueIdResponse"));
        assertTrue(responseMessageBody.contains("unique=\"true\""));
    }

    //------------------------------
    // Create report - Event service
    //------------------------------

    @InSequence(15)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testEventService_Create_Report() throws Exception {
        LOG.info("----------------------------------------------------------------------------------------------------");
        LOG.info("testEventService_Create_Report - Started");

        // Test data
        String messageGuid = "d5da24ff-42b4-5e76-967f-ad97762a0314";
        SalesReportRequest salesReportRequest = new SalesReportRequest();
        salesReportRequest.setPluginToSendResponseThrough("BELGIAN_SALES");
        String vesselFlagState = "BEL";
        String landingCountry = "BEL";
        salesReportRequest.setReport(salesTestMessageFactory.composeFLUXSalesReportMessageAsString(messageGuid, vesselFlagState, landingCountry));
        salesReportRequest.setMessageValidationStatus("OK");
        EventMessage eventMessage2 = new EventMessage(salesReportRequest);

        // Execute
        eventService.createReport(eventMessage2);

        // Assert
        TextMessage sendSalesResponseRequest = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNotNull(sendSalesResponseRequest);
        String sendSalesResponseRequestBody = sendSalesResponseRequest.getText();
        LOG.info("sendSalesResponseRequestBody: " + sendSalesResponseRequestBody);
        assertTrue(sendSalesResponseRequestBody.contains("SendSalesResponseRequest"));
        assertTrue(sendSalesResponseRequestBody.contains(messageGuid));


        // Assert case: Find report by id should exist for previously saved report

        // Test data
        FindReportByIdRequest findReportByIdRequest = new FindReportByIdRequest();
        findReportByIdRequest.withMethod(SalesModuleMethod.FIND_REPORT_BY_ID).withId(messageGuid);
        TextMessage findReportByIdRequestMessage = salesServiceTestHelper.getTextMessageWithReplyTo(salesServiceTestHelper.getReplyToRulesQueue());
        assertNotNull(findReportByIdRequestMessage);
        EventMessage eventMessage = new EventMessage(findReportByIdRequest);
        eventMessage.setJmsMessage(findReportByIdRequestMessage);

        Thread.sleep(5000);

        // Execute
        eventService.respondToFindReportMessage(eventMessage);
        TextMessage responseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(findReportByIdRequestMessage.getJMSMessageID());

        // Assert
        assertNotNull(responseMessage);
        String responseMessageBody = responseMessage.getText();
        FindReportByIdResponse findReportByIdResponse = JAXBMarshaller.unmarshallString(responseMessageBody, FindReportByIdResponse.class);
        assertTrue(StringUtils.isNotBlank(findReportByIdResponse.getReport()));
        FLUXSalesReportMessage fluxSalesReportMessage = JAXBMarshaller.unmarshallString(findReportByIdResponse.getReport(), FLUXSalesReportMessage.class);
        assertNotNull(fluxSalesReportMessage);
        assertEquals("BEL-SN-2007-7777777", fluxSalesReportMessage.getSalesReports().get(0).getIncludedSalesDocuments().get(0).getIDS().get(0).getValue());
        assertEquals(messageGuid, fluxSalesReportMessage.getFLUXReportDocument().getIDS().get(0).getValue());
        LOG.info("testEventService_Create_Report - Finished");
    }

    //----------------------------
    // Find report - Event service
    //----------------------------

    @InSequence(16)
    @Test
    @OperateOnDeployment("salesservice")
    @Transactional(TransactionMode.COMMIT)
    @DataSource("java:/jdbc/uvms_sales")
    public void testEventService_Find_Report_For_NonExistingReport() throws Exception {
        String noneExistingMessageGuid = "MyNoneExistingMessageId";
        FindReportByIdRequest findReportByIdRequest = new FindReportByIdRequest();
        findReportByIdRequest.withMethod(SalesModuleMethod.FIND_REPORT_BY_ID).withId(noneExistingMessageGuid);
        TextMessage findReportByIdRequestMessage = salesServiceTestHelper.getTextMessageWithReplyTo(salesServiceTestHelper.getReplyToRulesQueue());
        assertNotNull(findReportByIdRequestMessage);
        EventMessage eventMessage = new EventMessage(findReportByIdRequest);
        eventMessage.setJmsMessage(findReportByIdRequestMessage);

        // Execute
        eventService.respondToFindReportMessage(eventMessage);
        TextMessage responseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(findReportByIdRequestMessage.getJMSMessageID());

        // Assert
        assertNotNull(responseMessage);
        String responseMessageBody = responseMessage.getText();
        FindReportByIdResponse findReportByIdResponse = JAXBMarshaller.unmarshallString(responseMessageBody, FindReportByIdResponse.class);
        assertNotNull(findReportByIdResponse);
        assertTrue(StringUtils.isBlank(findReportByIdResponse.getReport()));
    }
}
