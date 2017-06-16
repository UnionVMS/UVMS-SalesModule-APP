package eu.europa.ec.fisheries.uvms.sales.domain.comparator;

import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import org.joda.time.DateTime;

import java.util.Comparator;

public class CompareReportOnCreationDateAscending implements Comparator<Report> {

    private ReportHelper reportHelper;

    public CompareReportOnCreationDateAscending(ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
    }

    @Override
    public int compare(Report report1, Report report2) {
        DateTime creationDateOfReport1 = reportHelper.getCreationDate(report1);
        DateTime creationDateOfReport2 = reportHelper.getCreationDate(report2);
        return creationDateOfReport1.compareTo(creationDateOfReport2);
    }
}
