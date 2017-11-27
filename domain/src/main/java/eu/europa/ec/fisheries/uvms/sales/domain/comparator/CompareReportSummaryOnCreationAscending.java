package eu.europa.ec.fisheries.uvms.sales.domain.comparator;

import eu.europa.ec.fisheries.schema.sales.ReportSummary;
import org.joda.time.DateTime;

import java.util.Comparator;

public class CompareReportSummaryOnCreationAscending implements Comparator<ReportSummary> {

    @Override
    public int compare(ReportSummary report1, ReportSummary report2) {
        DateTime creationDateOfReport1 = report1.getCreation();
        DateTime creationDateOfReport2 = report2.getCreation();
        return creationDateOfReport1.compareTo(creationDateOfReport2);
    }
}
