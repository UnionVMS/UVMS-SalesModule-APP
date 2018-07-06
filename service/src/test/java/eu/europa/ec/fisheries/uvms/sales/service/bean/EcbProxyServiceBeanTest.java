package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ExchangeRateCache;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.TextMessage;
import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EcbProxyServiceBeanTest {

    private static final long TIMEOUT = 120000;

    @InjectMocks
    EcbProxyServiceBean ecbProxyServiceBean;

    @Mock
    private SalesMessageProducer messageProducer;

    @Mock
    private MessageConsumer receiver;

    @Mock
    private ExchangeRateCache exchangeRateCache;

    @Mock
    private ActiveMQTextMessage textMessage;

    @Mock
    ClientSession session;

    @Before
    @SneakyThrows
    public void setUp() {
        textMessage = new ActiveMQTextMessage(session);
        Whitebox.setInternalState(textMessage, "text", new SimpleString(composeGetExchangeRateResponse()));
        Whitebox.setInternalState(textMessage, "jmsCorrelationID", "MyJMSMessageId");
    }

    @Test
    public void testFindExchangeRateForNotFoundInCacheAndLookupAndAddExchangeRateToCache() throws Exception {
        //data set
        String sourceCurrency = "DKK";
        String targetCurrency = "EUR";
        DateTime dateTime = new DateTime(2018,05,30,6,59, DateTimeZone.UTC);
        Optional<BigDecimal> expectedExchangeRate = Optional.fromNullable(BigDecimal.ONE);

        //mock
        doReturn(Optional.absent()).when(exchangeRateCache).getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);
        doReturn("MyJMSMessageId").when(messageProducer).sendModuleMessage(anyString(), any(Union.class));
        doReturn(textMessage).when(receiver).getMessage("MyJMSMessageId", TextMessage.class, TIMEOUT);
        doNothing().when(exchangeRateCache).addExchangeRateToCache(sourceCurrency, targetCurrency, dateTime, BigDecimal.ONE);

        //execute
        assertEquals(BigDecimal.ONE, ecbProxyServiceBean.findExchangeRate(sourceCurrency, targetCurrency, dateTime));

        //verify and assert
        verify(exchangeRateCache).getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);
        verify(messageProducer).sendModuleMessage(anyString(), any(Union.class));
        verify(receiver).getMessage("MyJMSMessageId", TextMessage.class, TIMEOUT);
        verify(exchangeRateCache).addExchangeRateToCache(sourceCurrency, targetCurrency, dateTime, BigDecimal.ONE);
        verifyNoMoreInteractions(messageProducer, receiver, exchangeRateCache);
    }

    @Test
    public void testFindExchangeRateForFoundInCache() {
        //data set
        String sourceCurrency = "DKK";
        String targetCurrency = "EUR";
        DateTime dateTime = new DateTime(2018,05,30,6,59, DateTimeZone.UTC);
        Optional<BigDecimal> expectedExchangeRate = Optional.fromNullable(BigDecimal.ONE);

        //mock
        doReturn(expectedExchangeRate).when(exchangeRateCache).getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);

        //execute
        assertEquals(BigDecimal.ONE, ecbProxyServiceBean.findExchangeRate(sourceCurrency, targetCurrency, dateTime));

        //verify and assert
        verify(exchangeRateCache).getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);
        verifyNoMoreInteractions(messageProducer, receiver, exchangeRateCache);
    }

    @Test
    public void tryFindExchangeRateForNotFoundInCacheAndLookupExchangeRateReceiveMessageTimeout() throws Exception {
        //data set
        String sourceCurrency = "DKK";
        String targetCurrency = "EUR";
        DateTime dateTime = new DateTime(2018,05,30,6,59, DateTimeZone.UTC);
        Optional<BigDecimal> expectedExchangeRate = Optional.fromNullable(BigDecimal.ONE);

        //mock
        doReturn(Optional.absent()).when(exchangeRateCache).getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);
        doReturn("MyJMSMessageId").when(messageProducer).sendModuleMessage(anyString(), any(Union.class));
        doThrow(new SalesNonBlockingException("MySalesNonBlockingException")).when(receiver).getMessage("MyJMSMessageId", TextMessage.class, TIMEOUT);

        //execute
        try {
            assertEquals(BigDecimal.ONE, ecbProxyServiceBean.findExchangeRate(sourceCurrency, targetCurrency, dateTime));
            fail("Test should fail for SalesNonBlockingException due to lookup exchange rate receive message timeout");

        } catch (SalesNonBlockingException e) {
            assertTrue(e.getMessage().startsWith("Could not convert the currency, because something went wrong contacting the ECB Proxy module. Sent message:"));
        }

        //verify and assert
        verify(exchangeRateCache).getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);
        verify(messageProducer).sendModuleMessage(anyString(), any(Union.class));
        verify(receiver).getMessage("MyJMSMessageId", TextMessage.class, TIMEOUT);

        verifyNoMoreInteractions(messageProducer, receiver, exchangeRateCache);
    }

    public String composeGetExchangeRateResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<getExchangeRateResponse xmlns=\"urn:types.ecb-proxy.sales.schema.fisheries.ec.europa.eu:v1\">\n" +
                "<sourceCurrency>DKK</sourceCurrency>\n" +
                "<targetCurrency>EUR</targetCurrency>\n" +
                "<date>2017-03-05T00:00:00+01:00</date>\n" +
                "<exchangeRate>1</exchangeRate>\n" +
                "</getExchangeRateResponse>";
    }
}
