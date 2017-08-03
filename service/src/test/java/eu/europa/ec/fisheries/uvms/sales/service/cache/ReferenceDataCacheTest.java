package eu.europa.ec.fisheries.uvms.sales.service.cache;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.sales.service.MDRService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReferenceDataCacheTest {

    private ReferenceDataCache referenceDataCache;

    @Mock
    private MDRService mdrService;

    @Mock
    private MapperFacade mapper;

    private final List<ReferenceCode> expectedReferenceCodes = Lists.newArrayList(new ReferenceCode("a", "a"), new ReferenceCode("b", "b"));
    private final List<ObjectRepresentation> objectRepresentations = Lists.newArrayList(new ObjectRepresentation(), new ObjectRepresentation());

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        referenceDataCache = new ReferenceDataCache(mdrService, mapper);
        referenceDataCache.init();
    }

    @Test
    public void getFlagStates() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.FLAG_STATES);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getFlagStates();
        
        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getFlagStates();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.FLAG_STATES);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getSalesCategories() throws Exception {
        assertEquals(3, referenceDataCache.getSalesCategories().size());
    }

    @Test
    public void getSalesLocations() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.SALES_LOCATIONS);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getSalesLocations();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getSalesLocations();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.SALES_LOCATIONS);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getFreshness() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.FRESHNESS);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getFreshness();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getFreshness();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.FRESHNESS);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getPresentations() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.PRESENTATIONS);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getPresentations();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getPresentations();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.PRESENTATIONS);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getPreservations() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.PRESERVATIONS);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getPreservations();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getPreservations();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.PRESERVATIONS);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getDistributionClasses() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.DISTRIBUTION_CLASSES);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getDistributionClasses();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getDistributionClasses();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.DISTRIBUTION_CLASSES);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getUsages() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.USAGES);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getUsages();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getUsages();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.USAGES);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getCurrencies() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.CURRENCIES);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getCurrencies();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getCurrencies();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.CURRENCIES);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getSpecies() throws Exception {
        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.SPECIES);
        doReturn(expectedReferenceCodes).when(mapper).mapAsList(objectRepresentations, ReferenceCode.class);

        //execute first time
        List<ReferenceCode> result = referenceDataCache.getSpecies();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCode> cachedResult = referenceDataCache.getSpecies();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.SPECIES);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCode.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCodes, result);
        assertEquals(expectedReferenceCodes, cachedResult);
    }

    @Test
    public void getMarketAndStorageLocations() throws Exception {
        List<ReferenceCoordinates> expectedReferenceCoordinates = Lists.newArrayList(new ReferenceCoordinates("a", 1d, 2d));

        doReturn(objectRepresentations).when(mdrService).findCodeList(MDRCodeListKey.MARKET_AND_STORAGE_LOCATIONS);
        doReturn(expectedReferenceCoordinates).when(mapper).mapAsList(objectRepresentations, ReferenceCoordinates.class);

        //execute first time
        List<ReferenceCoordinates> result = referenceDataCache.getMarketAndStorageLocations();

        //execute a second time, to make sure the second time the call to MDR is cached
        List<ReferenceCoordinates> cachedResult = referenceDataCache.getMarketAndStorageLocations();

        verify(mdrService, times(1)).findCodeList(MDRCodeListKey.MARKET_AND_STORAGE_LOCATIONS);
        verify(mapper, times(2)).mapAsList(objectRepresentations, ReferenceCoordinates.class);
        verifyNoMoreInteractions(mdrService, mapper);

        assertEquals(expectedReferenceCoordinates, result);
        assertEquals(expectedReferenceCoordinates, cachedResult);
    }

}