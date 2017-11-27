package eu.europa.ec.fisheries.uvms.sales.domain.comparator;

import eu.europa.ec.fisheries.schema.sales.ReportSummary;

import java.util.Comparator;

public class CompareReportSummaryOnCreationDescending implements Comparator<ReportSummary> {

    private CompareReportSummaryOnCreationAscending ascendingComparator;

    public CompareReportSummaryOnCreationDescending() {
        this.ascendingComparator = new CompareReportSummaryOnCreationAscending();
    }

    @Override
    public int compare(ReportSummary report1, ReportSummary report2) {
        return -ascendingComparator.compare(report1, report2);
    }
}
