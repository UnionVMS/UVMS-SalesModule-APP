package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceBeanHelperTest {

    @InjectMocks
    private AssetServiceBeanHelper helper;

    @Mock
    private SalesMessageProducer messageProducer;

    @Mock
    private SalesMessageConsumer receiver;

    @Mock
    private TextMessage textMessage;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testCallAssetModuleWhenResponseIsInvalid() throws Exception {
        when(messageProducer.sendModuleMessage("request", Union.ASSET, 2500L)).thenReturn("messageId");
        when(receiver.getMessage("messageId", TextMessage.class, 2500L)).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn("OOH INVALID XML");

        exception.expect(SalesServiceException.class);
        exception.expectMessage("Could not parse the response from the the Asset Module. The response was OOH INVALID XML");

        helper.callAssetModule("request", GetAssetModuleResponse.class);

        verify(messageProducer).sendModuleMessage("request", Union.ASSET, 2500L);
        verify(receiver).getMessage("messageId", TextMessage.class, 2500L);
        verify(textMessage).getText();
        verifyNoMoreInteractions(messageProducer, receiver, textMessage);
    }

    @Test
    public void testCallAssetModuleWhenResponseIsInvalidAndJMSIsBehavingFunky() throws Exception {
        when(messageProducer.sendModuleMessage("request", Union.ASSET, 2500L)).thenReturn("messageId");
        when(receiver.getMessage("messageId", TextMessage.class, 2500L)).thenReturn(textMessage);
        when(textMessage.getText()).thenThrow(new JMSException("I'm feeling funky!"));

        exception.expect(SalesServiceException.class);
        exception.expectMessage("Could not parse the response from the the Asset Module.");

        helper.callAssetModule("request", GetAssetModuleResponse.class);

        verify(messageProducer).sendModuleMessage("request", Union.ASSET, 2500L);
        verify(receiver).getMessage("messageId", TextMessage.class, 2500L);
        verify(textMessage).getText();
        verifyNoMoreInteractions(messageProducer, receiver, textMessage);
    }

    @Test
    public void testCallAssetModuleWhenAuditCannotBeContacted() throws Exception {
        when(messageProducer.sendModuleMessage("request", Union.ASSET, 2500L)).thenReturn("messageId");
        when(receiver.getMessage("messageId", TextMessage.class, 2500L)).thenThrow(new MessageException());

        exception.expect(SalesServiceException.class);
        exception.expectMessage("Could not contact the Asset Module");

        helper.callAssetModule("request", GetAssetModuleResponse.class);

        verify(messageProducer).sendModuleMessage("request", Union.ASSET, 2500L);
        verify(receiver).getMessage("messageId", TextMessage.class, 2500L);
        verifyNoMoreInteractions(messageProducer, receiver, textMessage);
    }

    @Test
    public void testCreateAssetListQueryToSearchOnNameOrCFROrIRCS() throws Exception {
        AssetListQuery query = helper.createAssetListQueryToSearchOnNameOrCFROrIRCS("searchString");

        assertEquals(3, query.getAssetSearchCriteria().getCriterias().size());
        assertEquals(ConfigSearchField.NAME, query.getAssetSearchCriteria().getCriterias().get(0).getKey());
        assertEquals("searchString", query.getAssetSearchCriteria().getCriterias().get(0).getValue());
        assertEquals(ConfigSearchField.CFR, query.getAssetSearchCriteria().getCriterias().get(1).getKey());
        assertEquals("searchString", query.getAssetSearchCriteria().getCriterias().get(1).getValue());
        assertEquals(ConfigSearchField.IRCS, query.getAssetSearchCriteria().getCriterias().get(2).getKey());
        assertEquals("searchString", query.getAssetSearchCriteria().getCriterias().get(2).getValue());
    }

    @Test
    public void testCreateAssetListCriteriaPair() throws Exception {
        AssetListCriteriaPair result = helper.createAssetListCriteriaPair("test", ConfigSearchField.ASSET_TYPE);
        assertEquals(ConfigSearchField.ASSET_TYPE, result.getKey());
        assertEquals("test", result.getValue());
    }
}