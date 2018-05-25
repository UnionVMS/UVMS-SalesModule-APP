package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;

import javax.ejb.Local;

/**
 * With this service, you can send messages via Exchange
 */

@Local
public interface OutgoingMessageService {

    /**
     * Forwards a report to the given recipient through the given plugin
     *
     * @param report the message to be forwarded
     * @param recipient the receiver of the message
     * @param plugin the plugin over which the message should be sent
     * @throws SalesServiceException when something goes wrong
     */
    void forwardReport(FLUXSalesReportMessage report, String recipient, String plugin);


    /**
     * Sends a Sales response message to the given recipient through the given plugin
     *
     * @param response the message to be sent to Rules over JMS
     * @param recipient the receiver of the message
     * @param plugin the plugin over which the message should be sent
     * @throws SalesServiceException when something goes wrong
     */
    void sendResponse(FLUXSalesResponseMessage response, String recipient, String plugin);


}
