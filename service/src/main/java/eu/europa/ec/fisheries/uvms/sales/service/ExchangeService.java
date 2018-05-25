package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;

import javax.ejb.Local;

/**
 * With this service, you can send messages via Exchange
 */

@Local
public interface ExchangeService {
    /**
     * Sends a report message to Exchange
     *
     * @param report the message to be sent to Exchange over JMS
     * @param recipient the receiver of the message
     * @param plugin the plugin over which the message should be sent
     * @throws SalesServiceException when something goes wrong
     */
    void sendReport(FLUXSalesReportMessage report, String recipient, String plugin);

}
