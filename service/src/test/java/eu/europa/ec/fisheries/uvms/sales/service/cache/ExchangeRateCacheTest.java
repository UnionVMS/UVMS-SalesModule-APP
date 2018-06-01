package eu.europa.ec.fisheries.uvms.sales.service.cache;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateCacheTest {

    private ExchangeRateCache exchangeRateCache;

    @Before
    public void setUp() throws Exception {
        exchangeRateCache = new ExchangeRateCache();
        exchangeRateCache.init();
    }

    @Test
    public void testExchangeRateCache() {
        String sourceCurrency = "DKK";
        String targetCurrency = "EUR";
        DateTime dateTime = new DateTime(2018,05,30,6,59, DateTimeZone.UTC);
        DateTime dateTimeMinusOneDay = new DateTime(2018,05,29,6,59, DateTimeZone.UTC);
        DateTime dateTimeMinusPlusOneDay = new DateTime(2018,05,31,6,59, DateTimeZone.UTC);
        assertTrue("DKK|30/05/18|EUR".equals(exchangeRateCache.getCacheKey(sourceCurrency, targetCurrency, dateTime))
                || "DKK|5/30/18|EUR".equals(exchangeRateCache.getCacheKey(sourceCurrency, targetCurrency, dateTime)));
        assertFalse(exchangeRateCache.getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime).isPresent());
        assertFalse(exchangeRateCache.getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTimeMinusOneDay).isPresent());
        assertFalse(exchangeRateCache.getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTimeMinusPlusOneDay).isPresent());
        exchangeRateCache.addExchangeRateToCache(sourceCurrency, targetCurrency, dateTime, BigDecimal.ONE);
        Optional<BigDecimal> exchangeRateFromCache = exchangeRateCache.getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTime);
        assertTrue(exchangeRateFromCache.isPresent());
        assertEquals(BigDecimal.ONE, exchangeRateFromCache.get());
        assertFalse(exchangeRateCache.getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTimeMinusOneDay).isPresent());
        assertFalse(exchangeRateCache.getExchangeRateFromCache(sourceCurrency, targetCurrency, dateTimeMinusPlusOneDay).isPresent());
    }

}
