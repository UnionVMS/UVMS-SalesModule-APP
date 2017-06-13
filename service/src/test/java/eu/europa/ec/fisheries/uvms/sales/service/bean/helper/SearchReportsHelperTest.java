package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportListDto;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchReportsHelperTest {

    @InjectMocks
    private SearchReportsHelper helper;

    @Mock
    private AssetService assetService;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Mock
    private MapperFacade mapper;

    @Test
    public void testPrepareVesselFreeTextSearchWhenNoFiltersAreProvided() throws Exception {
        ReportQuery reportQuery = new ReportQuery();

        helper.prepareVesselFreeTextSearch(reportQuery);

        verifyNoMoreInteractions(assetService);

        assertNull(reportQuery.getFilters());
    }

    @Test
    public void testPrepareVesselFreeTextSearchWhenVesselNameIsNull() throws Exception {
        ReportQueryFilter reportQueryFilter = new ReportQueryFilter();
        ReportQuery reportQuery = new ReportQuery()
                .withFilters(reportQueryFilter);

        helper.prepareVesselFreeTextSearch(reportQuery);

        verifyNoMoreInteractions(assetService);

        assertEquals(new ArrayList<String>(), reportQuery.getFilters().getVesselExtIds());
    }

    @Test
    public void testPrepareVesselFreeTextSearchWhenVesselNameIsAnEmptyString() throws Exception {
        ReportQueryFilter reportQueryFilter = new ReportQueryFilter()
                .withVesselName("");

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(reportQueryFilter);

        helper.prepareVesselFreeTextSearch(reportQuery);

        verifyNoMoreInteractions(assetService);

        assertEquals(new ArrayList<String>(), reportQuery.getFilters().getVesselExtIds());
    }

    @Test(expected = ServiceException.class)
    public void testPrepareVesselFreeTextSearchWhenSomethingGoesWrongContactingAsset() throws Exception {
        ReportQueryFilter reportQueryFilter = new ReportQueryFilter()
                .withVesselName("vessel");

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(reportQueryFilter);

        when(assetService.findExtIdsByNameOrCFROrIRCS("vessel")).thenThrow(new ServiceException("Boo"));

        helper.prepareVesselFreeTextSearch(reportQuery);

        verify(assetService).findExtIdsByNameOrCFROrIRCS("vessel");
        verifyNoMoreInteractions(assetService);

        assertEquals(new ArrayList<String>(), reportQuery.getFilters().getVesselExtIds());
    }

    @Test
    public void testPrepareVesselFreeTextSearchWhenSuccess() throws Exception {
        ReportQueryFilter reportQueryFilter = new ReportQueryFilter()
                .withVesselName("vessel");

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(reportQueryFilter);

        List<String> expectedVesselExtIds = Lists.newArrayList("guid1", "guid2");

        when(assetService.findExtIdsByNameOrCFROrIRCS("vessel")).thenReturn(expectedVesselExtIds);

        helper.prepareVesselFreeTextSearch(reportQuery);

        verify(assetService).findExtIdsByNameOrCFROrIRCS("vessel");
        verifyNoMoreInteractions(assetService);

        assertEquals(expectedVesselExtIds, reportQuery.getFilters().getVesselExtIds());
    }

    @Test
    public void testEnrichWithVesselInformationWhenNoReports() throws Exception {
        helper.enrichWithVesselInformation(new ArrayList<ReportListDto>());
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testEnrichWithVesselInformationWhenOneOfTheVesselsCannotBeFound() throws Exception {
        //data set
        ReportListDto reportListDto1 = new ReportListDto()
            .vesselExtId("invalid");
        ReportListDto reportListDto2 = new ReportListDto()
                .vesselExtId("valid");

        Asset asset = new Asset();
        asset.setIrcs("ircs");
        asset.setExternalMarking("external marking");
        asset.setName("name");

        //mock
        when(assetService.findByExtId("invalid")).thenThrow(new ServiceException("oh oooh"));
        when(assetService.findByExtId("valid")).thenReturn(asset);

        //execute
        helper.enrichWithVesselInformation(Lists.newArrayList(reportListDto1, reportListDto2));

        verify(assetService).findByExtId("invalid");
        verify(assetService).findByExtId("valid");

        //verify and assert
        verifyNoMoreInteractions(assetService);

        assertNull(reportListDto1.getIrcs());
        assertNull(reportListDto1.getExternalMarking());
        assertNull(reportListDto1.getVesselName());

        assertEquals("ircs", reportListDto2.getIrcs());
        assertEquals("external marking", reportListDto2.getExternalMarking());
        assertNull(reportListDto2.getVesselName());
    }

    @Test
    public void testEnrichWithVesselInformationWhenSuccess() throws Exception {
        //data set
        ReportListDto reportListDto1 = new ReportListDto()
                .vesselExtId("1");
        ReportListDto reportListDto2 = new ReportListDto()
                .vesselExtId("2");

        Asset asset1 = new Asset();
        asset1.setIrcs("ircs1");
        asset1.setExternalMarking("external marking1");
        asset1.setName("name1");

        Asset asset2 = new Asset();
        asset2.setIrcs("ircs2");
        asset2.setExternalMarking("external marking2");
        asset2.setName("name2");

        //mock
        when(assetService.findByExtId("1")).thenReturn(asset1);
        when(assetService.findByExtId("2")).thenReturn(asset2);

        //execute
        helper.enrichWithVesselInformation(Lists.newArrayList(reportListDto1, reportListDto2));

        verify(assetService).findByExtId("1");
        verify(assetService).findByExtId("2");

        //verify and assert
        verifyNoMoreInteractions(assetService);

        assertEquals("ircs1", reportListDto1.getIrcs());
        assertEquals("external marking1", reportListDto1.getExternalMarking());
        assertNull(reportListDto1.getVesselName());

        assertEquals("ircs2", reportListDto2.getIrcs());
        assertEquals("external marking2", reportListDto2.getExternalMarking());
        assertNull(reportListDto2.getVesselName());
    }

    @Test
    public void testPrepareSortingWhenNoSortingHasBeenDefined() throws Exception {
        ReportQuery query = new ReportQuery();

        helper.prepareSorting(query);

        ReportQuerySorting sorting = query.getSorting();
        assertEquals(SortDirection.ASCENDING, sorting.getDirection());
        assertEquals(ReportQuerySortField.SALES_DATE, sorting.getField());
    }

    @Test
    public void testPrepareSortingWhenAnEmptySortingObjectHasBeenDefined() throws Exception {
        ReportQuery query = new ReportQuery()
                .withSorting(new ReportQuerySorting());

        helper.prepareSorting(query);

        ReportQuerySorting sorting = query.getSorting();
        assertEquals(SortDirection.ASCENDING, sorting.getDirection());
        assertEquals(ReportQuerySortField.SALES_DATE, sorting.getField());

    }

    @Test
    public void testPrepareSortingWhenSortingHasAlreadyBeenDefined() throws Exception {
        ReportQuerySorting sorting = new ReportQuerySorting()
                .withDirection(SortDirection.DESCENDING)
                .withField(ReportQuerySortField.VESSEL_NAME);

        ReportQuery query = new ReportQuery()
                .withSorting(sorting);

        helper.prepareSorting(query);

        ReportQuerySorting actualSorting = query.getSorting();
        assertEquals(SortDirection.DESCENDING, actualSorting.getDirection());
        assertEquals(ReportQuerySortField.VESSEL_NAME, actualSorting.getField());
    }

    @Test
    public void testEnrichWithOlderVersions() {
        //data set
        String id1 = "id1";
        String id2 = "id2";

        Report report1 = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withIDS(new IDType().withValue(id1))));
        ReportListDto reportListDto1 = new ReportListDto()
                                        .extId(id1)
                                        .referencedId(id2);

        Report report2 = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withIDS(new IDType().withValue(id2))));
        ReportListDto reportListDto2 = new ReportListDto()
                .extId(id2);

        List<Report> allOlderVersions = Arrays.asList(report1, report2);
        List<ReportListDto> mappedOlderVersions = Arrays.asList(reportListDto1, reportListDto2);

        //mock
        doReturn(allOlderVersions).when(reportDomainModel).findOlderVersionsOrderedByCreationDateDescending(id2);
        doReturn(mappedOlderVersions).when(mapper).mapAsList(allOlderVersions, ReportListDto.class);

        //execute
        helper.enrichWithOlderVersions(Arrays.asList(reportListDto1));

        //verify and assert
        verify(reportDomainModel).findOlderVersionsOrderedByCreationDateDescending(id2);
        verify(mapper).mapAsList(allOlderVersions, ReportListDto.class);

        assertEquals(mappedOlderVersions, reportListDto1.getOlderVersions());
    }

}