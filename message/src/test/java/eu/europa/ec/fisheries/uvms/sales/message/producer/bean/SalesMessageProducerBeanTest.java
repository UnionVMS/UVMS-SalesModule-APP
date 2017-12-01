package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.DeliveryMode;

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

        //Mock
        doReturn(jmsMessageId).when(rulesMessageProducerBean).sendModuleMessage(text, null, timeout, DeliveryMode.NON_PERSISTENT);

        //Execute
        String returnedJMSMessageId = salesMessageProducerBean.sendModuleMessage(text, Union.RULES, timeout);

        //Verify and assert
        verify(rulesMessageProducerBean).sendModuleMessage(text, null, timeout, DeliveryMode.NON_PERSISTENT);
        verifyNoMoreInteractions(rulesMessageProducerBean, assetMessageProducerBean, mdrMessageProducerBean, ecbProxyMessageProducerBean);
        assertEquals(jmsMessageId, returnedJMSMessageId);
    }

}
