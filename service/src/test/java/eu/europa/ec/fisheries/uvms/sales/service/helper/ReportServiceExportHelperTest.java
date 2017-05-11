package eu.europa.ec.fisheries.uvms.sales.service.helper;

import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportServiceExportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportListExportDto;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReportServiceExportHelperTest {

    private ReportServiceExportHelper reportServiceExportHelper;

    @Before
    public void setUp() throws Exception {
        reportServiceExportHelper = new ReportServiceExportHelper();
    }

    @Test
    public void exportToList() throws ServiceException {
        String landingDate = new DateTime(2017, 3, 2, 0, 0).toString();
        String occurrence = new DateTime(2017, 3, 1, 0, 0).toString();

        List<List<String>> expected = createExpectedResults(landingDate, occurrence);

        List<ReportListExportDto> reportListExportDtos = createReportListExportDtos(landingDate, occurrence);

        List<List<String>> lists = reportServiceExportHelper.exportToList(reportListExportDtos);

        assertEquals(expected, lists);

    }

    private List<List<String>> createExpectedResults(String landingDate, String occurrence) {
        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump"));
        expected.add(Arrays.asList("BEL", "marked for life", "ircs", "vesselName",
                occurrence, "location", landingDate, "Garagepoort",
                "FIRST_SALE", "Putin", "Trump"));
        return expected;
    }

    private List<ReportListExportDto> createReportListExportDtos(String landingDate, String occurrence) {
        ReportListExportDto reportListExportDto1 = new ReportListExportDto()
                .buyer("Trump")
                .seller("Putin")
                .category(SalesCategoryType.FIRST_SALE)
                .externalMarking("marked for life")
                .flagState("BEL")
                .ircs("ircs")
                .landingDate(landingDate)
                .landingPort("Garagepoort")
                .location("location")
                .occurrence(occurrence)
                .vesselName("vesselName");

        ReportListExportDto reportListExportDto2 = new ReportListExportDto()
                .buyer("Trump")
                .seller("Putin")
                .category(SalesCategoryType.FIRST_SALE)
                .externalMarking("marked for life")
                .flagState("BEL")
                .ircs("ircs")
                .landingDate(landingDate)
                .landingPort("Garagepoort")
                .location("location")
                .occurrence(occurrence)
                .vesselName("vesselName");

        return Arrays.asList(reportListExportDto1, reportListExportDto2);
    }
}