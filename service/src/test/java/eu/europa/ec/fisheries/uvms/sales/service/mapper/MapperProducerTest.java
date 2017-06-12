package eu.europa.ec.fisheries.uvms.sales.service.mapper;


import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.model.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MapperProducerTest {

    private static MapperFacade mapper;

    @BeforeClass
    public static void setUp() {
        mapper = new MapperProducer().getMapper();
    }

    @Test
    public void testMapLocationDtoToFLUXLocationType() {
        LocationDto locationDto = new LocationDto()
                .country("BEL")
                .extId("extId")
                .latitude(10d)
                .longitude(0d);

        FLUXLocationType fluxLocationType = mapper.map(locationDto, FLUXLocationType.class);

        assertEquals("BEL", fluxLocationType.getCountryID().getValue());
        assertEquals("extId", fluxLocationType.getID().getValue());
        assertEquals(BigDecimal.valueOf(10d), fluxLocationType.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLatitudeMeasure().getValue());
        assertEquals(BigDecimal.valueOf(0d), fluxLocationType.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLongitudeMeasure().getValue());
    }

    @Test
    public void testMapFLUXLocationTypeToLocationDto() {
        FLUXLocationType fluxLocationType = new FLUXLocationType()
                .withCountryID(new IDType().withValue("SWE"))
                .withID(new IDType().withValue("heya"))
                .withSpecifiedPhysicalFLUXGeographicalCoordinate(new FLUXGeographicalCoordinateType()
                        .withLatitudeMeasure(new MeasureType().withValue(BigDecimal.TEN))
                        .withLongitudeMeasure(new MeasureType().withValue(BigDecimal.ONE)));

        LocationDto locationDto = mapper.map(fluxLocationType, LocationDto.class);

        assertEquals("SWE", locationDto.getCountry());
        assertEquals("heya", locationDto.getExtId());
        assertEquals(10, locationDto.getLatitude(), 0.00001);
        assertEquals(1, locationDto.getLongitude(), 0.00001);
    }

    @Test
    public void testMapPartyDtoToSalesPartyType() {
        PartyDto partyDto = new PartyDto()
                .extId("heya mama di yeya")
                .name("Heya mama")
                .role("Pop song");

        SalesPartyType salesPartyType = mapper.map(partyDto, SalesPartyType.class);

        assertEquals("heya mama di yeya", salesPartyType.getID().getValue());
        assertEquals("Heya mama", salesPartyType.getName().getValue());
        assertEquals("Pop song", salesPartyType.getRoleCodes().get(0).getValue());
    }

    @Test
    public void testMapSalesPartyTypeToPartyDto() {
        SalesPartyType salesPartyType = new SalesPartyType()
                .withID(new IDType().withValue("heya mama di yeya"))
                .withName(new TextType().withValue("Heya mama"))
                .withRoleCodes(new CodeType().withValue("Pop song"));

        PartyDto partyDto = mapper.map(salesPartyType, PartyDto.class);

        assertEquals("heya mama di yeya", partyDto.getExtId());
        assertEquals("Heya mama", partyDto.getName());
        assertEquals("Pop song", partyDto.getRole());
    }

    @Test
    public void testMapAAPProductTypeToProductDto() {
        AAPProductType aapProductType = new AAPProductType()
                .withAppliedAAPProcesses(new AAPProcessType().withTypeCodes(new CodeType().withListID("FISH_FRESHNESS").withValue("FRESH")))
                .withOriginFLUXLocations(
                        new FLUXLocationType().withCountryID(new IDType().withValue("BEL")),
                        new FLUXLocationType().withCountryID(new IDType().withValue("FRA")))
                .withSpeciesCode(new CodeType().withValue("SAL"))
                .withUnitQuantity(new QuantityType().withValue(BigDecimal.TEN))
                .withWeightMeasure(new MeasureType().withValue(BigDecimal.ZERO))
                .withUsageCode(new CodeType().withValue("EAT"))
                .withSpecifiedSizeDistribution(new SizeDistributionType()
                        .withCategoryCode(new CodeType().withValue("Category"))
                        .withClassCodes(new CodeType().withValue("ClassCode")));

        ProductDto productDto = mapper.map(aapProductType, ProductDto.class);

        assertEquals("SAL", productDto.getSpecies());
        assertEquals(Lists.newArrayList("BEL", "FRA"), productDto.getAreas());
        assertEquals(BigDecimal.TEN, productDto.getQuantity());
        assertEquals(BigDecimal.ZERO, productDto.getWeight());
        assertEquals("EAT", productDto.getUsage());
        assertEquals("Category", productDto.getDistributionCategory());
    }

    @Test
    public void testMapSalesDocumentTypeToDocumentDto() {
        DateTime dateTime = new DateTime(2008, 11, 17, 0, 0);
        SalesEventType salesEventType = new SalesEventType()
                .withOccurrenceDateTime(new DateTimeType().withDateTime(dateTime));

        SalesDocumentType salesDocumentType = new SalesDocumentType()
                .withIDS(new IDType().withValue("ExtId"))
                .withCurrencyCode(new CodeType().withValue("GOAT"))
                .withSpecifiedSalesEvents(salesEventType);

        DocumentDto document = mapper.map(salesDocumentType, DocumentDto.class);

        assertEquals("ExtId", document.getExtId());
        assertEquals("GOAT", document.getCurrency());
        assertEquals(dateTime, document.getOccurrence());
    }

    @Test
    public void testMapFLUXReportDocumentTypeToFluxReportDto() {
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withPurpose(new TextType().withValue("knees weak, arms are heavy"))
                .withIDS(new IDType().withValue("mom's spaghetti"))
                .withPurposeCode(new CodeType().withValue("ORIGINAL"))
                .withOwnerFLUXParty(new FLUXPartyType().withNames(new TextType().withValue("party")));


        FluxReportDto fluxReportDto = mapper.map(fluxReportDocumentType, FluxReportDto.class);

        assertEquals("knees weak, arms are heavy", fluxReportDto.getPurposeText());
        assertEquals("mom's spaghetti", fluxReportDto.getExtId());
        assertEquals("ORIGINAL", fluxReportDto.getPurposeCode());
        assertEquals("party", fluxReportDto.getFluxReportParty());
    }

    @Test
    public void testMapSavedSearchGroupDtoToSavedSearchGroup() {
        List<FieldDto> fields = Arrays.asList(new FieldDto().key("COUNTRY").value("BEL"), new FieldDto().key("KEY").value("VALUE"));
        SavedSearchGroupDto savedSearchGroupDto = new SavedSearchGroupDto()
                .fields(fields)
                .id(123)
                .name("name")
                .user("user");
        SavedSearchGroup searchGroup = mapper.map(savedSearchGroupDto, SavedSearchGroup.class);

        List<SavedSearchGroupField> expected = Arrays.asList(new SavedSearchGroupField().withKey("COUNTRY").withValue("BEL"), new SavedSearchGroupField().withKey("KEY").withValue("VALUE"));

        assertEquals("name", searchGroup.getName());
        assertEquals("user", searchGroup.getUser());
        assertEquals(Integer.valueOf(123), searchGroup.getId());
        assertEquals(expected, searchGroup.getFields());
    }

    @Test
    public void testMapSavedSearchGroupToSavedSearchGroupDto() {
        List<SavedSearchGroupField> fields = Arrays.asList(new SavedSearchGroupField().withKey("COUNTRY").withValue("BEL"), new SavedSearchGroupField().withKey("KEY").withValue("VALUE"));
        SavedSearchGroup savedSearchGroup = new SavedSearchGroup()
                .withFields(fields)
                .withId(123)
                .withName("name")
                .withUser("user");

        SavedSearchGroupDto searchGroup = mapper.map(savedSearchGroup, SavedSearchGroupDto.class);

        assertEquals("name", searchGroup.getName());
        assertEquals("user", searchGroup.getUser());
        assertEquals(Integer.valueOf(123), searchGroup.getId());
        assertEquals(2, searchGroup.getFields().size());
    }

    @Test
    public void testReportTypeToSalesDetailsDtoWhenAuctionSaleIsProvided() {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("extId"))
                .withCreationDateTime(new DateTimeType().withDateTime(new DateTime(2016, 10, 10, 12, 10)))
                .withPurpose(new TextType().withValue("Oh... do I need a reason??"))
                .withPurposeCode(new CodeType().withValue("9"))
                .withOwnerFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("BEL")));

        FishingActivityType fishingActivity = new FishingActivityType()
                .withIDS(new IDType().withValue("FA1"))
                .withRelatedFLUXLocations(new FLUXLocationType().withID(new IDType().withValue("LUX")))
                .withSpecifiedDelimitedPeriods(new DelimitedPeriodType().withStartDateTime(new DateTimeType().withDateTime(new DateTime(2016, 5, 10, 20, 22))));

        SalesDocumentType salesDocument = new SalesDocumentType()
                .withSpecifiedFishingActivities(fishingActivity)
                .withSpecifiedFLUXLocations(new FLUXLocationType().withID(new IDType().withValue("LOC")))
                .withSpecifiedSalesBatches(new SalesBatchType().withSpecifiedAAPProducts(new AAPProductType().withSpeciesCode(new CodeType().withValue("SAL"))))
                .withSpecifiedSalesParties(new SalesPartyType().withID(new IDType().withValue("FRA")));

        SalesReportType salesReport = new SalesReportType()
                .withIncludedSalesDocuments(salesDocument)
                .withItemTypeCode(new CodeType().withValue("SN"));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument)
                .withSalesReports(salesReport);

        AuctionSaleType auctionSale = new AuctionSaleType().withSalesCategory(SalesCategoryType.NEGOTIATED_SALE);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage)
                .withAuctionSale(auctionSale);

        SalesDetailsDto salesDetailsDto = mapper.map(report, SalesDetailsDto.class);

        assertEquals(new DateTime(2016, 5, 10, 20, 22), salesDetailsDto.getFishingTrip().getLandingDate());
        assertEquals("LUX", salesDetailsDto.getFishingTrip().getLandingLocation());
        assertEquals("FA1", salesDetailsDto.getFishingTrip().getExtId());
        assertEquals("FRA", salesDetailsDto.getSalesReport().getParties().get(0).getExtId());
        assertEquals("extId", salesDetailsDto.getSalesReport().getFluxReport().getExtId());
        assertEquals(new DateTime(2016, 10, 10, 12, 10), salesDetailsDto.getSalesReport().getFluxReport().getCreation());
        assertEquals("Oh... do I need a reason??", salesDetailsDto.getSalesReport().getFluxReport().getPurposeText());
        assertEquals("9", salesDetailsDto.getSalesReport().getFluxReport().getPurposeCode());
        assertEquals("BEL", salesDetailsDto.getSalesReport().getFluxReport().getFluxReportParty());
        assertEquals("LOC", salesDetailsDto.getSalesReport().getLocation().getExtId());
        assertEquals("SAL", salesDetailsDto.getSalesReport().getProducts().get(0).getSpecies());
        assertEquals(SalesCategoryType.NEGOTIATED_SALE, salesDetailsDto.getSalesReport().getCategory());
        assertEquals(FluxReportItemType.SALES_NOTE, salesDetailsDto.getSalesReport().getItemType());
    }

    @Test
    public void testReportTypeToSalesDetailsDtoWhenAuctionSaleIsNotProvided() {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("extId"))
                .withCreationDateTime(new DateTimeType().withDateTime(new DateTime(2016, 10, 10, 12, 10)))
                .withPurpose(new TextType().withValue("Oh... do I need a reason??"))
                .withPurposeCode(new CodeType().withValue("9"))
                .withOwnerFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("BEL")));

        FishingActivityType fishingActivity = new FishingActivityType()
                .withIDS(new IDType().withValue("FA1"))
                .withRelatedFLUXLocations(new FLUXLocationType().withID(new IDType().withValue("LUX")))
                .withSpecifiedDelimitedPeriods(new DelimitedPeriodType().withStartDateTime(new DateTimeType().withDateTime(new DateTime(2016, 5, 10, 20, 22))));

        SalesDocumentType salesDocument = new SalesDocumentType()
                .withSpecifiedFishingActivities(fishingActivity)
                .withSpecifiedFLUXLocations(new FLUXLocationType().withID(new IDType().withValue("LOC")))
                .withSpecifiedSalesBatches(new SalesBatchType().withSpecifiedAAPProducts(new AAPProductType().withSpeciesCode(new CodeType().withValue("SAL"))))
                .withSpecifiedSalesParties(new SalesPartyType().withID(new IDType().withValue("FRA")));

        SalesReportType salesReport = new SalesReportType()
                .withIncludedSalesDocuments(salesDocument)
                .withItemTypeCode(new CodeType().withValue("SN"));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument)
                .withSalesReports(salesReport);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        SalesDetailsDto salesDetailsDto = mapper.map(report, SalesDetailsDto.class);

        assertEquals(new DateTime(2016, 5, 10, 20, 22), salesDetailsDto.getFishingTrip().getLandingDate());
        assertEquals("LUX", salesDetailsDto.getFishingTrip().getLandingLocation());
        assertEquals("FA1", salesDetailsDto.getFishingTrip().getExtId());
        assertEquals("FRA", salesDetailsDto.getSalesReport().getParties().get(0).getExtId());
        assertEquals("extId", salesDetailsDto.getSalesReport().getFluxReport().getExtId());
        assertEquals(new DateTime(2016, 10, 10, 12, 10), salesDetailsDto.getSalesReport().getFluxReport().getCreation());
        assertEquals("Oh... do I need a reason??", salesDetailsDto.getSalesReport().getFluxReport().getPurposeText());
        assertEquals("9", salesDetailsDto.getSalesReport().getFluxReport().getPurposeCode());
        assertEquals("BEL", salesDetailsDto.getSalesReport().getFluxReport().getFluxReportParty());
        assertEquals("LOC", salesDetailsDto.getSalesReport().getLocation().getExtId());
        assertEquals("SAL", salesDetailsDto.getSalesReport().getProducts().get(0).getSpecies());
        assertEquals(SalesCategoryType.FIRST_SALE, salesDetailsDto.getSalesReport().getCategory());
        assertEquals(FluxReportItemType.SALES_NOTE, salesDetailsDto.getSalesReport().getItemType());
    }

    @Test
    public void testReferenceDataCacheToCodeListsDto() {
        ReferenceDataCache referenceDataCache = new ReferenceDataCache();

        CodeListsDto map = mapper.map(referenceDataCache, CodeListsDto.class);

        assertNotNull(map.getCurrencies());
        assertNotNull(map.getDistributionClasses());
        assertNotNull(map.getFlagStates());
        assertNotNull(map.getFreshness());
        assertNotNull(map.getPresentations());
        assertNotNull(map.getPreservations());
        assertNotNull(map.getLandingPorts());
        assertNotNull(map.getSalesLocations());
        assertNotNull(map.getSalesCategories());
        assertNotNull(map.getSpecies());
        assertNotNull(map.getUsages());
    }

    @Test
    public void testReportQueryFilterToReportQueryFilterDto() {
        DateTime endDate = new DateTime(2017, 2, 1, 20, 0);
        DateTime startDate = new DateTime(2017, 1, 1, 20, 0);
        ReportQueryFilter ReportQueryFilter = new ReportQueryFilter()
                .withExcludeFluxReportIds("1", "2", "3")
                .withIncludeFluxReportIds("4", "5", "6")
                .withFlagState("BEL")
                .withLandingPort("LP")
                .withSalesCategory(SalesCategoryType.FIRST_SALE)
                .withSalesEndDate(endDate)
                .withSalesLocation("loc")
                .withSalesStartDate(startDate)
                .withAllSpecies("SAL", "ROM")
                .withAnySpecies("DOM", "POM")
                .withVesselExtIds("v1", "v2")
                .withVesselName("the best vessel evah");

        ReportQueryFilterDto actual = mapper.map(ReportQueryFilter, ReportQueryFilterDto.class);

        ReportQueryFilterDto expected = new ReportQueryFilterDto()
                .excludeFluxReportIds(Lists.newArrayList("1", "2", "3"))
                .includeFluxReportIds(Lists.newArrayList("4", "5", "6"))
                .flagState("BEL")
                .landingPort("LP")
                .salesCategory(SalesCategoryType.FIRST_SALE)
                .salesEndDate(endDate)
                .salesStartDate(startDate)
                .salesLocation("loc")
                .speciesAll(Lists.newArrayList("SAL", "ROM"))
                .speciesAny(Lists.newArrayList("DOM", "POM"))
                .vesselExtIds(Lists.newArrayList("v1", "v2"))
                .vesselName("the best vessel evah");

        assertEquals(expected, actual);
    }

    @Test
    public void testReportQueryFilterDtoToReportQueryFilter() {
        DateTime endDate = new DateTime(2017, 2, 1, 20, 0);
        DateTime startDate = new DateTime(2017, 1, 1, 20, 0);

        ReportQueryFilterDto reportQueryFilterDto = new ReportQueryFilterDto()
                .excludeFluxReportIds(Lists.newArrayList("1", "2", "3"))
                .includeFluxReportIds(Lists.newArrayList("4", "5", "6"))
                .flagState("BEL")
                .landingPort("LP")
                .salesCategory(SalesCategoryType.FIRST_SALE)
                .salesEndDate(endDate)
                .salesStartDate(startDate)
                .salesLocation("loc")
                .speciesAll(Lists.newArrayList("SAL", "ROM"))
                .speciesAny(Lists.newArrayList("DOM", "POM"))
                .vesselExtIds(Lists.newArrayList("v1", "v2"))
                .vesselName("the best vessel evah");

        ReportQueryFilter actual = mapper.map(reportQueryFilterDto, ReportQueryFilter.class);

        ReportQueryFilter expected = new ReportQueryFilter()
                .withExcludeFluxReportIds("1", "2", "3")
                .withIncludeFluxReportIds("4", "5", "6")
                .withFlagState("BEL")
                .withLandingPort("LP")
                .withSalesCategory(SalesCategoryType.FIRST_SALE)
                .withSalesEndDate(endDate)
                .withSalesLocation("loc")
                .withSalesStartDate(startDate)
                .withAllSpecies("SAL", "ROM")
                .withAnySpecies("DOM", "POM")
                .withVesselExtIds("v1", "v2")
                .withVesselName("the best vessel evah");

        assertEquals(expected, actual);
    }

    @Test
    public void testMapPageCriteriaDtoToFluxReportQuery() {
        ReportQueryFilter ReportQueryFilter = new ReportQueryFilter();
        PageCriteriaDto<ReportQueryFilter> pageCriteriaDto = new PageCriteriaDto<ReportQueryFilter>()
                .pageIndex(2)
                .pageSize(15)
                .filters(ReportQueryFilter)
                .sortDirection(SortDirection.DESCENDING)
                .sortField(ReportQuerySortField.LANDING_DATE);

        ReportQuery fluxReportQuery = mapper.map(pageCriteriaDto, ReportQuery.class);

        assertEquals(2, fluxReportQuery.getPaging().getPage());
        assertEquals(15, fluxReportQuery.getPaging().getItemsPerPage());
        assertEquals(ReportQueryFilter, fluxReportQuery.getFilters());
        assertEquals(SortDirection.DESCENDING, fluxReportQuery.getSorting().getDirection());
        assertEquals(ReportQuerySortField.LANDING_DATE, fluxReportQuery.getSorting().getField());
    }

    @Test
    public void testMapReportTypeToReportListDtoWhenAuctionSalesIsProvided() {
        AuctionSaleType auctionSale = new AuctionSaleType()
                .withSalesCategory(SalesCategoryType.NEGOTIATED_SALE);

        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("fluxReportDocumentExtId"))
                .withOwnerFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("This party is mine")))
                .withReferencedID(new IDType().withValue("Heya"));

        FLUXLocationType fluxLocation1 = new FLUXLocationType()
                .withID(new IDType().withValue("BEL"));
        FLUXLocationType fluxLocation2 = new FLUXLocationType()
                .withID(new IDType().withValue("NED"));

        RegistrationLocationType registrationLocation = new RegistrationLocationType()
                .withCountryID(new IDType().withValue("FRA"));
        RegistrationEventType registrationEvent = new RegistrationEventType()
                .withRelatedRegistrationLocation(registrationLocation);

        VesselTransportMeansType vessel = new VesselTransportMeansType()
                .withNames(new TextType().withValue("vesselName"))
                .withIDS(new IDType().withValue("vesselExtId"))
                .withSpecifiedRegistrationEvents(registrationEvent);

        FishingActivityType fishingActivity = new FishingActivityType()
                .withRelatedVesselTransportMeans(vessel)
                .withSpecifiedDelimitedPeriods(new DelimitedPeriodType().withStartDateTime(new DateTimeType().withDateTime(new DateTime(2017, 3, 3, 14, 0))))
                .withRelatedFLUXLocations(fluxLocation2);

        SalesEventType salesEvent = new SalesEventType()
                .withOccurrenceDateTime(new DateTimeType().withDateTime(new DateTime(2017, 3, 2, 15, 0)));

        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("SELLER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        SalesDocumentType salesDocument = new SalesDocumentType()
                .withSpecifiedFLUXLocations(fluxLocation1)
                .withSpecifiedSalesEvents(salesEvent)
                .withSpecifiedFishingActivities(fishingActivity)
                .withSpecifiedSalesParties(salesParties);

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument)
                .withSalesReports(new SalesReportType().withIncludedSalesDocuments(salesDocument));

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage)
                .withAuctionSale(auctionSale);

        ReportListDto dto = mapper.map(report, ReportListDto.class);

        assertEquals(SalesCategoryType.NEGOTIATED_SALE, dto.getCategory());
        assertEquals("fluxReportDocumentExtId", dto.getExtId());
        assertEquals(new DateTime(2017, 3, 2, 15, 0), dto.getOccurrence());
        assertEquals("vesselName", dto.getVesselName());
        assertEquals("vesselExtId", dto.getVesselExtId());
        assertEquals("FRA", dto.getFlagState());
        assertEquals(new DateTime(2017, 3, 3, 14, 0), dto.getLandingDate());
        assertEquals("NED", dto.getLandingPort());
        assertEquals("BEL", dto.getLocation());
        assertEquals("Mathiblaa", dto.getBuyer());
        assertEquals("Superstijn", dto.getSeller());
        assertEquals("Heya", dto.getReferencedId());
    }

    @Test
    public void testMapReportTypeToReportListDtoWhenAuctionSalesIsNotProvided() {
        FLUXReportDocumentType fluxReportDocument = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("fluxReportDocumentExtId"))
                .withOwnerFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("This party is mine")))
                .withReferencedID(new IDType().withValue("Heya"));;

        FLUXLocationType fluxLocation1 = new FLUXLocationType()
                .withID(new IDType().withValue("BEL"));
        FLUXLocationType fluxLocation2 = new FLUXLocationType()
                .withID(new IDType().withValue("NED"));

        RegistrationLocationType registrationLocation = new RegistrationLocationType()
                .withCountryID(new IDType().withValue("FRA"));
        RegistrationEventType registrationEvent = new RegistrationEventType()
                .withRelatedRegistrationLocation(registrationLocation);

        VesselTransportMeansType vessel = new VesselTransportMeansType()
                .withNames(new TextType().withValue("vesselName"))
                .withIDS(new IDType().withValue("vesselExtId"))
                .withSpecifiedRegistrationEvents(registrationEvent);

        FishingActivityType fishingActivity = new FishingActivityType()
                .withRelatedVesselTransportMeans(vessel)
                .withSpecifiedDelimitedPeriods(new DelimitedPeriodType().withStartDateTime(new DateTimeType().withDateTime(new DateTime(2017, 3, 3, 14, 0))))
                .withRelatedFLUXLocations(fluxLocation2);

        SalesEventType salesEvent = new SalesEventType()
                .withOccurrenceDateTime(new DateTimeType().withDateTime(new DateTime(2017, 3, 2, 15, 0)));

        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("SELLER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        SalesDocumentType salesDocument = new SalesDocumentType()
                .withSpecifiedFLUXLocations(fluxLocation1)
                .withSpecifiedSalesEvents(salesEvent)
                .withSpecifiedFishingActivities(fishingActivity)
                .withSpecifiedSalesParties(salesParties);

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocument)
                .withSalesReports(new SalesReportType().withIncludedSalesDocuments(salesDocument));

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        ReportListDto dto = mapper.map(report, ReportListDto.class);

        assertEquals(SalesCategoryType.FIRST_SALE, dto.getCategory());
        assertEquals("fluxReportDocumentExtId", dto.getExtId());
        assertEquals(new DateTime(2017, 3, 2, 15, 0), dto.getOccurrence());
        assertEquals("vesselName", dto.getVesselName());
        assertEquals("vesselExtId", dto.getVesselExtId());
        assertEquals("FRA", dto.getFlagState());
        assertEquals(new DateTime(2017, 3, 3, 14, 0), dto.getLandingDate());
        assertEquals("NED", dto.getLandingPort());
        assertEquals("BEL", dto.getLocation());
        assertEquals("Mathiblaa", dto.getBuyer());
        assertEquals("Superstijn", dto.getSeller());
        assertEquals("Heya", dto.getReferencedId());
    }

    @Test
    public void testMapReportListDtoToReportListExportDto() {
        ReportListDto reportListDto = new ReportListDto()
                .buyer("Pope")
                .seller("Stijn")
                .category(SalesCategoryType.FIRST_SALE)
                .externalMarking("marked for life")
                .extId("GUID")
                .flagState("BEL")
                .ircs("ircs")
                .landingDate(new DateTime(2017, 3, 2, 0, 0))
                .landingPort("Garagepoort")
                .location("location")
                .occurrence(new DateTime(2017, 3, 1, 0, 0))
                .vesselName("vesselName")
                .vesselExtId("vesselExtId");

        ReportListExportDto reportListExportDto = new ReportListExportDto()
                .buyer("Pope")
                .seller("Stijn")
                .category(SalesCategoryType.FIRST_SALE)
                .externalMarking("marked for life")
                .flagState("BEL")
                .ircs("ircs")
                .landingDate(new DateTime(2017, 3, 2, 0, 0).toString())
                .landingPort("Garagepoort")
                .location("location")
                .occurrence(new DateTime(2017, 3, 1, 0, 0).toString())
                .vesselName("vesselName");

        ReportListExportDto listDto = mapper.map(reportListDto, ReportListExportDto.class);

        assertEquals(reportListExportDto, listDto);
    }

    @Test
    public void testMapFLUXSalesQueryMessage() {
        //data set
        DateTime startDate = new DateTime(2001, 1, 1, 12, 0);
        DateTime endDate = new DateTime(2009, 9, 9, 12, 0);

        DelimitedPeriodType period = new DelimitedPeriodType()
                .withStartDateTime(new DateTimeType().withDateTime(startDate))
                .withEndDateTime(new DateTimeType().withDateTime(endDate));

        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("FLAG"))
                .withValueCode(new CodeType().withValue("BEL"));

        SalesQueryType salesQueryType = new SalesQueryType()
                .withSpecifiedDelimitedPeriod(period)
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(salesQueryType);

        //execute
        ReportQuery reportQuery = mapper.map(fluxSalesQueryMessage, ReportQuery.class);

        //assert
        ReportQueryFilter filters = reportQuery.getFilters();

        assertNotNull(filters);
        assertNull(reportQuery.getPaging());
        assertNull(reportQuery.getSorting());

        assertEquals(startDate, filters.getSalesStartDate());
        assertEquals(endDate, filters.getSalesEndDate());
        assertEquals("BEL", filters.getFlagState());
    }

    @Test
    public void testMapSalesDetailsRelation() {
        //data set
        String reportExtId = "id1";
        String documentExtId = "id2";

        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                    .withFLUXReportDocument(new FLUXReportDocumentType()
                        .withIDS(new IDType().withValue(reportExtId)))
                    .withSalesReports(new SalesReportType()
                        .withItemTypeCode(new CodeType().withValue("SN"))
                        .withIncludedSalesDocuments(new SalesDocumentType()
                            .withIDS(new IDType().withValue(documentExtId)))));


        //execute
        SalesDetailsRelation salesDetailsRelation = mapper.map(report, SalesDetailsRelation.class);

        //assert
        assertEquals(reportExtId, salesDetailsRelation.getReportExtId());
        assertEquals(documentExtId, salesDetailsRelation.getDocumentExtId());
        assertEquals(FluxReportItemType.SALES_NOTE, salesDetailsRelation.getType());
    }

}