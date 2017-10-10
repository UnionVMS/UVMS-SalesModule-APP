package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXGPResponse;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.ErroneousMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvalidMessageServiceBeanTest {

    @InjectMocks
    private InvalidMessageServiceBean invalidMessageService;

    @Mock
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @Mock
    private ErroneousMessageDomainModel erroneousMessageDomainModel;

    @Mock
    private RulesService rulesService;

    @Test
    public void sendResponseToInvalidIncomingMessage() throws Exception {
        String messageGuid = "extId";
        Collection<ValidationQualityAnalysisType > validationResults = new ArrayList<>();
        String recipient = "rec";
        String plugin = "FLUX";
        FLUXSalesResponseMessage fluxSalesResponseMessage = new FLUXSalesResponseMessage();

        doReturn(fluxSalesResponseMessage).when(fluxSalesResponseMessageFactory).create(messageGuid, validationResults, FLUXGPResponse.NOK.name());

        invalidMessageService.sendResponseToInvalidIncomingMessage(messageGuid, validationResults, recipient, plugin);

        verify(erroneousMessageDomainModel).save(messageGuid);
        verify(fluxSalesResponseMessageFactory).create(messageGuid, validationResults, FLUXGPResponse.NOK.name());
        verify(rulesService).sendResponseToRules(fluxSalesResponseMessage, recipient, plugin);
        verifyNoMoreInteractions(fluxSalesResponseMessageFactory, erroneousMessageDomainModel, rulesService);
    }

}