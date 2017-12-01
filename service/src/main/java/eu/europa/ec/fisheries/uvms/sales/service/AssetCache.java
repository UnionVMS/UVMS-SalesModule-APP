package eu.europa.ec.fisheries.uvms.sales.service;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public interface AssetCache {

    Optional<Asset> retrieveAssetFromCache(String cfr);
    void cacheMessage(String cfr, Asset asset);
}
