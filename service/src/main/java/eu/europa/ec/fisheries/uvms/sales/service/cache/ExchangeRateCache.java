package eu.europa.ec.fisheries.uvms.sales.service.cache;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class ExchangeRateCache {

    private Cache<String, Optional<BigDecimal>> cache;

    @PostConstruct
    public void init() {
        if (cache != null) {
            return;
        }
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    public Optional<BigDecimal> getExchangeRateFromCache(String sourceCurrency, String targetCurrency, DateTime date) {
        Optional<BigDecimal> cachedExchangeRate = cache.getIfPresent(getCacheKey(sourceCurrency, targetCurrency, date));
        if (cachedExchangeRate == null) {
            return Optional.absent();
        }
        return cachedExchangeRate;
    }

    public void addExchangeRateToCache(String sourceCurrency, String targetCurrency, DateTime date, BigDecimal exchangeRate) {
        cache.put(getCacheKey(sourceCurrency, targetCurrency, date), Optional.fromNullable(exchangeRate));
    }

    public String getCacheKey(String sourceCurrency, String targetCurrency, DateTime date) {
        StringBuilder sb = new StringBuilder();
        sb.append(sourceCurrency).append("|").append(date.toString(DateTimeFormat.shortDate())).append("|").append(targetCurrency);
        return sb.toString();
    }
}



