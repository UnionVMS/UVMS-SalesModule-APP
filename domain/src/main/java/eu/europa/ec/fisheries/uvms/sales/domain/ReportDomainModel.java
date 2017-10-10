package eu.europa.ec.fisheries.uvms.sales.domain;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ReportDomainModel {

    /**
     * Finds a report by its GUID, internally known as extId.
     *
     * @param extId internal name for GUID
     * @return the found report
     */
    Optional<Report> findByExtId(String extId);

    /**
     * Creates a report.
     *
     * @param report the report to be created
     * @return the created report
     */
    Report create(Report report);

    /**
     * Get a {@link Report} with all products eagerly loaded. If no object has been found, {@link javax.persistence.NoResultException} is thrown.
     * @param extId extId
     * @return the FluxReport with matching extId
     */
    Report findSalesDetails(String extId);

    /**
     * Search reports which apply to the provided query.
     * @param fluxReportQuery query
     * @return the found reports. When nothing found, an empty list is returned.
     */
    List<Report> search(ReportQuery fluxReportQuery);

    /**
     * Count how many reports apply to the provided query.
     * Paging and sorting is not taken into account.
     * @param fluxReportQuery query
     * @return how many reports apply to the provided query
     */
    long count(ReportQuery fluxReportQuery);

    /**
     *  Retrieves the sales report (note or take over document) which is a correction or deletion of the given sales
     *  report.
     **/
    Optional<Report> findCorrectionOrDeletionOf(@NotNull String extId);

    /**
     * Returns all referenced reports, including the report that has
     * the given referencedId.
     * @param firstReferencedId the first referenced id of the report for which all referenced reports need to be retrieved.
     *                          Providing null is supported. Then, an empty list will be returned.
     * @return all referenced reports. When nothing found, an empty list.
     */
    List<Report> findOlderVersionsOrderedByCreationDateDescending(String firstReferencedId);

    /**
     * Returns all reports that are reference by this report.
     * @param report the report that refers to the returned reports. This report is not included in the result.
                     Providing null is supported. Then, an empty list will be returned.
     * @return all referenced reports. When nothing found, an empty list.
     */
    List<Report> findOlderVersionsOrderedByCreationDateDescending(Report report);

    /** Returns whether the given report is the latest version. A report being an older version means that another report
     * corrects or deletes the given report. **/
    boolean isLatestVersion(@NotNull Report report);

    /**
     * Returns the latest version of the given report. A report being an older version means that another report
     * corrects or deletes the given report.
     */
    Report findLatestVersion(Report report);

    /**
     * In case that the given report is a take over document: returns the related sales notes.
     * In case that the given report is a sales note: returns the related take over documents.
     * @param report
     */
    List<Report> findRelatedReportsOf(Report report);

}
