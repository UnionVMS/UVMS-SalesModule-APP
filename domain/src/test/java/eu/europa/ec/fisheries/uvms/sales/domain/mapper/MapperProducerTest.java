package eu.europa.ec.fisheries.uvms.sales.domain.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.SalesCategory;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.*;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapperProducerTest {

    private static MapperFacade mapper;

    @BeforeClass
    public static void setUp() {
        mapper = new MapperProducer().getMapper();
    }

    @Test
    public void testMapReportTypeToFluxReport() {
        DateTime creation = new DateTime(2017, 2, 17, 16, 35);
        DateTime deletion = new DateTime(2018, 2, 17, 16, 35);

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("ID"))
                .withCreationDateTime(new DateTimeType().withDateTime(creation))
                .withPurpose(new TextType().withValue("The cake is a lie"))
                .withPurposeCode(new CodeType().withValue("5"))
                .withOwnerFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("name")))
                .withReferencedID(new IDType().withValue("previousExtId"));

        SalesReportType salesReportType = new SalesReportType()
                .withItemTypeCode(new CodeType().withValue("TOD"))
                .withIncludedSalesDocuments(new SalesDocumentType().withIDS(new IDType().withValue("DOC1")));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType)
                .withSalesReports(salesReportType);

        AuctionSaleType auctionSaleType = new AuctionSaleType()
                .withSalesCategory(SalesCategoryType.FIRST_SALE);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage)
                .withAuctionSale(auctionSaleType)
                .withDeletion(deletion);

        FluxReport fluxReport = mapper.map(report, FluxReport.class);

        assertEquals("ID", fluxReport.getExtId());
        assertEquals(creation, fluxReport.getCreation());
        assertEquals("The cake is a lie", fluxReport.getPurposeText());
        assertEquals(Purpose.CORRECTION, fluxReport.getPurpose());
        assertEquals("name", fluxReport.getFluxReportParty());
        assertEquals(FluxReportItemType.TAKE_OVER_DOCUMENT, fluxReport.getItemType());
        assertEquals("DOC1", fluxReport.getDocument().getExtId());
        assertEquals(SalesCategory.FIRST_SALE, fluxReport.getAuctionSale().getCategory());
        assertEquals(deletion, fluxReport.getDeletion());
        assertEquals("previousExtId", fluxReport.getPreviousFluxReportExtId());
    }

    @Test
    public void testMapFluxReportToReportType() {
        DateTime creation = new DateTime(1994, 4, 5, 0, 0);
        DateTime deletion = new DateTime(1996, 4, 5, 0, 0);

        FluxReport fluxReport = new FluxReport()
                .extId("extId")
                .purpose(Purpose.ORIGINAL)
                .purposeText("Sorry miss Jackson, I am for real")
                .creation(creation)
                .fluxReportParty("Outkast")
                .itemType(FluxReportItemType.SALES_NOTE)
                .document(new Document().extId("docu"))
                .auctionSale(new AuctionSale().category(SalesCategory.NEGOTIATED_SALE))
                .previousFluxReportExtId("100ext")
                .deletion(deletion);

        Report report = mapper.map(fluxReport, Report.class);
        FLUXSalesReportMessage fluxSalesReportMessageType = report.getFLUXSalesReportMessage();
        AuctionSaleType auctionSaleType = report.getAuctionSale();

        assertEquals("extId", fluxSalesReportMessageType.getFLUXReportDocument().getIDS().get(0).getValue());
        assertEquals(creation, fluxSalesReportMessageType.getFLUXReportDocument().getCreationDateTime().getDateTime());
        assertEquals("Sorry miss Jackson, I am for real", fluxSalesReportMessageType.getFLUXReportDocument().getPurpose().getValue());
        assertEquals("9", fluxSalesReportMessageType.getFLUXReportDocument().getPurposeCode().getValue());
        assertEquals("Outkast", fluxSalesReportMessageType.getFLUXReportDocument().getOwnerFLUXParty().getIDS().get(0).getValue());
        assertEquals("SN", fluxSalesReportMessageType.getSalesReports().get(0).getItemTypeCode().getValue());
        assertEquals("docu", fluxSalesReportMessageType.getSalesReports().get(0).getIncludedSalesDocuments().get(0).getIDS().get(0).getValue());
        assertEquals(SalesCategoryType.NEGOTIATED_SALE, auctionSaleType.getSalesCategory());
        assertEquals("100ext", fluxSalesReportMessageType.getFLUXReportDocument().getReferencedID().getValue());
        assertEquals(deletion, fluxReport.getDeletion());
    }

    @Test
    public void testMapAAPProductTypeToProductWhenAllTypesArePresent() {
        AAPProcessType aapProcessType = new AAPProcessType().withTypeCodes(
                new CodeType().withListID("FISH_FRESHNESS").withValue("FRESH"),
                new CodeType().withListID("FISH_PRESENTATION").withValue("GUTTED"),
                new CodeType().withListID("FISH_PRESERVATION").withValue("SALTED"));

        AAPProductType aapProductType = new AAPProductType()
                .withAppliedAAPProcesses(aapProcessType);


        Product product = mapper.map(aapProductType, Product.class);

        assertEquals("FRESH", product.getFreshness());
        assertEquals("GUTTED", product.getPresentation());
        assertEquals("SALTED", product.getPreservation());
    }

    @Test
    public void testMapAAPProductTypeToProduct() {
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
                        .withClassCodes(new CodeType().withValue("ClassCode")))
                .withTotalSalesPrice(new SalesPriceType().withChargeAmounts(new AmountType().withValue(BigDecimal.ONE)));


        Product product = mapper.map(aapProductType, Product.class);

        assertEquals("FRESH", product.getFreshness());
        assertEquals(2, product.getOrigins().size());
        assertEquals("BEL", product.getOrigins().get(0).getCountryCode());
        assertEquals("SAL", product.getSpecies());
        assertEquals(new Double(10), product.getQuantity());
        assertEquals(new Double(0), product.getWeight());
        assertEquals("EAT", product.getUsage());
        assertEquals("Category", product.getDistributionCategory());
        assertEquals("ClassCode", product.getDistributionClass());
        assertEquals(BigDecimal.ONE, product.getPrice());
    }

    @Test
    public void testMapProductToAAPProductType() {
        Product product = new Product()
                .origins(Arrays.asList(new FluxLocation(), new FluxLocation(), new FluxLocation()))
                .presentation("GUTTED")
                .preservation("SALTED")
                .freshness("HOT OF THE PRESS")
                .species("COD")
                .quantity(123.0)
                .weight(345.0)
                .usage("FOOD")
                .distributionCategory("Category")
                .distributionClass("class")
                .price(new BigDecimal(125));
        AAPProductType aapProductType = mapper.map(product, AAPProductType.class);

        assertEquals(Arrays.asList(new FLUXLocationType(), new FLUXLocationType(), new FLUXLocationType()), aapProductType.getOriginFLUXLocations());
        AAPProcessType relevantProcessType = aapProductType.getAppliedAAPProcesses().get(0);

        assertEquals("SALTED", relevantProcessType.getTypeCodes().get(0).getValue());
        assertEquals("GUTTED", relevantProcessType.getTypeCodes().get(1).getValue());
        assertEquals("HOT OF THE PRESS", relevantProcessType.getTypeCodes().get(2).getValue());
    }

    @Test
    public void testMapSalesPartyTypeToPartyDocument() {
        SalesPartyType salesPartyType = new SalesPartyType()
                .withID(new IDType().withValue("Hello"))
                .withName(new TextType().withValue("name"))
                .withCountryID(new IDType().withValue("BEL"))
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedStructuredAddresses(new StructuredAddressType()
                        .withBlockName(new TextType().withValue("Never made it as a wise man")))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType()
                        .withName(new TextType().withValue("I couldn't cut it as a poor man stealin'"))
                        .withPostalStructuredAddresses(new StructuredAddressType()
                                .withBlockName(new TextType().withValue("Should pick this address!"))));

        PartyDocument partyDocument = mapper.map(salesPartyType, PartyDocument.class);

        assertEquals("Hello", partyDocument.getParty().getExtId());
        assertEquals("name", partyDocument.getParty().getName());
        assertEquals("BEL", partyDocument.getCountry());
        assertEquals(PartyRole.BUYER, partyDocument.getRole());
        assertEquals("Should pick this address!", partyDocument.getParty().getAddress().getBlock());
        assertEquals("I couldn't cut it as a poor man stealin'", partyDocument.getParty().getFluxOrganizationName());
    }

    @Test
    public void testMapPartyDocumentToSalesPartyType() {
        Party party = new Party()
                .extId("Yow")
                .fluxOrganizationName("Don't be fooled by the rocks that I got")
                .address(new Address().block("I'm still Jenny from the block"))
                .name("name");

        PartyDocument partyDocument = new PartyDocument()
                .party(party)
                .country("FRA")
                .role(PartyRole.RECIPIENT);

        SalesPartyType salesPartyType = mapper.map(partyDocument, SalesPartyType.class);

        assertEquals("Yow", salesPartyType.getID().getValue());
        assertEquals("Don't be fooled by the rocks that I got", salesPartyType.getSpecifiedFLUXOrganization().getName().getValue());
        assertEquals("I'm still Jenny from the block", salesPartyType.getSpecifiedFLUXOrganization().getPostalStructuredAddresses().get(0).getBlockName().getValue());
        assertEquals("name", salesPartyType.getName().getValue());
        assertEquals("FRA", salesPartyType.getCountryID().getValue());
        assertEquals(Lists.newArrayList(new CodeType().withValue("RECIPIENT")), salesPartyType.getRoleCodes());


    }

    @Test
    public void testMapSalesDocumentTypeToDocument() {
        DateTime date = new DateTime(2017, 2, 1, 15, 0);
        SalesEventType salesEventType = new SalesEventType()
                .withOccurrenceDateTime(new DateTimeType().withDateTime(date));
        SalesDocumentType salesDocumentType = new SalesDocumentType()
                .withIDS(new IDType().withValue("ID"))
                .withSpecifiedSalesEvents(salesEventType)
                .withCurrencyCode(new CodeType().withValue("EUR"))
                .withTotalSalesPrice(new SalesPriceType().withChargeAmounts(new AmountType().withValue(BigDecimal.TEN)))
                .withSpecifiedFishingActivities(new FishingActivityType())
                .withSpecifiedSalesParties(new SalesPartyType())
                .withSpecifiedFLUXLocations(new FLUXLocationType())
                .withSpecifiedSalesBatches(new SalesBatchType().withSpecifiedAAPProducts(new AAPProductType(), new AAPProductType(), new AAPProductType(), new AAPProductType()));

        Document document = mapper.map(salesDocumentType, Document.class);

        assertEquals("ID", document.getExtId());
        assertEquals(date, document.getOccurrence());
        assertEquals("EUR", document.getCurrency());
        assertEquals(BigDecimal.TEN, document.getTotalPrice());
        assertEquals(new FishingActivity(), document.getFishingActivity());
        assertEquals(Lists.newArrayList(new PartyDocument()), document.getPartyDocuments());
        assertEquals(new FluxLocation(), document.getFluxLocation());
        assertEquals(4, document.getProducts().size());

        for (Product product : document.getProducts()) {
            assertEquals(document, product.getDocument());
        }

    }

    @Test
    public void testMapDocumentToSalesDocumentType() {
        DateTime date = new DateTime(2017, 2, 1, 15, 0);
        Document document = new Document()
                .extId("ID")
                .occurrence(date)
                .currency("EUR")
                .totalPrice(BigDecimal.ONE)
                .fishingActivity(new FishingActivity())
                .partyDocuments(Lists.newArrayList(new PartyDocument()))
                .fluxLocation(new FluxLocation())
                .products(Lists.newArrayList(new Product(), new Product(), new Product()));

        SalesDocumentType salesDocumentType = mapper.map(document, SalesDocumentType.class);

        assertEquals("ID", salesDocumentType.getIDS().get(0).getValue());
        assertEquals(date, salesDocumentType.getSpecifiedSalesEvents().get(0).getOccurrenceDateTime().getDateTime());
        assertEquals("EUR", salesDocumentType.getCurrencyCode().getValue());
        assertEquals(BigDecimal.ONE, salesDocumentType.getTotalSalesPrice().getChargeAmounts().get(0).getValue());
        assertEquals(Lists.newArrayList(new FishingActivityType()), salesDocumentType.getSpecifiedFishingActivities());
        assertEquals(Lists.newArrayList(new SalesPartyType()), salesDocumentType.getSpecifiedSalesParties());
        assertEquals(Lists.newArrayList(new FLUXLocationType()), salesDocumentType.getSpecifiedFLUXLocations());
        assertEquals(1, salesDocumentType.getSpecifiedSalesBatches().size());
        assertEquals(3, salesDocumentType.getSpecifiedSalesBatches().get(0).getSpecifiedAAPProducts().size());
    }

    @Test
    public void testMapStructuredAddressTypeToAddress() throws Exception {
        StructuredAddressType structuredAddressType = new StructuredAddressType()
                .withBlockName(new TextType().withValue("blockName"))
                .withBuildingName(new TextType().withValue("buildingName"))
                .withCityName(new TextType().withValue("cityName"))
                .withCitySubDivisionName(new TextType().withValue("citySubDivisionName"))
                .withCountryID(new IDType().withValue("countryId"))
                .withCountryName(new TextType().withValue("countryName"))
                .withCountrySubDivisionName(new TextType().withValue("countrySubDivisionName"))
                .withID(new IDType().withValue("id"))
                .withPlotIdentification(new TextType().withValue("plotIdentification"))
                .withPostOfficeBox(new TextType().withValue("postOfficeBox"))
                .withStreetName(new TextType().withValue("streetName"));

        Address address = mapper.map(structuredAddressType, Address.class);

        assertEquals("blockName", address.getBlock());
        assertEquals("buildingName", address.getBuilding());
        assertEquals("cityName", address.getCity());
        assertEquals("citySubDivisionName", address.getCitySubDivision());
        assertEquals("countryId", address.getCountryCode());
        assertEquals("countryName", address.getCountryName());
        assertEquals("countrySubDivisionName", address.getCountrySubDivision());
        assertEquals("id", address.getExtId());
        assertEquals("plotIdentification", address.getPlotId());
        assertEquals("postOfficeBox", address.getPostOfficeBox());
        assertEquals("streetName", address.getStreet());
        assertNull(address.getId());
    }

    @Test
    public void testMapAddressToStructuredAddressType() throws Exception {
        Address address = new Address()
                .block("blockName")
                .building("buildingName")
                .city("cityName")
                .citySubDivision("citySubDivisionName")
                .countryCode("countryId")
                .countryName("countryName")
                .countrySubDivision("countrySubDivisionName")
                .extId("id")
                .plotId("plotIdentification")
                .postOfficeBox("postOfficeBox")
                .street("streetName");

        StructuredAddressType structuredAddressType = mapper.map(address, StructuredAddressType.class);

        assertEquals("blockName", structuredAddressType.getBlockName().getValue());
        assertEquals("buildingName", structuredAddressType.getBuildingName().getValue());
        assertEquals("cityName", structuredAddressType.getCityName().getValue());
        assertEquals("citySubDivisionName", structuredAddressType.getCitySubDivisionName().getValue());
        assertEquals("countryId", structuredAddressType.getCountryID().getValue());
        assertEquals("countryName", structuredAddressType.getCountryName().getValue());
        assertEquals("countrySubDivisionName", structuredAddressType.getCountrySubDivisionName().getValue());
        assertEquals("id", structuredAddressType.getID().getValue());
        assertEquals("plotIdentification", structuredAddressType.getPlotIdentification().getValue());
        assertEquals("postOfficeBox", structuredAddressType.getPostOfficeBox().getValue());
        assertEquals("streetName", structuredAddressType.getStreetName().getValue());
    }

    @Test
    public void testMapFLUXLocationTypeToFluxLocation() throws Exception {
        FLUXGeographicalCoordinateType fluxGeographicalCoordinateType =
                new FLUXGeographicalCoordinateType()
                        .withLatitudeMeasure(new MeasureType().withValue(BigDecimal.valueOf(12.15)))
                        .withLongitudeMeasure(new MeasureType().withValue(BigDecimal.valueOf(1.5)));

        FLUXLocationType fluxLocationType = new FLUXLocationType()
                .withID(new IDType().withValue("id"))
                .withTypeCode(new CodeType().withValue("typeCode"))
                .withCountryID(new IDType().withValue("BEL"))
                .withPhysicalStructuredAddress(new StructuredAddressType())
                .withSpecifiedPhysicalFLUXGeographicalCoordinate(fluxGeographicalCoordinateType);

        FluxLocation fluxLocation = mapper.map(fluxLocationType, FluxLocation.class);

        assertEquals("id", fluxLocation.getExtId());
        assertEquals("typeCode", fluxLocation.getType());
        assertEquals("BEL", fluxLocation.getCountryCode());
        assertEquals(new Address(), fluxLocation.getAddress());
        assertEquals(12.15, fluxLocation.getLatitude(), 0.001);
        assertEquals(1.5, fluxLocation.getLongitude(), 0.001);
        assertNull(fluxLocation.getId());
    }

    @Test
    public void testMapFLUXLocationToFluxLocationType() throws Exception {
        FluxLocation fluxLocation = new FluxLocation()
                .extId("id")
                .type("typeCode")
                .countryCode("BEL")
                .address(new Address())
                .latitude(12.15)
                .longitude(1.5);

        FLUXLocationType fluxLocationType = mapper.map(fluxLocation, FLUXLocationType.class);

        assertEquals("id", fluxLocationType.getID().getValue());
        assertEquals("typeCode", fluxLocationType.getTypeCode().getValue());
        assertEquals("BEL", fluxLocationType.getCountryID().getValue());
        assertEquals(new StructuredAddressType(), fluxLocationType.getPhysicalStructuredAddress());
        assertEquals(BigDecimal.valueOf(12.15), fluxLocationType.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLatitudeMeasure().getValue());
        assertEquals(BigDecimal.valueOf(1.5), fluxLocationType.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLongitudeMeasure().getValue());
    }

    @Test
    public void testMapFishingActivityTypeToFishingActivity() {
        DateTime startDate = new DateTime(2017, 1, 1, 10, 0);
        DateTime endDate = new DateTime(2017, 2, 2, 10, 0);
        DelimitedPeriodType period = new DelimitedPeriodType()
                .withStartDateTime(new DateTimeType().withDateTime(startDate))
                .withEndDateTime(new DateTimeType().withDateTime(endDate));
        FishingActivityType fishingActivityType = new FishingActivityType()
                .withIDS(new IDType().withValue("ID"))
                .withTypeCode(new CodeType().withValue("type"))
                .withSpecifiedDelimitedPeriods(period)
                .withSpecifiedFishingTrip(new FishingTripType().withIDS(new IDType().withValue("Fishing trip ID")))
                .withRelatedFLUXLocations(new FLUXLocationType())
                .withRelatedVesselTransportMeans(new VesselTransportMeansType());

        FishingActivity fishingActivity = mapper.map(fishingActivityType, FishingActivity.class);

        assertEquals("ID", fishingActivity.getExtId());
        assertEquals("type", fishingActivity.getType());
        assertEquals(startDate, fishingActivity.getStartDate());
        assertEquals(endDate, fishingActivity.getEndDate());
        assertEquals("Fishing trip ID", fishingActivity.getFishingTripId());
        assertEquals(new FluxLocation(), fishingActivity.getLocation());
        assertEquals(new Vessel().vesselContacts(new ArrayList<VesselContact>()), fishingActivity.getVessel());
        assertNull(fishingActivity.getId());
    }

    @Test
    public void testMapFishingActivityToFishingActivityType() {
        DateTime startDate = new DateTime(2017, 1, 1, 10, 0);
        DateTime endDate = new DateTime(2017, 2, 2, 10, 0);

        FishingActivity fishingActivity = new FishingActivity()
                .extId("ID")
                .type("type")
                .startDate(startDate)
                .endDate(endDate)
                .fishingTripId("Fishing trip ID")
                .location(new FluxLocation())
                .vessel(new Vessel());

        FishingActivityType fishingActivityType = mapper.map(fishingActivity, FishingActivityType.class);

        assertEquals("ID", fishingActivityType.getIDS().get(0).getValue());
        assertEquals("type", fishingActivityType.getTypeCode().getValue());
        assertEquals(startDate, fishingActivityType.getSpecifiedDelimitedPeriods().get(0).getStartDateTime().getDateTime());
        assertEquals(endDate, fishingActivityType.getSpecifiedDelimitedPeriods().get(0).getEndDateTime().getDateTime());
        assertEquals("Fishing trip ID", fishingActivityType.getSpecifiedFishingTrip().getIDS().get(0).getValue());
        assertEquals(Lists.newArrayList(new FLUXLocationType()), fishingActivityType.getRelatedFLUXLocations());
        assertEquals(Lists.newArrayList(new VesselTransportMeansType()), fishingActivityType.getRelatedVesselTransportMeans());
    }

    @Test
    public void testMapVesselTransportMeansTypeToVessel() {
        VesselTransportMeansType vesselTransportMeansType = new VesselTransportMeansType()
                .withIDS(new IDType().withValue("ID"))
                .withNames(new TextType().withValue("name"))
                .withRegistrationVesselCountry(new VesselCountryType().withID(new IDType().withValue("BE")))
                .withSpecifiedContactParties(new ContactPartyType());


        Vessel vessel = mapper.map(vesselTransportMeansType, Vessel.class);

        assertEquals("ID", vessel.getExtId());
        assertEquals("name", vessel.getName());
        assertEquals("BE", vessel.getCountryCode());

        assertEquals(Lists.newArrayList(new VesselContact()), vessel.getVesselContacts());
        assertNull(vessel.getId());
    }

    @Test
    public void testMapVesselToVesselTransportMeansType() {
        VesselContact emptyVesselContact = new VesselContact().contact(new Contact());

        Vessel vessel = new Vessel()
                .extId("ID")
                .name("name")
                .countryCode("BE")
                .vesselContacts(Lists.newArrayList(emptyVesselContact));

        VesselTransportMeansType vesselTransportMeansType = mapper.map(vessel, VesselTransportMeansType.class);

        assertEquals("ID", vesselTransportMeansType.getIDS().get(0).getValue());
        assertEquals("name", vesselTransportMeansType.getNames().get(0).getValue());
        assertEquals("BE", vesselTransportMeansType.getRegistrationVesselCountry().getID().getValue());
        assertEquals(Lists.newArrayList(new ContactPartyType()), vesselTransportMeansType.getSpecifiedContactParties());
    }

    @Test
    public void testMapContactPartyTypeToVesselContact() {
        ContactPersonType contactPersonType = new ContactPersonType()
                .withAlias(new TextType().withValue("alias"))
                .withTitle(new TextType().withValue("title"))
                .withGivenName(new TextType().withValue("givenName"))
                .withFamilyName(new TextType().withValue("familyName"))
                .withNameSuffix(new TextType().withValue("nameSuffix"))
                .withGenderCode(new CodeType().withValue("gender"))
                .withMiddleName(new TextType().withValue("middleName"));

        ContactPartyType contactPartyType = new ContactPartyType()
                .withRoleCodes(new CodeType().withValue("test"))
                .withSpecifiedContactPersons(contactPersonType);

        VesselContact vesselContact = mapper.map(contactPartyType, VesselContact.class);
        Contact contact = vesselContact.getContact();

        assertEquals("test", vesselContact.getRole());
        assertEquals("alias", contact.getAlias());
        assertEquals("title", contact.getTitle());
        assertEquals("givenName", contact.getGivenName());
        assertEquals("familyName", contact.getFamilyName());
        assertEquals("nameSuffix", contact.getNameSuffix());
        assertEquals("gender", contact.getGender());
        assertEquals("middleName", contact.getMiddleName());

    }

    @Test
    public void testMapVesselContactToContactPartyType() {
        Contact contact = new Contact()
                .alias("alias")
                .title("title")
                .givenName("givenName")
                .familyName("familyName")
                .nameSuffix("nameSuffix")
                .gender("gender")
                .middleName("middleName");

        VesselContact vesselContact = new VesselContact()
                .role("test")
                .contact(contact);

        ContactPartyType contactPartyType = mapper.map(vesselContact, ContactPartyType.class);
        ContactPersonType contactPersonType = contactPartyType.getSpecifiedContactPersons().get(0);


        assertEquals(Lists.newArrayList(new CodeType().withValue("test")), contactPartyType.getRoleCodes());
        assertEquals("alias", contactPersonType.getAlias().getValue());
        assertEquals("title", contactPersonType.getTitle().getValue());
        assertEquals("givenName", contactPersonType.getGivenName().getValue());
        assertEquals("familyName", contactPersonType.getFamilyName().getValue());
        assertEquals("nameSuffix", contactPersonType.getNameSuffix().getValue());
        assertEquals("gender", contactPersonType.getGenderCode().getValue());
        assertEquals("middleName", contactPersonType.getMiddleName().getValue());
    }

    @Test
    public void testMapAuctionSaleTypeToAuctionSale() {
        AuctionSaleType auctionSaleType = new AuctionSaleType() .withCountryCode("BE")
                                                                .withSalesCategory(SalesCategoryType.FIRST_SALE);

        AuctionSale auctionSale = mapper.map(auctionSaleType, AuctionSale.class);

        assertEquals("BE", auctionSale.getCountryCode());
        assertEquals(SalesCategory.FIRST_SALE, auctionSale.getCategory());
    }

    @Test
    public void testMapAuctionSaleToAuctionSaleType() {
        AuctionSale auctionSale = new AuctionSale()
                                        .category(SalesCategory.FIRST_SALE)
                                        .countryCode("BE");

        AuctionSaleType auctionSaleType = mapper.map(auctionSale, AuctionSaleType.class);

        assertEquals("BE", auctionSaleType.getCountryCode());
        assertEquals(SalesCategoryType.FIRST_SALE, auctionSaleType.getSalesCategory());
    }

    @Test
    public void testMapSavedSearchGroupToSavedSearchGroupTest() {
        Map<String, String> fields = new HashMap<>();
        fields.put("We turn red and we turn green", "It's the craziest thing I've ever seen");
        eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup savedSearchGroup = new eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup()
                .fields(new HashMap<String, String>())
                .name("name")
                .user("user")
                .fields(fields)
                .id(10);

        eu.europa.ec.fisheries.schema.sales.SavedSearchGroup searchGroup = mapper.map(savedSearchGroup, eu.europa.ec.fisheries.schema.sales.SavedSearchGroup.class);

        assertEquals(1, searchGroup.getFields().size());
        assertEquals("We turn red and we turn green", searchGroup.getFields().get(0).getKey());
        assertEquals("It's the craziest thing I've ever seen", searchGroup.getFields().get(0).getValue());
        assertEquals("name", searchGroup.getName());
        assertEquals("user", searchGroup.getUser());
        assertEquals(Integer.valueOf(10), searchGroup.getId());

    }

    @Test
    public void testMapSavedSearchGroupTestToSavedSearchGroup() {
        eu.europa.ec.fisheries.schema.sales.SavedSearchGroup savedSearchGroup = new eu.europa.ec.fisheries.schema.sales.SavedSearchGroup()
                .withName("name")
                .withUser("user")
                .withFields(new SavedSearchGroupField() .withKey("We turn red and we turn green")
                                                        .withValue("It's the craziest thing I've ever seen"))
                .withId(10);


        eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup searchGroup = mapper.map(savedSearchGroup, eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup.class);

        assertEquals(1, searchGroup.getFields().size());
        assertEquals("It's the craziest thing I've ever seen", searchGroup.getFields().get("We turn red and we turn green"));
        assertEquals("name", searchGroup.getName());
        assertEquals("user", searchGroup.getUser());
        assertEquals(Integer.valueOf(10), searchGroup.getId());

    }

    @Test
    public void testMapSalesQueryTypeToQuery() {
        SalesQueryType salesQueryType = createSalesQueryType();

        Query mappedQuery = mapper.map(salesQueryType, Query.class);

        assertEquals(createQuery(), mappedQuery);
    }

    @Test
    public void testMapQueryToSalesQueryType() {
        Query query = createQuery();

        SalesQueryType mappedQuery = mapper.map(query, SalesQueryType.class);

        assertEquals(createSalesQueryType(), mappedQuery);
    }

    @Test
    public void testMapResponseToFLUXResponseDocumentType() {
        DateTime now = DateTime.now();
        Response response = createResponse(now);
        FLUXResponseDocumentType fluxResponseDocumentType = createFluxResponseDocumentType(now);

        FLUXResponseDocumentType mappedResult = mapper.map(response, FLUXResponseDocumentType.class);
        assertEquals(fluxResponseDocumentType, mappedResult);
    }

    @Test
    public void testMapFLUXResponseDocumentTypeToResponse() {
        DateTime now = DateTime.now();
        Response response = createResponse(now);
        FLUXResponseDocumentType fluxResponseDocumentType = createFluxResponseDocumentType(now);

        Response mappedResult = mapper.map(fluxResponseDocumentType, Response.class);
        assertEquals(response, mappedResult);
    }

    private Response createResponse(DateTime now) {
        return new Response()
                .extId("extId")
                .referencedId("referencedId")
                .creationDateTime(now)
                .responseCode("responseCode")
                .remarks("remarks")
                .rejectionReason("rejectionReason")
                .typeCode("typeCode")
                .respondentFLUXParty("respondentFLUXParty");
    }

    private FLUXResponseDocumentType createFluxResponseDocumentType(DateTime now) {
        return new FLUXResponseDocumentType()
                .withIDS(Arrays.asList(new IDType().withValue("extId")))
                .withReferencedID(new IDType().withValue("referencedId"))
                .withCreationDateTime(new DateTimeType().withDateTime(now))
                .withResponseCode(new CodeType().withValue("responseCode"))
                .withRemarks(new TextType().withValue("remarks"))
                .withRejectionReason(new TextType().withValue("rejectionReason"))
                .withTypeCode(new CodeType().withValue("typeCode"))
                .withRespondentFLUXParty(new FLUXPartyType().withIDS(
                        new IDType().withValue("respondentFLUXParty")));
    }

    private Query createQuery() {
        QueryParameterType queryParameter = new QueryParameterType()
                .typeCode("typeCode")
                .valueCode("valueCode")
                .valueDateTime(DateTime.parse("1995-11-24"))
                .valueID("idType");

        return new Query()
                .queryType("typeCode")
                .extId("id")
                .startDate(DateTime.parse("2016-08-01"))
                .endDate(DateTime.parse("2060-08-01"))
                .submittedDate(DateTime.parse("2017-09-05"))
                .submitterFLUXParty("fluxParty idType")
                .parameters(Arrays.asList(queryParameter));
    }

    private SalesQueryType createSalesQueryType() {
        SalesQueryParameterType salesQueryParameterType = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("typeCode"))
                .withValueCode(new CodeType().withValue("valueCode"))
                .withValueDateTime(new DateTimeType().withDateTime(DateTime.parse("1995-11-24")))
                .withValueID(new IDType().withValue("idType"));

        return new SalesQueryType()
                .withTypeCode(new CodeType().withValue("typeCode"))
                .withID(new IDType().withValue("id"))
                .withSpecifiedDelimitedPeriod(
                        new DelimitedPeriodType()
                                .withStartDateTime(new DateTimeType().withDateTime(DateTime.parse("2016-08-01")))
                                .withEndDateTime(new DateTimeType().withDateTime(DateTime.parse("2060-08-01"))))
                .withSubmittedDateTime(new DateTimeType().withDateTime(DateTime.parse("2017-09-05")))
                .withSubmitterFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("fluxParty idType")))
                .withSimpleSalesQueryParameters(salesQueryParameterType);
    }



    @Test
    public void testMapFluxReportToReportSummaryWhenAuctionSalesIsProvided() {
        DateTime deletion = new DateTime();
        DateTime creation = new DateTime(2017, 4, 2, 15, 0);
        DateTime occurrence = new DateTime(2017, 3, 2, 15, 0);
        DateTime landingDate = new DateTime(2017, 3, 3, 14, 0);

        FluxReport fluxReport = new FluxReport()
                .creation(creation)
                .deletion(deletion)
                .extId("fluxReportDocumentExtId")
                .purpose(Purpose.ORIGINAL)
                .auctionSale(new AuctionSale()
                    .category(SalesCategory.NEGOTIATED_SALE))
                .previousFluxReportExtId("Heya")
                .document(new Document()
                    .occurrence(occurrence)
                    .fishingActivity(new FishingActivity()
                        .startDate(landingDate)
                        .vessel(new Vessel()
                            .extId("vesselExtId")
                            .name("vesselName")
                            .countryCode("FRA"))
                        .location(new FluxLocation()
                            .extId("NED")))
                    .fluxLocation(new FluxLocation()
                            .extId("BEL"))
                    .partyDocuments(Arrays.asList(
                            new PartyDocument()
                                .role(PartyRole.BUYER)
                                .party(new Party()
                                    .name("Mathiblaa")),
                            new PartyDocument()
                                .role(PartyRole.PROVIDER)
                                .party(new Party()
                                    .name("Superstijn"))
                                )
                    )
                );

        ReportSummary reportSummary = mapper.map(fluxReport, ReportSummary.class);

        assertEquals(SalesCategoryType.NEGOTIATED_SALE, reportSummary.getCategory());
        assertEquals("fluxReportDocumentExtId", reportSummary.getExtId());
        assertEquals(new DateTime(2017, 3, 2, 15, 0), reportSummary.getOccurrence());
        assertEquals("vesselName", reportSummary.getVesselName());
        assertEquals("vesselExtId", reportSummary.getVesselExtId());
        assertEquals("FRA", reportSummary.getFlagState());
        assertEquals(new DateTime(2017, 3, 3, 14, 0), reportSummary.getLandingDate());
        assertEquals("NED", reportSummary.getLandingPort());
        assertEquals("BEL", reportSummary.getLocation());
        assertEquals("Mathiblaa", reportSummary.getBuyer());
        assertEquals("Superstijn", reportSummary.getProvider());
        assertEquals("Heya", reportSummary.getReferencedId());
        assertEquals(deletion, reportSummary.getDeletion());
        assertEquals(creation, reportSummary.getCreation());
        assertEquals(Purpose.ORIGINAL.toString(), reportSummary.getPurpose());
    }

    @Test
    public void testMapFluxReportToReportSummaryWhenAuctionSalesIsNotProvided() {
        DateTime deletion = new DateTime();
        DateTime occurrence = new DateTime(2017, 3, 2, 15, 0);
        DateTime landingDate = new DateTime(2017, 3, 3, 14, 0);

        FluxReport fluxReport = new FluxReport()
                .deletion(deletion)
                .extId("fluxReportDocumentExtId")
                .purpose(Purpose.ORIGINAL)
                .previousFluxReportExtId("Heya")
                .document(new Document()
                        .occurrence(occurrence)
                        .fishingActivity(new FishingActivity()
                                .startDate(landingDate)
                                .vessel(new Vessel()
                                        .extId("vesselExtId")
                                        .name("vesselName")
                                        .countryCode("FRA"))
                                .location(new FluxLocation()
                                        .extId("NED")))
                        .fluxLocation(new FluxLocation()
                                .extId("BEL"))
                        .partyDocuments(Arrays.asList(
                                new PartyDocument()
                                        .role(PartyRole.BUYER)
                                        .party(new Party()
                                                .name("Mathiblaa")),
                                new PartyDocument()
                                        .role(PartyRole.PROVIDER)
                                        .party(new Party()
                                                .name("Superstijn"))
                                )
                        )
                );

        ReportSummary reportSummary = mapper.map(fluxReport, ReportSummary.class);

        assertEquals(SalesCategoryType.FIRST_SALE, reportSummary.getCategory());
        assertEquals("fluxReportDocumentExtId", reportSummary.getExtId());
        assertEquals(new DateTime(2017, 3, 2, 15, 0), reportSummary.getOccurrence());
        assertEquals("vesselName", reportSummary.getVesselName());
        assertEquals("vesselExtId", reportSummary.getVesselExtId());
        assertEquals("FRA", reportSummary.getFlagState());
        assertEquals(new DateTime(2017, 3, 3, 14, 0), reportSummary.getLandingDate());
        assertEquals("NED", reportSummary.getLandingPort());
        assertEquals("BEL", reportSummary.getLocation());
        assertEquals("Mathiblaa", reportSummary.getBuyer());
        assertEquals("Superstijn", reportSummary.getProvider());
        assertEquals("Heya", reportSummary.getReferencedId());
        assertEquals(deletion, reportSummary.getDeletion());
        assertEquals(Purpose.ORIGINAL.toString(), reportSummary.getPurpose());
    }


}