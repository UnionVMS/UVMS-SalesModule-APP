package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.AssetServiceBeanHelper;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceBeanTest {


    @InjectMocks
    private AssetServiceBean assetServiceBean;

    @Mock
    private AssetServiceBeanHelper helper;

    @Mock
    private AssetCacheBean cache;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFindByCFRWhenSuccessAndCacheWasHit() throws Exception {
        final String CFR = "cfr";

        Asset asset = new Asset();
        asset.setCfr(CFR);
        GetAssetModuleResponse response = new GetAssetModuleResponse();
        response.setAsset(asset);

        when(cache.retrieveAssetFromCache(CFR)).thenReturn(Optional.of(asset));

        assertSame(asset, assetServiceBean.findByCFR(CFR));
        verify(cache).retrieveAssetFromCache(CFR);
        verifyNoMoreInteractions(helper, cache);
    }

    @Test
    public void testFindByCFRWhenSuccessAndCacheWasntHit() throws Exception {
        final String CFR = "cfr";

        Asset asset = new Asset();
        asset.setCfr(CFR);
        GetAssetModuleResponse response = new GetAssetModuleResponse();
        response.setAsset(asset);

        when(helper.createRequestToFindAssetByCFR(CFR)).thenReturn("request");
        when(helper.callAssetModule("request", GetAssetModuleResponse.class)).thenReturn(response);
        when(cache.retrieveAssetFromCache(CFR)).thenReturn(Optional.absent());
        doNothing().when(cache).cacheMessage(CFR, asset);

        assertSame(asset, assetServiceBean.findByCFR(CFR));
        verify(helper).createRequestToFindAssetByCFR(CFR);
        verify(helper).callAssetModule("request", GetAssetModuleResponse.class);
        verify(cache).cacheMessage(CFR, asset);
        verify(cache).retrieveAssetFromCache(CFR);
        verifyNoMoreInteractions(helper, cache);
    }

    @Test
    public void testFindByNameOrCFROrIRCS() throws Exception {
        Asset asset = new Asset();
        ListAssetResponse listAssetResponse = new ListAssetResponse();
        listAssetResponse.getAsset().add(asset);

        when(helper.createRequestToFindAssetsByNameOrCFROrIRCS("searchString")).thenReturn("request");
        when(helper.callAssetModule("request", ListAssetResponse.class)).thenReturn(listAssetResponse);

        List<Asset> result = assetServiceBean.findByNameOrCFROrIRCS("searchString");

        verify(helper).createRequestToFindAssetsByNameOrCFROrIRCS("searchString");
        verify(helper).callAssetModule("request", ListAssetResponse.class);
        verifyNoMoreInteractions(helper);

        assertEquals(1, result.size());
        assertSame(asset, result.get(0));
    }

    @Test
    public void testFindExtIdsByNameOrCFROrIRCS() throws Exception {
        AssetId assetId = new AssetId();
        assetId.setGuid("guid");

        Asset asset = new Asset();
        asset.setAssetId(assetId);

        ListAssetResponse listAssetResponse = new ListAssetResponse();
        listAssetResponse.getAsset().add(asset);

        when(helper.createRequestToFindAssetsByNameOrCFROrIRCS("searchString")).thenReturn("request");
        when(helper.callAssetModule("request", ListAssetResponse.class)).thenReturn(listAssetResponse);

        List<String> result = assetServiceBean.findExtIdsByNameOrCFROrIRCS("searchString");

        verify(helper).createRequestToFindAssetsByNameOrCFROrIRCS("searchString");
        verify(helper).callAssetModule("request", ListAssetResponse.class);
        verifyNoMoreInteractions(helper);

        assertEquals(1, result.size());
        assertSame("guid", result.get(0));
    }

}