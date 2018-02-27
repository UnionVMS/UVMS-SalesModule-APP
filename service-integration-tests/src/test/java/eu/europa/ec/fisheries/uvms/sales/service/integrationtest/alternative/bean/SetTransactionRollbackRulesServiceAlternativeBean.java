package eu.europa.ec.fisheries.uvms.sales.service.integrationtest.alternative.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.integrationtest.test.state.MessageRedeliveryCounter;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/** Set transaction rollback rules service alternative bean. To support alternative beans in ejb-jar.xml.
 */
@Stateless
public class SetTransactionRollbackRulesServiceAlternativeBean implements RulesService {

    public static String KEY_SEND_RESPONSE_TO_RULES = "sendResponseToRules";
    public static String KEY_SEND_REPORT_TO_RULES = "sendReportToRules";

    @EJB
    MessageRedeliveryCounter messageRedeliveryCounter;

    @Override
    public void sendResponseToRules(FLUXSalesResponseMessage response, String recipient, String pluginToSendResponseThrough) {
        messageRedeliveryCounter.incrementCounterForKey(KEY_SEND_RESPONSE_TO_RULES);
        throw new SalesServiceException("Forced SalesServiceException. Could not send the sales response to Rules");
    }

    @Override
    public void sendReportToRules(FLUXSalesReportMessage report, String recipient, String pluginToSendResponseThrough) {
        messageRedeliveryCounter.incrementCounterForKey(KEY_SEND_REPORT_TO_RULES);
        throw new SalesServiceException("Forced SalesServiceException. Could not send the sales response to Rules");
    }
}
