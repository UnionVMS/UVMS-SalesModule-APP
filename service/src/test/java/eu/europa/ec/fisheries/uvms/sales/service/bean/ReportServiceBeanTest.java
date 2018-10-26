package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceBeanTest {

    @InjectMocks
    private ReportServiceBean reportServiceBean;

    @Mock
    private EcbProxyService ecbProxyService;

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

    @Mock
    private ParameterService parameterService;

    @Mock
    private ReportHelper reportHelper;


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
        verify(salesDetailsHelper).enrichWithOtherRelevantVersions(salesDetailsDto, report);
        verify(salesDetailsHelper).enrichWithRelatedReport(salesDetailsDto, report);
        verify(salesDetailsHelper).enrichProductsWithFactor(salesDetailsDto);
        verifyNoMoreInteractions(reportDomainModel, mapper, salesDetailsHelper, reportHelper);
    }

    @Test
    public void testExportSelectedDocumentsWhenLessThan10Documents() {
        //set up
        reportServiceBean.setReportServiceExportHelper(new ReportServiceExportHelper());

        //data set
        PageCriteriaDto<ReportQueryFilterDto> criteria = new PageCriteriaDto<>();
        criteria.filters(new ReportQueryFilterDto()
                    .excludeFluxReportIds(Arrays.asList("abc")))
                .pageIndex(0)
                .pageSize(Integer.MAX_VALUE);

        ExportListsDto exportListsDto = new ExportListsDto()
                .criteria(criteria)
                .ids(Arrays.asList("1"));

        //data set for search
        ReportQuery query = new ReportQuery();

        ReportSummary reportSummary = new ReportSummary();
        List<ReportSummary> reports = Lists.newArrayList(reportSummary);

        List<ReportListDto> reportDtos = Arrays.asList(new ReportListDto());

        //mock for search
        when(mapper.map(criteria, ReportQuery.class)).thenReturn(query);
        when(reportDomainModel.search(query, false)).thenReturn(reports);
        when(reportDomainModel.count(query)).thenReturn(15L);
        when(mapper.mapAsList(reports, ReportListDto.class)).thenReturn(reportDtos);

        //mock for export
        when(mapper.mapAsList(reportDtos, ReportListExportDto.class)).thenReturn(getReportListExportDtos());

        //execute
        List<List<String>> csv = reportServiceBean.exportSelectedDocuments(exportListsDto);

        //assert
        String landingDate = new DateTime(2017, 3, 2, 0, 0).toString();
        String occurrence = new DateTime(2017, 3, 1, 0, 0).toString();
        String deletion = new DateTime(2017, 3, 3, 0, 0).toString();

        final List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Stijn", "Pope", ""));
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump", deletion));
        assertEquals(expected, csv);
    }


    @Test
    public void testExportSelectedDocumentsWhenMoreThan10Documents() {
        //set up
        reportServiceBean.setReportServiceExportHelper(new ReportServiceExportHelper());

        //data set
        PageCriteriaDto<ReportQueryFilterDto> criteria = new PageCriteriaDto<>();
        criteria.filters(new ReportQueryFilterDto()
                    .excludeFluxReportIds(Arrays.asList("abc")))
                .pageIndex(0)
                .pageSize(Integer.MAX_VALUE);

        ExportListsDto exportListsDto = new ExportListsDto()
                .criteria(criteria)
                .ids(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));

        //data set for search
        ReportQuery query = new ReportQuery();

        ReportSummary reportSummary = new ReportSummary();
        List<ReportSummary> reports = Lists.newArrayList(reportSummary);

        List<ReportListDto> reportDtos = Arrays.asList(new ReportListDto(), new ReportListDto(), new ReportListDto(),
                new ReportListDto(), new ReportListDto(), new ReportListDto(), new ReportListDto(), new ReportListDto(),
                new ReportListDto(), new ReportListDto(), new ReportListDto(), new ReportListDto());

        //mock for search
        when(mapper.map(criteria, ReportQuery.class)).thenReturn(query);
        when(reportDomainModel.search(query, true)).thenReturn(reports);
        when(reportDomainModel.count(query)).thenReturn(15L);
        when(mapper.mapAsList(reports, ReportListDto.class)).thenReturn(reportDtos);

        //mock for export
        when(mapper.mapAsList(reportDtos, ReportListExportDto.class)).thenReturn(getReportListExportDtos());

        //execute
        List<List<String>> csv = reportServiceBean.exportSelectedDocuments(exportListsDto);

        //assert
        String landingDate = new DateTime(2017, 3, 2, 0, 0).toString();
        String occurrence = new DateTime(2017, 3, 1, 0, 0).toString();
        String deletion = new DateTime(2017, 3, 3, 0, 0).toString();

        final List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Stijn", "Pope", ""));
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump", deletion));
        assertEquals(expected, csv);
    }

    @Test
    public void testExportDocumentsExportAll() {
        //set up
        reportServiceBean.setReportServiceExportHelper(new ReportServiceExportHelper());

        //data set
        PageCriteriaDto<ReportQueryFilterDto> criteria = new PageCriteriaDto<>();
        criteria.filters(new ReportQueryFilterDto());

        ExportListsDto exportListsDto = new ExportListsDto().criteria(criteria).exportAll(true);

        //data set for search
        ReportQuery query = new ReportQuery();

        ReportSummary reportSummary = new ReportSummary();
        List<ReportSummary> reports = Lists.newArrayList(reportSummary);

        List<ReportListDto> reportDtos = Arrays.asList(new ReportListDto());

        //mock for search
        when(mapper.map(criteria, ReportQuery.class)).thenReturn(query);
        when(reportDomainModel.search(query, true)).thenReturn(reports);
        when(reportDomainModel.count(query)).thenReturn(15L);
        when(mapper.mapAsList(reports, ReportListDto.class)).thenReturn(reportDtos);

        //mock for export
        when(mapper.mapAsList(reportDtos, ReportListExportDto.class)).thenReturn(getReportListExportDtos());

        //execute
        List<List<String>> csv = reportServiceBean.exportSelectedDocuments(exportListsDto);

        //assert
        String landingDate = new DateTime(2017, 3, 2, 0, 0).toString();
        String occurrence = new DateTime(2017, 3, 1, 0, 0).toString();
        String deletion = new DateTime(2017, 3, 3, 0, 0).toString();

        final List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Stijn", "Pope", ""));
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump", deletion));
        assertEquals(expected, csv);
    }


    private List<ReportListExportDto> getReportListExportDtos() {
        ReportListExportDto reportListDto1 = new ReportListExportDto()
                .buyer("Pope")
                .provider("Stijn")
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
                .provider("Putin")
                .category(SalesCategoryType.FIRST_SALE)
                .externalMarking("marked for life")
                .flagState("BEL")
                .ircs("ircs")
                .landingDate(new DateTime(2017, 3, 2, 0, 0).toString())
                .landingPort("Garagepoort")
                .location("location")
                .occurrence(new DateTime(2017, 3, 1, 0, 0).toString())
                .vesselName("vesselName")
                .deletion(new DateTime(2017, 3, 3, 0, 0));
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

        ReportSummary reportSummary = new ReportSummary();
        List<ReportSummary> reports = Lists.newArrayList(reportSummary);

        ReportListDto reportListDto = new ReportListDto();
        List<ReportListDto> reportDtos = Lists.newArrayList(reportListDto);

        //mock
        when(mapper.map(criteria, ReportQuery.class)).thenReturn(query);
        when(reportDomainModel.search(query, false)).thenReturn(reports);
        when(reportDomainModel.count(query)).thenReturn(15L);
        when(mapper.mapAsList(reports, ReportListDto.class)).thenReturn(reportDtos);

        //execute
        PagedListDto<ReportListDto> result = reportServiceBean.search(criteria, false);

        //verify and assert
        verify(mapper).map(criteria, ReportQuery.class);
        verify(searchReportsHelper).includeDeletedReportsInQuery(query);
        verify(searchReportsHelper).prepareVesselFreeTextSearch(query);
        verify(searchReportsHelper).prepareSorting(query);
        verify(reportDomainModel).search(query, false);
        verify(reportDomainModel).count(query);
        verify(mapper).mapAsList(reports, ReportListDto.class);
        verify(searchReportsHelper).enrichWithVesselInformation(reportDtos);
        verify(searchReportsHelper).enrichWithOlderVersions(reportDtos);
        verifyNoMoreInteractions(searchReportsHelper, reportDomainModel, mapper, reportHelper);

        assertEquals(1, result.getCurrentPage());
        assertSame(reportDtos, result.getItems());
        assertEquals(10, result.getRowsPerPage());
        assertEquals(2, result.getTotalNumberOfPages());
        assertEquals(15, result.getTotalNumberOfRecords());
    }

    @Test
    public void searchWithFluxSalesReportMessage() {
        //data set
        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                        .withID(new IDType().withValue("value")).withSubmitterFLUXParty(new FLUXPartyType().withIDS(new IDType().withValue("BEL"))));
        ReportQuery reportQuery = new ReportQuery();
        List<Report> reports = new ArrayList<>();
        FLUXPartyType fluxPartyType = new FLUXPartyType();
        String messageValidationResult = "OK";
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(new ValidationQualityAnalysisType());

        FLUXResponseDocumentType fluxResponseDocumentType = new FLUXResponseDocumentType()
                .withIDS(new IDType().withValue(UUID.randomUUID().toString()))
                .withCreationDateTime(new DateTimeType().withDateTime(DateTime.now()))
                .withReferencedID(fluxSalesQueryMessage.getSalesQuery().getID())
                .withRespondentFLUXParty(fluxPartyType);
        FLUXSalesResponseMessage fluxSalesResponseMessage = new FLUXSalesResponseMessage().withFLUXResponseDocument(fluxResponseDocumentType);

        //mock
        doReturn(reportQuery).when(mapper).map(fluxSalesQueryMessage, ReportQuery.class);
        doReturn(reports).when(reportDomainModel).searchIncludingDetails(reportQuery);
        doReturn(fluxSalesResponseMessage).when(fluxSalesResponseMessageFactory).create(fluxSalesQueryMessage, reports, validationResults, messageValidationResult);
        doNothing().when(rulesService).sendResponseToRules(fluxSalesResponseMessage, "BEL", "FLUX");

        //execute
        reportServiceBean.search(fluxSalesQueryMessage, "FLUX", validationResults, messageValidationResult);

        //verify and assert
        verify(mapper).map(fluxSalesQueryMessage, ReportQuery.class);
        verify(searchReportsHelper).excludeDeletedReportsInQuery(reportQuery);
        verify(reportDomainModel).searchIncludingDetails(reportQuery);
        verify(fluxSalesResponseMessageFactory).create(fluxSalesQueryMessage, reports, validationResults, messageValidationResult);
        verify(rulesService).sendResponseToRules(fluxSalesResponseMessage, "BEL", "FLUX");
        verifyNoMoreInteractions(mapper, reportDomainModel, fluxSalesResponseMessageFactory, rulesService, reportHelper);
    }

    @Test
    public void testSaveReportWhenPurposeIsOriginalAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHost() throws ConfigServiceException {
        Report report = new Report();
        report.withFLUXSalesReportMessage(
                new FLUXSalesReportMessage()
                        .withFLUXReportDocument(
                            new FLUXReportDocumentType()
                                    .withIDS(new IDType().withValue("bla"))
                                    .withCreationDateTime(new DateTimeType().withDateTime(DateTime.parse("2010-01-01"))))
                        .withSalesReports(new SalesReportType().withIncludedSalesDocuments(new SalesDocumentType().withCurrencyCode(new CodeType().withValue("USD")))));

        String plugin = "FLUX";
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(new ValidationQualityAnalysisType());
        String messageValidationResult = "OK";

        //mock
        doReturn(Optional.empty()).when(reportDomainModel).findByExtId("bla");
        doReturn(false).when(reportHelper).isReportDeleted(report);
        doReturn(BigDecimal.valueOf(1.5)).when(ecbProxyService).findExchangeRate("USD", "EUR", DateTime.parse("2010-01-01"));
        doReturn("EUR").when(parameterService).getStringValue(ParameterKey.CURRENCY.getKey());

        //execute
        reportServiceBean.saveReport(report, plugin, validationResults, messageValidationResult);

        //verify and assert
        verify(reportDomainModel).create(report, "EUR", BigDecimal.valueOf(1.5));
        verify(reportDomainModel).findByExtId("bla");
        verify(reportHelper).isReportDeleted(report);
        verify(ecbProxyService).findExchangeRate("USD", "EUR", DateTime.parse("2010-01-01"));
        verify(reportServiceHelper).sendResponseToSenderOfReport(report, plugin, validationResults, messageValidationResult);
        verify(reportServiceHelper).forwardReportToOtherRelevantParties(report, plugin);
        verifyNoMoreInteractions(reportDomainModel, reportServiceHelper, reportHelper);
    }

    @Test
    public void testSaveReportWhenPurposeIsDeleteAndSalesLocationIsNotCountryOfHostAndVesselFlagIsNotCountryOfHost() throws ConfigServiceException {
        Report report = new Report();
        report.withFLUXSalesReportMessage(
                new FLUXSalesReportMessage()
                        .withFLUXReportDocument(
                                new FLUXReportDocumentType()
                                        .withIDS(new IDType().withValue("bla"))
                                        .withCreationDateTime(new DateTimeType().withDateTime(DateTime.parse("2010-01-01"))))
                        .withSalesReports(new SalesReportType().withIncludedSalesDocuments(new SalesDocumentType().withCurrencyCode(new CodeType().withValue("USD")))));

        String plugin = "FLUX";
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(new ValidationQualityAnalysisType());
        String messageValidationResult = "OK";

        //mock
        doReturn(Optional.empty()).when(reportDomainModel).findByExtId("bla");
        doReturn(true).when(reportHelper).isReportDeleted(report);
        doReturn("EUR").when(parameterService).getStringValue(ParameterKey.CURRENCY.getKey());

        //execute
        reportServiceBean.saveReport(report, plugin, validationResults, messageValidationResult);

        //verify and assert
        verify(reportDomainModel).create(report, "EUR", BigDecimal.ONE);
        verify(reportDomainModel).findByExtId("bla");
        verify(reportHelper).isReportDeleted(report);
        verify(reportServiceHelper).sendResponseToSenderOfReport(report, plugin, validationResults, messageValidationResult);
        verify(reportServiceHelper).forwardReportToOtherRelevantParties(report, plugin);
        verifyNoMoreInteractions(reportDomainModel, reportServiceHelper, reportHelper);
    }

    @Test
    public void testSaveReportWhenIdAlreadyExists() throws ConfigServiceException {
        Report report = new Report();
        report.withFLUXSalesReportMessage(
                new FLUXSalesReportMessage().withFLUXReportDocument(
                        new FLUXReportDocumentType().withIDS(
                                new IDType().withValue("bla"))));

        String plugin = "FLUX";
        List<ValidationQualityAnalysisType> validationResults = Lists.newArrayList(new ValidationQualityAnalysisType());
        String messageValidationResult = "OK";

        //mock
        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId("bla");

        //execute
        reportServiceBean.saveReport(report, plugin, validationResults, messageValidationResult);

        //verify and assert
        verify(reportDomainModel).findByExtId("bla");
        verify(reportServiceHelper).sendResponseToSenderOfReport(report, plugin, validationResults, messageValidationResult);
        verify(reportServiceHelper).forwardReportToOtherRelevantParties(report, plugin);
        verifyNoMoreInteractions(reportDomainModel, reportHelper);
    }

}