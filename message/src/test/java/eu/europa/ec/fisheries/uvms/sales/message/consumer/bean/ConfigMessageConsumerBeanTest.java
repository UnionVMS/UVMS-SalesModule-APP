package eu.europa.ec.fisheries.uvms.sales.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigMessageConsumerBeanTest {

    @InjectMocks
    private ConfigMessageConsumerBean configMessageConsumerBean;

    @Mock
    private SalesMessageConsumer salesMessageConsumer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetMessageWhenSuccess() throws Exception {
        //data set
        String text = "testMessage";
        String correlationId = "corr";
        Class<String> type = String.class;

        //mock
        when(salesMessageConsumer.getMessage(correlationId, type, 10000L)).thenReturn(text);

        //execute
        String actualResult = configMessageConsumerBean.getConfigMessage(correlationId, type);

        //verify and assert
        verify(salesMessageConsumer).getMessage(correlationId, type, 10000L);
        verifyNoMoreInteractions(salesMessageConsumer);

        assertEquals(text, actualResult);
    }

    @Test
    public void testGetMessageWhenSomethingGoesWrong() throws Exception {
        //data set
        String text = "testMessage";
        String correlationId = "corr";
        Class<String> type = String.class;

        //mock
        when(salesMessageConsumer.getMessage(correlationId, type, 10000L)).thenThrow(new MessageException("oops"));

        expectedException.expect(ConfigMessageException.class);

        //execute
        configMessageConsumerBean.getConfigMessage(correlationId, type);

        //verify and assert
        verify(salesMessageConsumer).getMessage(correlationId, type, 10000L);
        verifyNoMoreInteractions(salesMessageConsumer);
    }
}