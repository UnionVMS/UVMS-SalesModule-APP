package eu.europa.ec.fisheries.uvms.sales.domain.comparator;

import eu.europa.ec.fisheries.schema.sales.ReportSummary;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;

public class CompareReportSummaryOnCreationDescendingTest {

    private CompareReportSummaryOnCreationDescending compareReportSummaryOnCreationDescending;

    @Before
    public void init() {
        this.compareReportSummaryOnCreationDescending = new CompareReportSummaryOnCreationDescending();
    }

    @Test
    public void compareCase1() throws Exception {
        DateTime date1 = new DateTime(2017, 6, 8, 10, 0);
        DateTime date2 = new DateTime(2017, 6, 9, 10, 0);
        ReportSummary report1 = new ReportSummary().withCreation(date1);
        ReportSummary report2 = new ReportSummary().withCreation(date2);

        List<ReportSummary> list = Arrays.asList(report1, report2);

        Collections.sort(list, compareReportSummaryOnCreationDescending);

        assertSame(report2, list.get(0));
        assertSame(report1, list.get(1));
    }

    @Test
    public void compareCase2() throws Exception {
        DateTime date1 = new DateTime(2017, 6, 8, 10, 0);
        DateTime date2 = new DateTime(2017, 6, 9, 10, 0);
        ReportSummary report1 = new ReportSummary().withCreation(date1);
        ReportSummary report2 = new ReportSummary().withCreation(date2);

        List<ReportSummary> list = Arrays.asList(report2, report1);

        Collections.sort(list, compareReportSummaryOnCreationDescending);

        assertSame(report2, list.get(0));
        assertSame(report1, list.get(1));
    }

    @Test
    public void compareCase3() throws Exception {
        DateTime date1 = new DateTime(2017, 10, 10, 10, 0);
        DateTime date2 = new DateTime(2017, 6, 9, 10, 0);
        ReportSummary report1 = new ReportSummary().withCreation(date1);
        ReportSummary report2 = new ReportSummary().withCreation(date2);

        List<ReportSummary> list = Arrays.asList(report1, report2);

        Collections.sort(list, compareReportSummaryOnCreationDescending);

        assertSame(report1, list.get(0));
        assertSame(report2, list.get(1));
    }

    @Test
    public void compareCase4() throws Exception {
        DateTime date1 = new DateTime(2017, 10, 10, 10, 0);
        DateTime date2 = new DateTime(2017, 6, 9, 10, 0);
        ReportSummary report1 = new ReportSummary().withCreation(date1);
        ReportSummary report2 = new ReportSummary().withCreation(date2);

        List<ReportSummary> list = Arrays.asList(report2, report1);

        Collections.sort(list, compareReportSummaryOnCreationDescending);

        assertSame(report1, list.get(0));
        assertSame(report2, list.get(1));
    }
}