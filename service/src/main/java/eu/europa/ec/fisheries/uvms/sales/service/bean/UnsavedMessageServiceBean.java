package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXGPResponse;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.domain.UnsavedMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.UnsavedMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;

import static org.apache.commons.lang.StringUtils.isBlank;

@Stateless
public class UnsavedMessageServiceBean implements UnsavedMessageService {

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private UnsavedMessageDomainModel unsavedMessageDomainModel;

    @EJB
    private RulesService rulesService;

    @Override
    public void sendResponseToInvalidIncomingMessage(String messageGuid, Collection<ValidationQualityAnalysisType> validationResults,
                                                     String recipient, String plugin, String schemeId) {
        unsavedMessageDomainModel.save(messageGuid);

        FLUXSalesResponseMessage fluxSalesResponseMessage;
        if (isBlank(schemeId)) {
            fluxSalesResponseMessage = fluxSalesResponseMessageFactory.create(messageGuid, validationResults, FLUXGPResponse.NOK.name());
        } else {
            fluxSalesResponseMessage = fluxSalesResponseMessageFactory.create(messageGuid, validationResults, FLUXGPResponse.NOK.name(), schemeId);
        }

        rulesService.sendResponseToRules(fluxSalesResponseMessage, recipient, plugin);
    }

}
