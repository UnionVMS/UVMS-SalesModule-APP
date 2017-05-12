package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportServiceExportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportServiceHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.SalesDetailsHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.SearchReportsHelper;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceBeanTest {

    @InjectMocks
    private ReportServiceBean reportServiceBean;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Mock
    private MapperFacade mapper;

    @Mock
    private SearchReportsHelper searchReportsHelper;

    @Mock
    private SalesDetailsHelper salesDetailsHelper;

    @Mock
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @Mock
    private RulesService rulesService;

    @Mock
    private ReportServiceHelper reportServiceHelper;

    @Test(expected = IllegalArgumentException.class)
    public void testFindSalesDetailsWhenExtIdIsNull() throws Exception {
        reportServiceBean.findSalesDetails(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSalesDetailsWhenExtIdIsEmpty() throws Exception {
        reportServiceBean.findSalesDetails("");
    }

    @Test
    public void testFindSalesDetails() throws Exception {
        //data set
        String extId = "extId";

        Report report = new Report();

        SalesDetailsDto salesDetailsDto = new SalesDetailsDto();

        //mock
        when(reportDomainModel.findSalesDetails(extId)).thenReturn(report);
        when(mapper.map(report, SalesDetailsDto.class)).thenReturn(salesDetailsDto);

        //execute
        reportServiceBean.findSalesDetails(extId);

        //verifications and assertions
        verify(reportDomainModel).findSalesDetails(extId);
        verify(mapper).map(report, SalesDetailsDto.class);
        verify(salesDetailsHelper).convertPricesInLocalCurrency(salesDetailsDto, report);
        verify(salesDetailsHelper).calculateTotals(salesDetailsDto);
        verify(salesDetailsHelper).enrichWithLocation(salesDetailsDto);
        verify(salesDetailsHelper).enrichWithVesselInformation(salesDetailsDto, report);
        verifyNoMoreInteractions(reportDomainModel, mapper, salesDetailsHelper);
    }

    @Test
    public void testExportSelectedDocuments() throws ServiceException {
        //set up
        reportServiceBean.setReportServiceExportHelper(new ReportServiceExportHelper());

        //data set
        PageCriteriaDto<ReportQueryFilterDto> criteria = new PageCriteriaDto<>();
        criteria.filters(new ReportQueryFilterDto().excludeFluxReportIds(Arrays.asList("abc")))
                .pageIndex(0)
                .pageSize(Integer.MAX_VALUE);

        ExportListsDto exportListsDto = new ExportListsDto().criteria(criteria);

        //data set for search
        ReportQuery query = new ReportQuery();

        Report report = new Report();
        List<Report> reports = Lists.newArrayList(report);

        List<ReportListDto> reportDtos = Arrays.asList(new ReportListDto());

        //mock for search
        mockForSearch(criteria, query, reports, reportDtos);

        //mock for export
        when(mapper.mapAsList(reportDtos, ReportListExportDto.class)).thenReturn(getReportListExportDtos());

        //execute
        List<List<String>> csv = reportServiceBean.exportSelectedDocuments(exportListsDto);

        //assert
        String landingDate = new DateTime(2017, 3, 2, 0, 0).toString();
        String occurrence = new DateTime(2017, 3, 1, 0, 0).toString();

        final List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Stijn", "Pope"));
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump"));
        assertEquals(expected, csv);
    }

    @Test
    public void testExportDocumentsExportAll() throws ServiceException {
        //set up
        reportServiceBean.setReportServiceExportHelper(new ReportServiceExportHelper());

        //data set
        PageCriteriaDto<ReportQueryFilterDto> criteria = new PageCriteriaDto<>();
        criteria.filters(new ReportQueryFilterDto());

        ExportListsDto exportListsDto = new ExportListsDto().criteria(criteria).exportAll(true);

        //data set for search
        ReportQuery query = new ReportQuery();

        Report report = new Report();
        List<Report> reports = Lists.newArrayList(report);

        List<ReportListDto> reportDtos = Arrays.asList(new ReportListDto());

        //mock for search
        mockForSearch(criteria, query, reports, reportDtos);

        //mock for export
        when(mapper.mapAsList(reportDtos, ReportListExportDto.class)).thenReturn(getReportListExportDtos());

        //execute
        List<List<String>> csv = reportServiceBean.exportSelectedDocuments(exportListsDto);

        //assert
        String landingDate = new DateTime(2017, 3, 2, 0, 0).toString();
        String occurrence = new DateTime(2017, 3, 1, 0, 0).toString();

        final List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Stijn", "Pope"));
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump"));
        assertEquals(expected, csv);
    }

    private void mockForSearch(PageCriteriaDto<ReportQueryFilterDto> criteria, ReportQuery query, List<Report> reports, List<ReportListDto> reportDtos) {
        when(mapper.map(criteria, ReportQuery.class)).thenReturn(query);
        when(reportDomainModel.search(query)).thenReturn(reports);
        when(reportDomainModel.count(query)).thenReturn(15L);
        when(mapper.mapAsList(reports, ReportListDto.class)).thenReturn(reportDtos);
    }

    private List<ReportListExportDto> getReportListExportDtos() {
        ReportListExportDto reportListDto1 = new ReportListExportDto()
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

        ReportListExportDto reportListDto2 = new ReportListExportDto()
                .buyer("Trump")
                .seller("Putin")
                .category(SalesCategoryType.FIRST_SALE)
                .externalMarking("marked for life")
                .flagState("BEL")
                .ircs("ircs")
                .landingDate(new DateTime(2017, 3, 2, 0, 0).toString())
                .landingPort("Garagepoort")
                .location("location")
                .occurrence(new DateTime(2017, 3, 1, 0, 0).toString())
                .vesselName("vesselName");
        return Arrays.asList(reportListDto1, reportListDto2);
    }

    @Test
    public void testSearchWithPageCriteriaDto() throws Exception {
        //data set
        PageCriteriaDto<ReportQueryFilterDto> criteria = new PageCriteriaDto<>();

        ReportQuery query = new ReportQuery();
        query.setPaging(new Paging()
                .withItemsPerPage(10)
                .withPage(1));

        Report report = new Report();
        List<Report> reports = Lists.newArrayList(report);

        ReportListDto reportListDto = new ReportListDto();
        List<ReportListDto> reportDtos = Lists.newArrayList(reportListDto);

        //mock
        when(mapper.map(criteria, ReportQuery.class)).thenReturn(query);
        when(reportDomainModel.search(query)).thenReturn(reports);
        when(reportDomainModel.count(query)).thenReturn(15L);
        when(mapper.mapAsList(reports, ReportListDto.class)).thenReturn(reportDtos);

        //execute
        PagedListDto<ReportListDto> result = reportServiceBean.search(criteria);

        //verify and assert
        verify(mapper).map(criteria, ReportQuery.class);
        verify(searchReportsHelper).prepareVesselFreeTextSearch(query);
        verify(searchReportsHelper).prepareSorting(query);
        verify(reportDomainModel).search(query);
        verify(reportDomainModel).count(query);
        verify(mapper).mapAsList(reports, ReportListDto.class);
        verify(searchReportsHelper).enrichWithVesselInformation(reportDtos);
        verifyNoMoreInteractions(searchReportsHelper, reportDomainModel, mapper);

        assertEquals(1, result.getCurrentPage());
        assertSame(reportDtos, result.getItems());
        assertEquals(10, result.getRowsPerPage());
        assertEquals(2, result.getTotalNumberOfPages());
        assertEquals(15, result.getTotalNumberOfRecords());
    }

    @Test
    public void testSearchWithFluxSalesReportMessage() throws ServiceException {
        //data set
        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                        .withID(new IDType().withValue("value")).withSubmitterFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("BEL"))));
        ReportQuery reportQuery = new ReportQuery();
        List<Report> reports = new ArrayList<>();
        ValidationResultDocumentType validationResultDocumentType = new ValidationResultDocumentType();
        FLUXPartyType fluxPartyType = new FLUXPartyType();

        FLUXResponseDocumentType fluxResponseDocumentType = new FLUXResponseDocumentType()
                .withIDS(new IDType().withValue(UUID.randomUUID().toString()))
                .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now()))
                .withReferencedID(fluxSalesQueryMessage.getSalesQuery().getID())
                .withRespondentFLUXParty(fluxPartyType);
        FLUXSalesResponseMessage fluxSalesResponseMessage = new FLUXSalesResponseMessage().withFLUXResponseDocument(fluxResponseDocumentType);

        //mock
        doReturn(reportQuery).when(mapper).map(fluxSalesQueryMessage, ReportQuery.class);
        doReturn(reports).when(reportDomainModel).search(reportQuery);
        doReturn(fluxSalesResponseMessage).when(fluxSalesResponseMessageFactory).create(fluxSalesQueryMessage, reports, validationResultDocumentType);
        doNothing().when(rulesService).sendResponseToRules(fluxSalesResponseMessage, "BEL");

        //execute
        reportServiceBean.search(fluxSalesQueryMessage);

        //verify and assert
        verify(mapper).map(fluxSalesQueryMessage, ReportQuery.class);
        verify(reportDomainModel).search(reportQuery);
    }

    @Test
    public void testSaveReportWhenPurposeIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHost() throws ServiceException {
        Report report = new Report();

        //execute
        reportServiceBean.saveReport(report);

        //verify and assert
        verify(reportDomainModel).create(report);
        verify(reportServiceHelper).sendResponseToSenderOfReport(report);
        verify(reportServiceHelper).forwardReportToOtherRelevantParties(report);
    }

}