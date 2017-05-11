package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import eu.europa.ec.fisheries.uvms.sales.service.mother.AAPProductTypeMother;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesDetailsHelperTest {

    @InjectMocks
    private SalesDetailsHelper salesDetailsHelper;

    @Mock
    private AssetService assetService;

    @Mock
    private ReferenceDataCache referenceDataCache;

    @Mock
    private ParameterService parameterService;

    @Mock
    private EcbProxyService ecbProxyService;

    @Mock
    private ReportHelper reportHelper;

    private List<ReferenceCoordinates> referenceCoordinates;

    @Before
    public void init() {
        referenceCoordinates = new ArrayList<>();
        referenceCoordinates.add(new ReferenceCoordinates("BEOST",51.21667, 2.91666666666667));
        referenceCoordinates.add(new ReferenceCoordinates("BEZEE",51.3189468, 3.206850700000018));
        referenceCoordinates.add(new ReferenceCoordinates("GBLON",51.5, 0.00));
        referenceCoordinates.add(new ReferenceCoordinates("BENIE",51.133333, 2.75));
    }

    @Test
    public void testEnrichWithLocationWhenLocationIsNotKnown() {
        //data set
        LocationDto locationDto = new LocationDto()
                .extId("NON-EXISTENT");

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .salesNote(new SalesNoteDto()
                    .location(locationDto));

        //mock
        when(referenceDataCache.getReferenceCoordinates()).thenReturn(referenceCoordinates);

        //execute
        salesDetailsHelper.enrichWithLocation(salesDetailsDto);

        //verify and assert
        verify(referenceDataCache).getReferenceCoordinates();
        verifyNoMoreInteractions(referenceDataCache);

        assertNull(locationDto.getLatitude());
        assertNull(locationDto.getLongitude());
    }

    @Test
    public void testEnrichWithLocationWhenLocationIsKnown() {
        //data set
        LocationDto locationDto = new LocationDto()
                .extId("GBLON");

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .salesNote(new SalesNoteDto()
                        .location(locationDto));

        //mock
        when(referenceDataCache.getReferenceCoordinates()).thenReturn(referenceCoordinates);

        //execute
        salesDetailsHelper.enrichWithLocation(salesDetailsDto);

        //verify and assert
        verify(referenceDataCache).getReferenceCoordinates();
        verifyNoMoreInteractions(referenceDataCache);

        assertEquals(51.5, locationDto.getLatitude(), 0.001);
        assertEquals(0.00, locationDto.getLongitude(), 0.001);
    }

    @Test
    public void testEnrichWithVesselInformationWhenNoAuctionSale() throws ServiceException {
        //data set
        String extId = "extId";
        String vesselName = "Opel Corsa";
        String vesselCFR = "cfr";

        Report report = new Report();

        AssetId assetId = new AssetId();
        assetId.setGuid(extId);

        Asset vessel = new Asset();
        vessel.setAssetId(assetId);
        vessel.setName(vesselName);
        vessel.setCfr(vesselCFR);

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .fishingTrip(new FishingTripDto());

        //mock
        doReturn(extId).when(reportHelper).getVesselExtId(report);
        doReturn(vessel).when(assetService).findByExtId(extId);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verify(assetService).findByExtId(extId);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertEquals(extId, salesDetailsDto.getFishingTrip().getVesselGuid());
        assertEquals(vesselName, salesDetailsDto.getFishingTrip().getVesselName());
        assertEquals(vesselCFR, salesDetailsDto.getFishingTrip().getVesselCFR());
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithFirstSaleAndSuccess() throws ServiceException {
        //data set
        String extId = "extId";
        String vesselName = "Opel Corsa";
        String vesselCFR = "cfr";

        AuctionSaleType auctionSale = new AuctionSaleType()
                .withSalesCategory(SalesCategoryType.FIRST_SALE);

        Report report = new Report()
                .withAuctionSale(auctionSale);

        AssetId assetId = new AssetId();
        assetId.setGuid(extId);

        Asset vessel = new Asset();
        vessel.setAssetId(assetId);
        vessel.setName(vesselName);
        vessel.setCfr(vesselCFR);

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .fishingTrip(new FishingTripDto());

        //mock
        doReturn(extId).when(reportHelper).getVesselExtId(report);
        doReturn(vessel).when(assetService).findByExtId(extId);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verify(assetService).findByExtId(extId);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertEquals(extId, salesDetailsDto.getFishingTrip().getVesselGuid());
        assertEquals(vesselName, salesDetailsDto.getFishingTrip().getVesselName());
        assertEquals(vesselCFR, salesDetailsDto.getFishingTrip().getVesselCFR());
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithFirstSaleAndReportIsIncomplete() throws ServiceException {
        //data set
        String extId = "extId";
        String vesselName = "Opel Corsa";
        String vesselCFR = "cfr";

        AuctionSaleType auctionSale = new AuctionSaleType()
                .withSalesCategory(SalesCategoryType.FIRST_SALE);

        Report report = new Report()
                .withAuctionSale(auctionSale);

        AssetId assetId = new AssetId();
        assetId.setGuid(extId);

        Asset vessel = new Asset();
        vessel.setAssetId(assetId);
        vessel.setName(vesselName);
        vessel.setCfr(vesselCFR);

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .fishingTrip(new FishingTripDto());

        //mock
        doThrow(new NullPointerException()).when(reportHelper).getVesselExtId(report);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertNull(salesDetailsDto.getFishingTrip().getVesselGuid());
        assertNull(salesDetailsDto.getFishingTrip().getVesselName());
        assertNull(salesDetailsDto.getFishingTrip().getVesselCFR());

        //no exception should be thrown! Only logged!
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithFirstSaleAndCommunicationWithAssetGoesWrong() throws ServiceException {
        //data set
        String extId = "extId";

        AuctionSaleType auctionSale = new AuctionSaleType()
                .withSalesCategory(SalesCategoryType.FIRST_SALE);

        Report report = new Report()
                .withAuctionSale(auctionSale);

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .fishingTrip(new FishingTripDto());

        //mock
        doReturn(extId).when(reportHelper).getVesselExtId(report);
        doThrow(new ServiceException("oh oooh")).when(assetService).findByExtId(extId);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verify(assetService).findByExtId(extId);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertNull(salesDetailsDto.getFishingTrip().getVesselGuid());
        assertNull(salesDetailsDto.getFishingTrip().getVesselName());
        assertNull(salesDetailsDto.getFishingTrip().getVesselCFR());

        //no exception should be thrown! Only logged!
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithVariousSupply() throws ServiceException {
        //data set
        AuctionSaleType auctionSale = new AuctionSaleType()
                .withSalesCategory(SalesCategoryType.VARIOUS_SUPPLY);

        Report report = new Report()
                .withAuctionSale(auctionSale);

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .fishingTrip(new FishingTripDto());

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verifyNoMoreInteractions(assetService, reportHelper);

        assertNull(salesDetailsDto.getFishingTrip().getVesselGuid());
        assertNull(salesDetailsDto.getFishingTrip().getVesselName());
        assertNull(salesDetailsDto.getFishingTrip().getVesselCFR());
    }

    @Test
    public void testConvertPricesInLocalCurrencyWhenDocumentCurrencyIsDifferentThanLocalCurrency() throws Exception {
        //data set
        String localCurrency = "EUR";
        String documentCurrency = "USD";
        DateTime documentDate = new DateTime();
        BigDecimal rate = new BigDecimal(2);

        AAPProductType product1 = new AAPProductType()
                .withTotalSalesPrice(new SalesPriceType()
                        .withChargeAmounts(new AmountType()
                                .withValue(new BigDecimal(10))));
        AAPProductType product2 = new AAPProductType()
                .withTotalSalesPrice(new SalesPriceType()
                        .withChargeAmounts(new AmountType()
                                .withValue(new BigDecimal(30))));

        Report report = new Report();

        ProductDto productDto1 = new ProductDto();
        ProductDto productDto2 = new ProductDto();
        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .salesNote(new SalesNoteDto()
                    .products(Lists.newArrayList(productDto1, productDto2)));

        //mock
        doReturn(localCurrency).when(parameterService).getStringValue(ParameterKey.CURRENCY.getKey());
        doReturn(documentCurrency).when(reportHelper).getDocumentCurrency(report);
        doReturn(documentDate).when(reportHelper).getDocumentDate(report);
        doReturn(rate).when(ecbProxyService).findExchangeRate(documentCurrency, localCurrency, documentDate);
        doReturn(Lists.newArrayList(product1, product2)).when(reportHelper).getProductsOfReport(report);

        //execute
        salesDetailsHelper.convertPricesInLocalCurrency(salesDetailsDto, report);

        //verify and assert
        verify(parameterService).getStringValue(ParameterKey.CURRENCY.getKey());
        verify(reportHelper).getDocumentCurrency(report);
        verify(reportHelper).getDocumentDate(report);
        verify(ecbProxyService).findExchangeRate(documentCurrency, localCurrency, documentDate);
        verify(reportHelper).getProductsOfReport(report);
        verifyNoMoreInteractions(parameterService, ecbProxyService, reportHelper);

        assertEquals(new BigDecimal(20), productDto1.getPrice());
        assertEquals(new BigDecimal(60), productDto2.getPrice());
    }

    @Test
    public void testConvertPricesInLocalCurrencyWhenDocumentCurrencyIsSamesAsLocalCurrency() throws Exception {
        //data set
        String localCurrency = "EUR";
        String documentCurrency = "EUR";

        AAPProductType product1 = AAPProductTypeMother.withTotalPrice(10);
        AAPProductType product2 = AAPProductTypeMother.withTotalPrice(30);

        Report report = new Report();

        ProductDto productDto1 = new ProductDto();
        ProductDto productDto2 = new ProductDto();
        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                .salesNote(new SalesNoteDto()
                        .products(Lists.newArrayList(productDto1, productDto2)));

        //mock
        doReturn(localCurrency).when(parameterService).getStringValue(ParameterKey.CURRENCY.getKey());
        doReturn(documentCurrency).when(reportHelper).getDocumentCurrency(report);
        doReturn(Lists.newArrayList(product1, product2)).when(reportHelper).getProductsOfReport(report);

        //execute
        salesDetailsHelper.convertPricesInLocalCurrency(salesDetailsDto, report);

        //verify and assert
        verify(parameterService).getStringValue(ParameterKey.CURRENCY.getKey());
        verify(reportHelper).getDocumentCurrency(report);
        verify(reportHelper).getProductsOfReport(report);
        verifyNoMoreInteractions(parameterService, ecbProxyService, reportHelper);

        assertEquals(new BigDecimal(10), productDto1.getPrice());
        assertEquals(new BigDecimal(30), productDto2.getPrice());
    }

    @Test
    public void testCalculateTotals() {
        ProductDto productDto1 = new ProductDto()
                .price(new BigDecimal("15.02"))
                .weight(new BigDecimal("32.21"));

        ProductDto productDto2 = new ProductDto()
                .price(null)
                .weight(null);

        ProductDto productDto3 = new ProductDto()
                .price(new BigDecimal("6.21"))
                .weight(new BigDecimal("0.215"));

        SalesNoteDto salesNoteDto = new SalesNoteDto()
                                            .products(Lists.newArrayList(productDto1, productDto2, productDto3));
        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                                            .salesNote(salesNoteDto);

        salesDetailsHelper.calculateTotals(salesDetailsDto);

        assertEquals(new BigDecimal("21.23"), salesNoteDto.getTotals().getTotalPrice());
        assertEquals(new BigDecimal("32.425"), salesNoteDto.getTotals().getTotalWeight());
    }
}