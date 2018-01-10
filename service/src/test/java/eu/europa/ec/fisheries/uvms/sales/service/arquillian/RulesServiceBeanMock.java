package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RulesServiceBeanMock implements RulesService {

    public static String KEY_SEND_RESPONSE_TO_RULES = "sendResponseToRules";
    public static String KEY_SEND_REPORT_TO_RULES = "sendReportToRules";

    @EJB
    RedeliveryCounterHelper redeliveryCounterHelper;

    @Override
    public void sendResponseToRules(FLUXSalesResponseMessage response, String recipient, String pluginToSendResponseThrough) {
        redeliveryCounterHelper.incrementCounterForKey(KEY_SEND_RESPONSE_TO_RULES);
        throw new SalesServiceException("Forced SalesServiceException. Could not send the sales response to Rules");
    }

    @Override
    public void sendReportToRules(FLUXSalesReportMessage report, String recipient, String pluginToSendResponseThrough) {
        redeliveryCounterHelper.incrementCounterForKey(KEY_SEND_REPORT_TO_RULES);
        throw new SalesServiceException("Forced SalesServiceException. Could not send the sales response to Rules");
    }
}
