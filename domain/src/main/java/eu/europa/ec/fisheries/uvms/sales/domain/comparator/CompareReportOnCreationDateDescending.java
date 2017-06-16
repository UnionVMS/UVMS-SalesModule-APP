package eu.europa.ec.fisheries.uvms.sales.domain.comparator;

import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;

import java.util.Comparator;

public class CompareReportOnCreationDateDescending implements Comparator<Report> {

    private CompareReportOnCreationDateAscending ascendingComparator;

    public CompareReportOnCreationDateDescending(ReportHelper reportHelper) {
        this.ascendingComparator = new CompareReportOnCreationDateAscending(reportHelper);
    }

    @Override
    public int compare(Report report1, Report report2) {
        return -ascendingComparator.compare(report1, report2);
    }
}
