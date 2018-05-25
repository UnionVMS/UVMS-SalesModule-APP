package eu.europa.ec.fisheries.uvms.sales.integrationtest.alternative.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.sales.integrationtest.test.state.MessageRedeliveryCounter;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.OutgoingMessageService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/** Set transaction rollback rules service alternative bean. To support alternative beans in ejb-jar.xml.
 */
@Stateless
public class SetTransactionRollbackOutgoingMessageServiceAlternativeBean implements OutgoingMessageService {

    public static String KEY_SEND_REPORT_TO_EXCHANGE = "sendReportToExchange";
    public static String KEY_SEND_RESPONSE_TO_RULES = "sendResponseToRules";

    @EJB
    private MessageRedeliveryCounter messageRedeliveryCounter;

    @Override
    public void forwardReport(FLUXSalesReportMessage report, String recipient, String plugin) {
        messageRedeliveryCounter.incrementCounterForKey(KEY_SEND_REPORT_TO_EXCHANGE);
        throw new SalesServiceException("Forced SalesServiceException. Could not send the sales response to Rules");
    }

    @Override
    public void sendResponse(FLUXSalesResponseMessage response, String recipient, String plugin) {
        messageRedeliveryCounter.incrementCounterForKey(KEY_SEND_RESPONSE_TO_RULES);
        throw new SalesServiceException("Forced SalesServiceException. Could not send the sales response to Rules");
    }
}
