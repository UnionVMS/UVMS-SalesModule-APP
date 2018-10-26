package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import java.util.Optional;

public interface AssetCache {

    Optional<Asset> retrieveAssetFromCache(String cfr);
    void cacheMessage(String cfr, Asset asset);
}
