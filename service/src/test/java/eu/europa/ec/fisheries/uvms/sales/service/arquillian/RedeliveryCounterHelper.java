package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import javax.ejb.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class RedeliveryCounterHelper {

    final ConcurrentMap<String, AtomicLong> map = new ConcurrentHashMap<String, AtomicLong>();

    public void incrementCounterForKey(String key) {
        map.putIfAbsent(key, new AtomicLong(0L));
        map.get(key).incrementAndGet();
    }

    public long getCounterValueForKey(String key) {
        return map.getOrDefault(key, new AtomicLong(0L)).get();
    }

    public void resetRedeliveryCounter() {
        map.clear();
    }
}
