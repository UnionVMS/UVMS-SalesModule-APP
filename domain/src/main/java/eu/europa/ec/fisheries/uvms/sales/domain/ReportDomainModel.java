package eu.europa.ec.fisheries.uvms.sales.domain;

import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import eu.europa.ec.fisheries.schema.sales.ReportSummary;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReportDomainModel {

    /**
     * Finds a report by its GUID, internally known as extId.
     * Does not return a report that has been corrected or deleted.
     *
     * @param extId internal name for GUID
     * @return the found report
     */
    Optional<Report> findByExtId(String extId);

    /**
     * Finds a report by its GUID, internally known as extId.
     *
     * @param extId internal name for GUID
     * @param includeDeletedOrCorrectedReports true if you want to find a report that could have been corrected or deleted
     * @return the found report
     */
    Optional<Report> findByExtId(String extId, boolean includeDeletedOrCorrectedReports);

    /**
     * Creates a report and converts the document/product prices to the local currency based on the exchangeRate.
     *
     * @param report the report to be created
     * @return the created report
     */
    Report create(Report report, String localCurrency, BigDecimal exchangeRate);

    /**
     * Get a {@link Report} with all products eagerly loaded. If no object has been found, {@link javax.persistence.NoResultException} is thrown.
     * @param extId extId
     * @return the FluxReport with matching extId
     */
    Report findSalesDetails(String extId);

    /**
     * Search reports which apply to the provided query.
     * @param fluxReportQuery query
     * @param eagerLoadRelations When you expect a lot of reports to come back, it is probably not interesting to lazy load all relations. By activating this mode, the query will eager fetch several relations, avoiding the need to do more queries.
     * @return the basic details of the found reports. When nothing found, an empty list is returned.
     */
    List<ReportSummary> search(ReportQuery fluxReportQuery, boolean eagerLoadRelations);

    /**
     * Search reports which apply to the provided query.
     * @param fluxReportQuery query
     * @return the found reports. When nothing found, an empty list is returned.
     */
    List<Report> searchIncludingDetails(ReportQuery fluxReportQuery);

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
    Optional<Report> findCorrectionOf(@NotNull String extId);

    /**
     * Returns all referenced reports, including the report that has
     * the given referencedId.
     * @param firstReferencedId the first referenced id of the report for which all referenced reports need to be retrieved.
     *                          Providing null is supported. Then, an empty list will be returned.
     * @return all referenced reports. When nothing found, an empty list.
     */
    List<ReportSummary> findOlderVersionsOrderedByCreationDateDescending(String firstReferencedId);

    /**
     * Returns all reports that are reference by this report.
     * @param report the report that refers to the returned reports. This report is not included in the result.
                     Providing null is supported. Then, an empty list will be returned.
     * @return all referenced reports. When nothing found, an empty list.
     */
    List<Report> findOlderVersionsOrderedByCreationDateDescendingIncludingDetails(Report report);


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
