package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetCache;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import javax.ejb.Singleton;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class AssetCacheBean implements AssetCache {

    private Cache<String, Optional<Asset>> cache;

    public AssetCacheBean() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public Optional<Asset> retrieveAssetFromCache(String cfr) {
        checkArgument(!isNullOrEmpty(cfr),"You're trying to retrieve an asset with a null/blank guid");

        Optional<Asset> cachedAsset = cache.getIfPresent(cfr);
        if (cachedAsset == null) {
            return Optional.empty();
        } else if (!cachedAsset.isPresent()) {
            throw new SalesNonBlockingException("No asset found for cfr '" + cfr + "'");
        } else {
            return cachedAsset;
        }
    }


    @Override
    public void cacheMessage(String cfr, Asset asset) {
        checkArgument(!isNullOrEmpty(cfr), "You're trying to add a null or empty key to the AssetCache");
        // Allow null asset here, so when an asset doesn't exist, it's still cached and doesn't use ActiveMQ.

        cache.put(cfr, Optional.ofNullable(asset));
    }
}
