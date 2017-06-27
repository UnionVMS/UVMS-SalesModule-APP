package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXGPResponse;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.service.InvalidMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;

@Stateless
public class InvalidMessageServiceBean implements InvalidMessageService {

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private RulesService rulesService;

    @Override
    public void sendResponseToInvalidIncomingMessage(String messageGuid, Collection<ValidationQualityAnalysisType> validationResults,
                                                     String recipient, String plugin) {
        FLUXSalesResponseMessage fluxSalesResponseMessage =
                fluxSalesResponseMessageFactory.create(messageGuid, validationResults, FLUXGPResponse.NOK.name());
        rulesService.sendResponseToRules(fluxSalesResponseMessage, recipient, plugin);
    }

}
