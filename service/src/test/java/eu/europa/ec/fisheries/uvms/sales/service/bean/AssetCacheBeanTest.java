package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class AssetCacheBeanTest {

    private static final String ASSET_NULL_KEY = "You're trying to retrieve an asset with a null/blank guid";
    private static final String ASSET_NULL_CACHE = "You're trying to add a null or empty key to the AssetCache";
    private AssetCacheBean cache;

    private Asset asset;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        cache = new AssetCacheBean();
        asset = new Asset();
        asset.setCfr("cfr");
        
        cache.cacheMessage("cfr", asset);
    }

    @Test
    public void retrieveAssetFromCacheWhenAssetIsCached() throws Exception {
        Optional<Asset> assetOptional = cache.retrieveAssetFromCache("cfr");
        assertSame(asset, assetOptional.get());
    }

    @Test
    public void retrieveAssetFromCacheWhenAssetIsNotCached() throws Exception {
        Optional<Asset> assetOptional = cache.retrieveAssetFromCache("rfc");

        assertFalse(assetOptional.isPresent());
    }

    @Test
    public void retrieveAssetFromCacheWhenPassingBlankString() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(ASSET_NULL_KEY);
        cache.retrieveAssetFromCache("");
    }

    @Test
    public void retrieveAssetFromCacheWhenPassingNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(ASSET_NULL_KEY);
        cache.retrieveAssetFromCache(null);
    }

    @Test
    public void cacheMessageWhenEverythingIsOK() throws Exception {
        Asset asset = new Asset();
        cache.cacheMessage("456", asset);

        Asset assetOptional = cache.retrieveAssetFromCache("456").get();
        assertSame(asset, assetOptional);
    }

    @Test
    public void cacheMessageWhenPassingBlankKey() throws Exception {
        Asset asset = new Asset();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(ASSET_NULL_CACHE);
        cache.cacheMessage("", asset);
    }

    @Test
    public void cacheMessageWhenPassingNullKey() throws Exception {
        Asset asset = new Asset();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(ASSET_NULL_CACHE);
        cache.cacheMessage("", asset);
    }

}