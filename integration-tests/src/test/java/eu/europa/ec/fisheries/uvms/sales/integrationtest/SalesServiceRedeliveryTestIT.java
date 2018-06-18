package eu.europa.ec.fisheries.uvms.sales.integrationtest;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.CheckForUniqueIdResponse;
import eu.europa.ec.fisheries.schema.sales.SalesIdType;
import eu.europa.ec.fisheries.schema.sales.SalesMessageIdType;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.alternative.bean.SetTransactionRollbackOutgoingMessageServiceAlternativeBean;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.deployment.MessageRedeliveryTestDeployment;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.test.factory.SalesTestMessageFactory;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.test.helper.SalesServiceTestHelper;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.test.state.MessageRedeliveryCounter;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.ValidationQualityAnalysisMapper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class SalesServiceRedeliveryTestIT extends MessageRedeliveryTestDeployment {

    static final Logger LOG = LoggerFactory.getLogger(SalesServiceRedeliveryTestIT.class);

    @EJB
    SalesServiceTestHelper salesServiceTestHelper;

    @EJB
    SalesTestMessageFactory salesTestMessageFactory;

    @EJB
    MessageRedeliveryCounter messageRedeliveryCounter;


    //=============================================
    // Sales Message consumer redelivery test cases
    //=============================================

    //------------------------------------------------------------------------------
    // Save report (No JMS redelivery retries required) -- Sales message consumer
    //------------------------------------------------------------------------------

    @InSequence(1)
    @Test
    @OperateOnDeployment("salesservice_redelivery")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Save_Report_No_JMS_Redelivery_Retries_Required() throws Exception {
        //wait until config had the chance to sync
        Thread.sleep(10000L);

        // Data
        String request = salesTestMessageFactory.composeFLUXSalesReportMessageNoRedeliveryAsString();
        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

        //Execute, trigger MessageConsumerBean
        messageRedeliveryCounter.resetRedeliveryCounter();
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(salesReportRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert
        // No JMS out messages expected
        TextMessage sendSalesResponseRequestMessage = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNull(sendSalesResponseRequestMessage);

        // Redelivery should not occur
        assertEquals(1L, messageRedeliveryCounter.getCounterValueForKey(SetTransactionRollbackOutgoingMessageServiceAlternativeBean.KEY_SEND_RESPONSE_TO_RULES));
        assertEquals(1L, messageRedeliveryCounter.getCounterValueForKey(SetTransactionRollbackOutgoingMessageServiceAlternativeBean.KEY_SEND_REPORT_TO_EXCHANGE));
    }

    //------------------------------------------------------------------------------
    // Respond to invalid message (JMS redelivery retries required) -- Sales message consumer
    //------------------------------------------------------------------------------

    @InSequence(2)
    @Test
    @OperateOnDeployment("salesservice_redelivery")
    @Transactional(TransactionMode.DISABLED)
    @DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumer_Respond_To_Invalid_Message_And_Redeliver_seven_times() throws Exception {
        //wait until config had the chance to sync
        Thread.sleep(10000L);

        // Test data
        String messageGuid = "d0c749bf-50d6-479a-b12e-61c2f2d66419";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        String sender = "BEL";
        ValidationQualityAnalysisType validationQualityAnalysis = ValidationQualityAnalysisMapper.map("SALE-L00-00-0000", "L00", "ERR", "Internal error.", new ArrayList<String>());
        String respondToInvalidMessageRequest = SalesModuleRequestMapper.createRespondToInvalidMessageRequest(messageGuid, Lists.newArrayList(validationQualityAnalysis), pluginToSendResponseThrough, sender, SalesIdType.FLUXTL_ON);

        //Execute, trigger MessageConsumerBean
        messageRedeliveryCounter.resetRedeliveryCounter();
        salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(respondToInvalidMessageRequest, salesServiceTestHelper.getReplyToRulesQueue());

        // Assert
        // No JMS out messages expected
        TextMessage sendSalesResponseRequestMessage = salesServiceTestHelper.receiveMessageFromRulesEventQueue();
        assertNull(sendSalesResponseRequestMessage);

        // JMS Redelivery 6 + 1
        assertEquals(7L, messageRedeliveryCounter.getCounterValueForKey(SetTransactionRollbackOutgoingMessageServiceAlternativeBean.KEY_SEND_RESPONSE_TO_RULES));


        // Assert use case: unique ID should be true for previous respondToInvalidMessageRequest failed redelivery attempts
        String checkForUniqueIdRequest = SalesModuleRequestMapper.createCheckForUniqueIdRequest(Lists.newArrayList(messageGuid), SalesMessageIdType.SALES_REPORT);

        //Execute, trigger MessageConsumerBean
        String CheckForUniqueIdCorrelationId = null;
        String correlationId = salesServiceTestHelper.sendMessageToSalesMessageConsumerBean(checkForUniqueIdRequest, salesServiceTestHelper.getReplyToRulesQueue());
        TextMessage checkForUniqueIdResponseMessage = salesServiceTestHelper.receiveMessageFromReplyToRulesQueue(correlationId);

        assertNotNull(checkForUniqueIdResponseMessage);
        CheckForUniqueIdResponse checkForUniqueIdResponse = JAXBMarshaller.unmarshallString(checkForUniqueIdResponseMessage.getText(), CheckForUniqueIdResponse.class);
        assertTrue(checkForUniqueIdResponse.isUnique());
    }

}
