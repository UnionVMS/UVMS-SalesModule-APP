package eu.europa.ec.fisheries.uvms.sales.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigMessageProducerBeanTest {

    @InjectMocks
    private ConfigMessageProducerBean configMessageProducerBean;

    @Mock
    private SalesMessageProducer salesMessageProducer;

    @Test
    public void sendConfigMessageWhenSuccess() throws Exception {
        //data set
        String text = "testMessage";
        String expectedCorrelationId = "corr";

        //mock
        when(salesMessageProducer.sendModuleMessage(text, Union.CONFIG)).thenReturn(expectedCorrelationId);

        //execute
        String actualCorrelationId = configMessageProducerBean.sendConfigMessage(text);

        //verify and assert
        verify(salesMessageProducer).sendModuleMessage(text, Union.CONFIG);
        verifyNoMoreInteractions(salesMessageProducer);

        assertEquals(expectedCorrelationId, actualCorrelationId);
    }

    @Test(expected = ConfigMessageException.class)
    public void sendConfigMessageWhenSomethingGoesWrong() throws Exception {
        //data set
        String text = "testMessage";

        //mock
        when(salesMessageProducer.sendModuleMessage(text, Union.CONFIG)).thenThrow(new MessageException("oops"));

        //execute
        configMessageProducerBean.sendConfigMessage(text);

        //verify and assert
        verify(salesMessageProducer).sendModuleMessage(text, Union.CONFIG);
        verifyNoMoreInteractions(salesMessageProducer);
    }

}