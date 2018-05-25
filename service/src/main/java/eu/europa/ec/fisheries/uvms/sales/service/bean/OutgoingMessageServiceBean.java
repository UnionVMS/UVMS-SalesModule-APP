package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.sales.service.OutgoingMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;

import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class OutgoingMessageServiceBean implements OutgoingMessageService {

    @EJB
    private ExchangeService exchangeService;

    @EJB
    private RulesService rulesService;


    @Override
    public void forwardReport(FLUXSalesReportMessage report, String recipient, String plugin) {
        exchangeService.sendReport(report, recipient, plugin);
    }

    @Override
    public void sendResponse(FLUXSalesResponseMessage response, String recipient, String plugin) {
        rulesService.sendResponseToRules(response, recipient, plugin);
    }
}
