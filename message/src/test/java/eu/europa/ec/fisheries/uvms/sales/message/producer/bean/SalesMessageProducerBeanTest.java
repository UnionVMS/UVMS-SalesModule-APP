package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.DeliveryMode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesMessageProducerBeanTest {

    @InjectMocks
    private SalesMessageProducerBean salesMessageProducerBean;

    @Mock
    RulesMessageProducerBean rulesMessageProducerBean;

    @Mock
    AssetMessageProducerBean assetMessageProducerBean;

    @Mock
    MDRMessageProducerBean mdrMessageProducerBean;

    @Mock
    ECBProxyMessageProducerBean ecbProxyMessageProducerBean;

    @Test
    public void test() throws Exception {
        //Data
        String text = "MyText";
        long timeout = 30000L;
        String jmsMessageId = "MyJmsMessageId";
        String messageSelector = "MyMessageSelector";
        Map<String, String> messageProperties = new HashMap<>();
        if (messageSelector != null) {
            messageProperties.put("messageSelector", messageSelector);
        }

        //Mock
        doReturn(jmsMessageId).when(rulesMessageProducerBean).sendModuleMessageWithProps(text, null, messageProperties);

        //Execute
        String returnedJMSMessageId = salesMessageProducerBean.sendModuleMessage(text, Union.RULES, timeout, messageSelector);

        //Verify and assert
        verify(rulesMessageProducerBean).sendModuleMessageWithProps(text, null, messageProperties);
        verifyNoMoreInteractions(rulesMessageProducerBean, assetMessageProducerBean, mdrMessageProducerBean, ecbProxyMessageProducerBean);
        assertEquals(jmsMessageId, returnedJMSMessageId);
    }

}
