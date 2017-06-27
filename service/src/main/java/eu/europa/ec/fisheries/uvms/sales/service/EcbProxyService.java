package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import org.joda.time.DateTime;

import javax.ejb.Local;
import java.math.BigDecimal;

/**
 * With this service, you can retrieve data from the ECB Proxy.
 */
@Local
public interface EcbProxyService {

    /**
     * Gets the exchange rate between currency A and B
     *
     * @param sourceCurrency the currency of the source amount
     * @param targetCurrency the currency to which the source amount needs to be converted
     * @param date the date
     * @return the exchange rate
     * @throws SalesServiceException when something goes wrong
     */
    BigDecimal findExchangeRate(String sourceCurrency, String targetCurrency, DateTime date);

}