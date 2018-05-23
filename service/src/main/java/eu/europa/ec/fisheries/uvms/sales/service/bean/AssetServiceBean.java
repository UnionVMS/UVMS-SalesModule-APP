package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetCache;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.AssetServiceBeanHelper;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AssetServiceBean implements AssetService {

    @EJB
    private AssetServiceBeanHelper helper;

    @EJB
    private AssetCache cache;

    @Override
    public Asset findByCFR(String cfr) {
        GetAssetModuleResponse response;

        try {
            Optional<Asset> assetOptional = cache.retrieveAssetFromCache(cfr);
            if (assetOptional.isPresent()) {
                return assetOptional.get();
            }

            String request = helper.createRequestToFindAssetByCFR(cfr);
            log.info("Send GetAssetModuleRequest message to Asset");
            response = helper.callAssetModule(request, GetAssetModuleResponse.class);
        } catch (SalesNonBlockingException e) {
            cache.cacheMessage(cfr, null);
            throw new SalesNonBlockingException("Exception during JMS call or the response " +
                    "from Asset contained a 'not found' message");
        }

        cache.cacheMessage(cfr, response.getAsset());
        return response.getAsset();
    }

    @Override
    public List<Asset> findByNameOrCFROrIRCS(String searchString) {
        String request = helper.createRequestToFindAssetsByNameOrCFROrIRCS(searchString);
        log.info("Send AssetListModuleRequest message to Asset");
        ListAssetResponse response = helper.callAssetModule(request, ListAssetResponse.class);
        return response.getAsset(); //yup, getAsset() return a list of assets in this case. I was confused as well.
    }

    @Override
    public List<String> findExtIdsByNameOrCFROrIRCS(String searchString) {
        List<Asset> assets = findByNameOrCFROrIRCS(searchString);

        List<String> assetExtIds = new ArrayList<>();
        for (Asset asset : assets) {
            assetExtIds.add(asset.getAssetId().getGuid());
        }
        return assetExtIds;
    }

}
