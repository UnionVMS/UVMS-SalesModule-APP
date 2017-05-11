package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesMessage;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeHelperTest {

    private ExchangeHelper exchangeHelper;

    @Mock
    private SalesMessageProducer salesMessageProducer;

    @Before
    public void setUp() throws Exception {
        exchangeHelper = new ExchangeHelper();
    }

    @Test
    public void testSendToExchange() throws Exception {
        String exchangeWrapper = "durum";
        exchangeHelper.sendToExchange(exchangeWrapper, salesMessageProducer);

        verify(salesMessageProducer).sendModuleMessage(exchangeWrapper, Union.EXCHANGE);
        verifyNoMoreInteractions(salesMessageProducer);
    }

    @Test
    public void testWrapInExchangeModel() throws Exception {
        final String message = "pifpoefpaf";
        final String recipient = "BEL";

        SendSalesMessage sendSalesMessage = exchangeHelper.wrapInExchangeModel(message, recipient, ExchangeModuleMethod.SEND_SALES_MESSAGE);

        assertEquals(message, sendSalesMessage.getMessage());
        assertEquals(recipient, sendSalesMessage.getRecipient());
    }

    

}