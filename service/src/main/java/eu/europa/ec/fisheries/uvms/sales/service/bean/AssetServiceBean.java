package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.AssetServiceBeanHelper;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetServiceBean implements AssetService {

    @EJB
    private AssetServiceBeanHelper helper;

    @Override
    public Asset findByCFR(String cfr) {
        String request = helper.createRequestToFindAssetByCFR(cfr);
        GetAssetModuleResponse response = helper.callAssetModule(request, GetAssetModuleResponse.class);
        return response.getAsset();
    }

    @Override
    public List<Asset> findByNameOrCFROrIRCS(String searchString) {
        String request = helper.createRequestToFindAssetsByNameOrCFROrIRCS(searchString);
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
