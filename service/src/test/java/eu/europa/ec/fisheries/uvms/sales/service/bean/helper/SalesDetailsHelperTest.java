package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mother.AAPProductTypeMother;
import eu.europa.ec.fisheries.uvms.sales.domain.mother.ReportMother;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
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
    private ConfigService configService;

    @Mock
    private EcbProxyService ecbProxyService;

    @Mock
    private ReportHelper reportHelper;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Mock
    private MapperFacade mapper;

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
                .salesReport(new SalesReportDto()
                    .location(locationDto));

        //mock
        when(referenceDataCache.getMarketAndStorageLocations()).thenReturn(referenceCoordinates);

        //execute
        salesDetailsHelper.enrichWithLocation(salesDetailsDto);

        //verify and assert
        verify(referenceDataCache).getMarketAndStorageLocations();
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
                .salesReport(new SalesReportDto()
                        .location(locationDto));

        //mock
        when(referenceDataCache.getMarketAndStorageLocations()).thenReturn(referenceCoordinates);

        //execute
        salesDetailsHelper.enrichWithLocation(salesDetailsDto);

        //verify and assert
        verify(referenceDataCache).getMarketAndStorageLocations();
        verifyNoMoreInteractions(referenceDataCache);

        assertEquals(51.5, locationDto.getLatitude(), 0.001);
        assertEquals(0.00, locationDto.getLongitude(), 0.001);
    }

    @Test
    public void testEnrichWithVesselInformationWhenNoAuctionSale() {
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
        doReturn(vessel).when(assetService).findByCFR(extId);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verify(assetService).findByCFR(extId);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertEquals(extId, salesDetailsDto.getFishingTrip().getVesselGuid());
        assertEquals(vesselName, salesDetailsDto.getFishingTrip().getVesselName());
        assertEquals(vesselCFR, salesDetailsDto.getFishingTrip().getVesselCFR());
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithFirstSaleAndSuccess() {
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
        doReturn(vessel).when(assetService).findByCFR(extId);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verify(assetService).findByCFR(extId);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertEquals(extId, salesDetailsDto.getFishingTrip().getVesselGuid());
        assertEquals(vesselName, salesDetailsDto.getFishingTrip().getVesselName());
        assertEquals(vesselCFR, salesDetailsDto.getFishingTrip().getVesselCFR());
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithFirstSaleAndReportIsIncomplete() {
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
    public void testEnrichWithVesselInformationWhenAuctionSaleWithFirstSaleAndCommunicationWithAssetGoesWrong() {
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
        doThrow(new SalesServiceException("oh oooh")).when(assetService).findByCFR(extId);

        //execute
        salesDetailsHelper.enrichWithVesselInformation(salesDetailsDto, report);

        //verify and assert
        verify(reportHelper).getVesselExtId(report);
        verify(assetService).findByCFR(extId);
        verifyNoMoreInteractions(assetService, reportHelper);

        assertNull(salesDetailsDto.getFishingTrip().getVesselGuid());
        assertNull(salesDetailsDto.getFishingTrip().getVesselName());
        assertNull(salesDetailsDto.getFishingTrip().getVesselCFR());

        //no exception should be thrown! Only logged!
    }

    @Test
    public void testEnrichWithVesselInformationWhenAuctionSaleWithVariousSupply() {
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
                .salesReport(new SalesReportDto()
                    .products(Lists.newArrayList(productDto1, productDto2)));

        //mock
        doReturn(localCurrency).when(configService).getParameter(ParameterKey.CURRENCY);
        doReturn(documentCurrency).when(reportHelper).getDocumentCurrency(report);
        doReturn(documentDate).when(reportHelper).getDocumentDate(report);
        doReturn(rate).when(ecbProxyService).findExchangeRate(documentCurrency, localCurrency, documentDate);
        doReturn(Lists.newArrayList(product1, product2)).when(reportHelper).getProductsOfReport(report);

        //execute
        salesDetailsHelper.convertPricesInLocalCurrency(salesDetailsDto, report);

        //verify and assert
        verify(configService).getParameter(ParameterKey.CURRENCY);
        verify(reportHelper).getDocumentCurrency(report);
        verify(reportHelper).getDocumentDate(report);
        verify(ecbProxyService).findExchangeRate(documentCurrency, localCurrency, documentDate);
        verify(reportHelper).getProductsOfReport(report);
        verifyNoMoreInteractions(configService, ecbProxyService, reportHelper);

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
                .salesReport(new SalesReportDto()
                        .products(Lists.newArrayList(productDto1, productDto2)));

        //mock
        doReturn(localCurrency).when(configService).getParameter(ParameterKey.CURRENCY);
        doReturn(documentCurrency).when(reportHelper).getDocumentCurrency(report);
        doReturn(Lists.newArrayList(product1, product2)).when(reportHelper).getProductsOfReport(report);

        //execute
        salesDetailsHelper.convertPricesInLocalCurrency(salesDetailsDto, report);

        //verify and assert
        verify(configService).getParameter(ParameterKey.CURRENCY);
        verify(reportHelper).getDocumentCurrency(report);
        verify(reportHelper).getProductsOfReport(report);
        verifyNoMoreInteractions(configService, ecbProxyService, reportHelper);

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

        SalesReportDto salesReportDto = new SalesReportDto()
                                            .products(Lists.newArrayList(productDto1, productDto2, productDto3));
        SalesDetailsDto salesDetailsDto = new SalesDetailsDto()
                                            .salesReport(salesReportDto);

        salesDetailsHelper.calculateTotals(salesDetailsDto);

        assertEquals(new BigDecimal("21.23"), salesReportDto.getTotals().getTotalPrice());
        assertEquals(new BigDecimal("32.425"), salesReportDto.getTotals().getTotalWeight());
    }

    @Test
    public void testEnrichWithRelatedReport() {
        Report report = ReportMother.withId("yow");
        List<Report> relatedReports = Arrays.asList(ReportMother.withId("hi"));
        SalesDetailsRelation salesDetailsRelation = new SalesDetailsRelation()
                .documentExtId("1")
                .reportExtId("5")
                .type(FluxReportItemType.SALES_NOTE);
        List<SalesDetailsRelation> salesDetailsRelations = Arrays.asList(salesDetailsRelation);
        SalesDetailsDto detailsDto = new SalesDetailsDto()
                                            .salesReport(new SalesReportDto());

        doReturn(relatedReports).when(reportDomainModel).findRelatedReportsOf(report);
        doReturn(salesDetailsRelations).when(mapper).mapAsList(relatedReports, SalesDetailsRelation.class);

        salesDetailsHelper.enrichWithRelatedReport(detailsDto, report);

        verify(reportDomainModel).findRelatedReportsOf(report);
        verify(mapper).mapAsList(relatedReports, SalesDetailsRelation.class);

        assertEquals(salesDetailsRelations, detailsDto.getSalesReport().getRelatedReports());
    }

    @Test
    public void testEnrichWithOtherRelevantVersionsWhenGivenReportIsTheLatestVersion() {
        Report report = new Report();
        SalesDetailsDto detailsDto = new SalesDetailsDto()
                                            .salesReport(new SalesReportDto());
        List<Report> olderVersions = Arrays.asList(ReportMother.withId("1"), ReportMother.withId("2"));
        List<SalesDetailsRelation> mappedOlderVersions = Arrays.asList(new SalesDetailsRelation().reportExtId("1"),
                                                            new SalesDetailsRelation().reportExtId("2"));

        doReturn(true).when(reportDomainModel).isLatestVersion(report);
        doReturn(olderVersions).when(reportDomainModel).findOlderVersionsOrderedByCreationDateDescending(report);
        doReturn(mappedOlderVersions).when(mapper).mapAsList(olderVersions, SalesDetailsRelation.class);

        salesDetailsHelper.enrichWithOtherRelevantVersions(detailsDto, report);

        verify(reportDomainModel).isLatestVersion(report);
        verify(reportDomainModel).findOlderVersionsOrderedByCreationDateDescending(report);
        verify(mapper).mapAsList(olderVersions, SalesDetailsRelation.class);
        verifyNoMoreInteractions(reportDomainModel, mapper);

        assertTrue(detailsDto.getSalesReport().isLatestVersion());
        assertSame(mappedOlderVersions, detailsDto.getSalesReport().getOtherVersions());
    }

    @Test
    public void testEnrichWithOtherRelevantVersionsWhenGivenReportIsNotTheLatestVersion() {
        Report report = new Report();
        SalesDetailsDto detailsDto = new SalesDetailsDto()
                                        .salesReport(new SalesReportDto());
        Report newestVersion = ReportMother.withId("1");
        List<SalesDetailsRelation> mappedOlderVersions = Arrays.asList(new SalesDetailsRelation().reportExtId("1"),
                new SalesDetailsRelation().reportExtId("2"));

        doReturn(false).when(reportDomainModel).isLatestVersion(report);
        doReturn(newestVersion).when(reportDomainModel).findLatestVersion(report);
        doReturn(mappedOlderVersions).when(mapper).mapAsList(Arrays.asList(newestVersion), SalesDetailsRelation.class);

        salesDetailsHelper.enrichWithOtherRelevantVersions(detailsDto, report);

        verify(reportDomainModel).isLatestVersion(report);
        verify(reportDomainModel).findLatestVersion(report);
        verify(mapper).mapAsList(Arrays.asList(newestVersion), SalesDetailsRelation.class);
        verifyNoMoreInteractions(reportDomainModel, mapper);

        assertFalse(detailsDto.getSalesReport().isLatestVersion());
        assertSame(mappedOlderVersions, detailsDto.getSalesReport().getOtherVersions());
    }

    @Test
    public void testEnrichProductWithFactor() {
        //data set
        ProductDto productDto1 = new ProductDto().factor(BigDecimal.valueOf(9));
        ProductDto productDto2 = new ProductDto().factor(null);
        SalesDetailsDto detailsDto = new SalesDetailsDto()
                .salesReport(new SalesReportDto()
                        .products(Arrays.asList(productDto1, productDto2)));

        //mock
        doReturn(BigDecimal.valueOf(1234)).when(referenceDataCache).getConversionFactorForProduct(productDto2);

        //execute
        salesDetailsHelper.enrichProductsWithFactor(detailsDto);

        //verify and assert
        verify(referenceDataCache).getConversionFactorForProduct(productDto2);
        verifyNoMoreInteractions(referenceDataCache);

        assertEquals(BigDecimal.valueOf(9), productDto1.getFactor());
        assertEquals(BigDecimal.valueOf(1234), productDto2.getFactor());
    }
}