package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;

import javax.ejb.Local;

/**
 * With this service, you can send messages via Rules
 */
@Local
public interface RulesService {

    /**
     * Sends a response message to Rules
     *
     * @param response the message to be sent to Rules over JMS
     * @param recipient the receiver of the message
     * @param plugin the plugin over which the message should be sent
     * @throws SalesServiceException when something goes wrong
     */
    void sendResponseToRules(FLUXSalesResponseMessage response, String recipient, String plugin);

    /**
     * Sends a report message to Rules
     *
     * @param report the message to be sent to Rules over JMS
     * @param recipient the receiver of the message
     * @param plugin the plugin over which the message should be sent
     * @throws SalesServiceException when something goes wrong
     */
    void sendReportToRules(FLUXSalesReportMessage report, String recipient, String plugin);

}