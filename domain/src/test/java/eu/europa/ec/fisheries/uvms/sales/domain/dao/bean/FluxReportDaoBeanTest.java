package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.SalesCategory;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.*;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FluxReportDaoBeanTest extends AbstractDaoTest<FluxReportDaoBean> {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        dao.productDao = new ProductDaoBean();
        dao.productDao.setEntityManager(dao.em);
    }

    @Test
    @DataSet(expectedData = "data/FluxReportDaoBeanTest-testCreateWithoutPreviousReport-expected.xml")
    public void testCreateWithoutPreviousReport() throws Exception {
        Address address = new Address()
                .city("test-city")
                .countryCode("BEL")
                .street("test-street");

        FluxLocation fluxLocation = new FluxLocation()
                .extId("test")
                .countryCode("BEL")
                .type("dummy")
                .longitude(1.254654)
                .latitude(40.5256546)
                .address(address);

        FluxLocation fluxLocation2 = new FluxLocation()
                .extId("test2")
                .countryCode("BEL")
                .type("dummy")
                .longitude(1.254654)
                .latitude(40.5256546)
                .address(address);

        Contact contact = new Contact()
                .familyName("Hooft");

        VesselContact vesselContact = new VesselContact()
                .role("OPERATOR")
                .contact(contact);

        Vessel vessel = new Vessel()
                .extId("eeeext")
                .countryCode("FRA")
                .vesselContacts(Lists.newArrayList(vesselContact));

        FishingActivity fishingActivity = new FishingActivity()
                .vessel(vessel)
                .location(fluxLocation)
                .extId("31")
                .type("dummy")
                .fishingTripId("12345")
                .startDate(new DateTime(2017, 1, 1, 10, 0))
                .endDate(new DateTime(2017, 1, 5, 10, 0));

        PartyDocument partyDocument = new PartyDocument()
                .role(PartyRole.BUYER)
                .party(new Party().name("BULTE"));

        Product product = new Product()
                .distributionCategory("distributionCategory")
                .distributionClass("distributionClass")
                .price(BigDecimal.TEN)
                .species("SAL")
                .usage("JUNK");

        Document document = new Document()
                .extId("DocumentExternalID")
                .currency("Euro")
                .occurrence(new DateTime(2001, 9, 11, 0, 0))
                .fishingActivity(fishingActivity)
                .fluxLocation(fluxLocation2)
                .partyDocuments(Lists.newArrayList(partyDocument))
                .products(Lists.newArrayList(product));

        //Create relationships
        product.document(document);

        AuctionSale auctionSale = new AuctionSale()
                .category(SalesCategory.FIRST_SALE)
                .countryCode("BEL");

        FluxReport currentReport = new FluxReport()
                .extId("ExternalID")
                .itemType(FluxReportItemType.SALES_NOTE)
                .creation(new DateTime(1995, 11, 24, 0, 0))
                .fluxReportParty("BEL")
                .document(document)
                .purpose(Purpose.CORRECTION)
                .auctionSale(auctionSale);

        dao.create(currentReport);
    }

    @Test
    @DataSet(expectedData = "data/FluxReportDaoBeanTest-testCreateWithPreviousReport-expected.xml")
    public void testCreateWithPreviousReport() throws Exception {
        //first, create an old report
        FluxReport correctedFluxReport = new FluxReport()
                .extId("OLD_REPORT")
                .purpose(Purpose.ORIGINAL)
                .itemType(FluxReportItemType.SALES_NOTE)
                .creation(new DateTime(1995, 11, 17, 0, 0))
                .fluxReportParty("BEL")
                .document(new Document().currency("EURO").extId("abcdef").occurrence(new DateTime(1990, 11, 11, 0, 0)));

        dao.create(correctedFluxReport);

        //now, lets create a new report, with a link to the old report
        Document document = new Document()
                .extId("DocumentExternalID")
                .currency("Euro")
                .occurrence(new DateTime(2001, 9, 11, 0, 0));

        AuctionSale auctionSale = new AuctionSale()
                .category(SalesCategory.FIRST_SALE)
                .countryCode("BEL");

        FluxReport currentReport = new FluxReport()
                .extId("ExternalID")
                .itemType(FluxReportItemType.SALES_NOTE)
                .creation(new DateTime(1995, 11, 24, 0, 0))
                .fluxReportParty("BEL")
                .document(document)
                .purpose(Purpose.CORRECTION)
                .auctionSale(auctionSale);

        FluxReport reportToBeCorrected = dao.findByExtId("OLD_REPORT");
        currentReport.setPreviousFluxReport(reportToBeCorrected);

        dao.create(currentReport);
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindByExtId-initial.xml")
    public void testFindByExtIdWhenNotNull() {
        FluxReport fluxReport = dao.findByExtId("ExternalID");

        assertEquals("ExternalID", fluxReport.getExtId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindByExtId-initial.xml")
    public void testFindByExtIdWhenNoResultFound() {
        exception.expect(NoResultException.class);
        exception.expectMessage("No entity found for query");

        dao.findByExtId("");
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithoutFilters() throws Exception {
        ReportQuery reportQuery = new ReportQuery();

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());

        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithVesselNameFilterInSameCase() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setVesselName("CoolV");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithVesselNameFilterInDifferentCase() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setVesselName("cOoLv");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithFlagStateFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setFlagState("BEL");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithVesselExtIdsFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withVesselExtIds(Lists.newArrayList("50a", "60abcde", "61a"));
        ReportQuery reportQuery = new ReportQuery().withFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithSalesLocationFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesLocation("73a");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithSalesStartDateFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesStartDate(new DateTime(2017, 1, 5, 0, 0));
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithSalesEndDateFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesEndDate(new DateTime(2017, 1, 5, 0, 0));
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithSalesStartAndEndDateFilterAndInRange() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesStartDate(new DateTime(2017, 1, 19, 0, 0));
        filters.setSalesEndDate(new DateTime(2017, 2, 26, 0, 0));
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithSalesStartAndEndDateFilterAndNotInRange() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesStartDate(new DateTime(2015, 1, 19, 0, 0));
        filters.setSalesEndDate(new DateTime(2016, 2, 26, 0, 0));
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(0, fluxReports.size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithLandingPortFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setLandingPort("71a");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithSalesTypeFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesCategory(SalesCategoryType.VARIOUS_SUPPLY);
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithAnySpeciesFilterAndSomethingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAnySpecies("SAL", "ADQ", "MTP");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithAnySpeciesFilterAndNothingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAnySpecies("ADQ", "MTP");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(0, fluxReports.size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithAllSpeciesFilterAndSomethingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAllSpecies("SAL", "MUS");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithAllSpeciesFilterAndNothingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAllSpecies("SAL", "MTP");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(0, fluxReports.size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchIncludeFluxIdsFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withIncludeFluxReportIds("abc");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchExcludeFluxIdsFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withExcludeFluxReportIds("abc");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithLandingCountryFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setLandingCountry("BEL");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithLandingCountryFilterWhenNothingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setLandingCountry("NLD");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(0, fluxReports.size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithTripIdFilter() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setTripId("12345");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(1, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWithTripIdFilterWhenNothingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setTripId("7989");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(0, fluxReports.size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-big.xml")
    public void testSearchWithPagingWhenPageSizeNotExceeded() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter();
        filters.setSalesEndDate(new DateTime(2017, 1, 5, 0, 0));

        Paging paging = new Paging();
        paging.setPage(1);
        paging.setItemsPerPage(10);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);
        reportQuery.setPaging(paging);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(5, fluxReports.size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-big.xml")
    public void testSearchWithPagingWhenPageSizeExceededAndOnPage1() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAnySpecies("SAL", "ADQ", "MTP");

        Paging paging = new Paging();
        paging.setPage(1);
        paging.setItemsPerPage(5);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);
        reportQuery.setPaging(paging);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(5, fluxReports.size());
        assertEquals(Integer.valueOf(131), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(132), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(133), fluxReports.get(2).getId());
        assertEquals(Integer.valueOf(134), fluxReports.get(3).getId());
        assertEquals(Integer.valueOf(135), fluxReports.get(4).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-big.xml")
    public void testSearchWithPagingWhenPageSizeExceededAndOnPage3() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAnySpecies("SAL", "ADQ", "MTP");

        Paging paging = new Paging();
        paging.setPage(3);
        paging.setItemsPerPage(5);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);
        reportQuery.setPaging(paging);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(5, fluxReports.size());
        assertEquals(Integer.valueOf(141), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(142), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(143), fluxReports.get(2).getId());
        assertEquals(Integer.valueOf(144), fluxReports.get(3).getId());
        assertEquals(Integer.valueOf(145), fluxReports.get(4).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnFlagStateAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.FLAG_STATE);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnCategoryAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.CATEGORY);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnCategoryDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.CATEGORY);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnFlagStateDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.FLAG_STATE);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(126), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnLandingDateAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.LANDING_DATE);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnLandingDateDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.LANDING_DATE);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(126), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnLandingPortAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.LANDING_PORT);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnLandingPortDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.LANDING_PORT);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(126), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnVesselNameAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.VESSEL_NAME);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(126), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(2).getId());
    }


    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnVesselNameDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.VESSEL_NAME);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnSalesDateAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.SALES_DATE);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnSalesDateDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.SALES_DATE);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(125), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnSalesLocationAscending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.ASCENDING);
        sorting.setField(ReportQuerySortField.SALES_LOCATION);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(123), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(126), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-minimal.xml")
    public void testSearchWhenSortingOnSalesLocationDescending() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting();
        sorting.setDirection(SortDirection.DESCENDING);
        sorting.setField(ReportQuerySortField.SALES_LOCATION);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setSorting(sorting);

        List<FluxReport> fluxReports = dao.search(reportQuery);

        assertEquals(3, fluxReports.size());
        assertEquals(Integer.valueOf(126), fluxReports.get(0).getId());
        assertEquals(Integer.valueOf(125), fluxReports.get(1).getId());
        assertEquals(Integer.valueOf(123), fluxReports.get(2).getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-big.xml")
    public void testCountWhenPageSizeExceeded() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withAnySpecies("SAL", "ADQ", "MTP");

        Paging paging = new Paging();
        paging.setPage(2);
        paging.setItemsPerPage(5);

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);
        reportQuery.setPaging(paging);

        assertEquals(20, dao.count(reportQuery));
    }


    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testSearchAndCount-initial-big.xml")
    public void testCountWhenNothingFound() throws Exception {
        ReportQueryFilter filters = new ReportQueryFilter().withIncludeFluxReportIds("898945");
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setFilters(filters);

        assertEquals(0, dao.count(reportQuery));
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindDetailsById-initial.xml")
    public void testFindDetailsById() {
        FluxReport fluxReport = dao.findDetailsByExtId("ExternalID");
        commitTransaction(); //stop the active transaction, so we are sure that this entity is completely loaded,
                                // and no lazy fetching is needed anymore

        //should not throw a "not initialised" exception
        for (Product product : fluxReport.getDocument().getProducts()) {
            for (FluxLocation fluxLocation : product.getOrigins()) {
                fluxLocation.getId();
            }
        }
    }

    @Test(expected = NonUniqueResultException.class)
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindReportsWhichReferTo-initial.xml")
    public void testFindReportsWhichReferToWhenDatabaseContainsIncorrectData() {
        dao.findCorrectionOrDeletionOf("abc");
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindReportsWhichReferTo-initial.xml")
    public void testFindReportsWhichReferToWhenSomethingFound() {
        Optional<FluxReport> fluxReport = dao.findCorrectionOrDeletionOf("ghi");
        assertEquals("yoyoyo", fluxReport.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindReportsWhichReferTo-initial.xml")
    public void testFindReportsWhichReferToWhenNothingFound() {
        assertFalse(dao.findCorrectionOrDeletionOf("bla").isPresent());
    }


    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindLatestVersion-initial.xml")
    public void testFindLatestVersionWhenProvidedReportIsOldVersion() {
        FluxReport fluxReport = new FluxReport()
                .id(123)
                .extId("abc");
        FluxReport latestVersion = dao.findLatestVersion(fluxReport);
        assertEquals(Integer.valueOf(126), latestVersion.getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testFindLatestVersion-initial.xml")
    public void testFindLatestVersionWhenProvidedReportIsNewestVersion() {
        FluxReport fluxReport = new FluxReport()
                .id(126)
                .extId("yoyoyo");
        FluxReport latestVersion = dao.findLatestVersion(fluxReport);
        assertEquals(Integer.valueOf(126), latestVersion.getId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testMapping-initial.xml")
    public void testMappingRelatedTakeOverDocumentsInCaseOfASalesNote() {
        FluxReport salesNote1 = dao.findByIdOrNull(1);
        assertEquals(1, salesNote1.getRelatedTakeOverDocuments().size());
        assertEquals(Integer.valueOf(2), salesNote1.getRelatedTakeOverDocuments().get(0).getId());
        assertEquals(0, salesNote1.getRelatedSalesNotes().size());

        FluxReport salesNote2 = dao.findByIdOrNull(3);
        assertEquals(1, salesNote2.getRelatedTakeOverDocuments().size());
        assertEquals(Integer.valueOf(4), salesNote2.getRelatedTakeOverDocuments().get(0).getId());
        assertEquals(0, salesNote2.getRelatedSalesNotes().size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testMapping-initial.xml")
    public void testMappingRelatedSalesNotesInCaseOfATakeOverDocument() {
        FluxReport takeOverDocument1 = dao.findByIdOrNull(2);
        assertEquals(1, takeOverDocument1.getRelatedSalesNotes().size());
        assertEquals(Integer.valueOf(1), takeOverDocument1.getRelatedSalesNotes().get(0).getId());
        assertEquals(0, takeOverDocument1.getRelatedTakeOverDocuments().size());

        FluxReport takeOverDocument2 = dao.findByIdOrNull(4);
        assertEquals(1, takeOverDocument2.getRelatedSalesNotes().size());
        assertEquals(Integer.valueOf(3), takeOverDocument2.getRelatedSalesNotes().get(0).getId());
        assertEquals(0, takeOverDocument2.getRelatedTakeOverDocuments().size());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testMapping-initial-tod.xml")
    public void findTakeOverDocumentByExtIdWhenExactlyOneReportWasFound() throws Exception {
        Optional<FluxReport> report = dao.findTakeOverDocumentByExtId("abc");

        assertTrue(report.isPresent());
        assertEquals("abc", report.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/FluxReportDaoBeanTest-testMapping-multiple-tod-with-same-id.xml")
    public void findTakeOverDocumentByExtIdWhenMultipleReportsWereFound() throws Exception {
        exception.expectMessage("More than one result found for 'findByExtId' on entity FluxReport in table 'sales_flux_report', this should not happen");
        exception.expect(SalesNonBlockingException.class);
        Optional<FluxReport> report = dao.findTakeOverDocumentByExtId("abc");

        assertTrue(report.isPresent());
        assertEquals("abc", report.get().getExtId());
    }
}
