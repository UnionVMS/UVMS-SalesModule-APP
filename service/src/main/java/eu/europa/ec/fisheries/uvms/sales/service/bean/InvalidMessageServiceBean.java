package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXGPResponse;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.ErroneousMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.InvalidMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;

import static org.apache.commons.lang.StringUtils.isBlank;

@Stateless
public class InvalidMessageServiceBean implements InvalidMessageService {

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private ErroneousMessageDomainModel erroneousMessageDomainModel;

    @EJB
    private RulesService rulesService;

    @Override
    public void sendResponseToInvalidIncomingMessage(String messageGuid, Collection<ValidationQualityAnalysisType> validationResults,
                                                     String recipient, String plugin, String schemeId) {
        erroneousMessageDomainModel.save(messageGuid);

        FLUXSalesResponseMessage fluxSalesResponseMessage;
        if (isBlank(schemeId)) {
            fluxSalesResponseMessage = fluxSalesResponseMessageFactory.create(messageGuid, validationResults, FLUXGPResponse.NOK.name());
        } else {
            fluxSalesResponseMessage = fluxSalesResponseMessageFactory.create(messageGuid, validationResults, FLUXGPResponse.NOK.name(), schemeId);
        }

        rulesService.sendResponseToRules(fluxSalesResponseMessage, recipient, plugin);
    }

}
