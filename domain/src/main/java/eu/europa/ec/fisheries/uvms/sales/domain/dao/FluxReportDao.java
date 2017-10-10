package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;

import javax.validation.constraints.NotNull;
import java.util.List;

/** DAO for all operations concerning a flux report. **/
public interface FluxReportDao extends DaoForSales<FluxReport, Integer> {

    /**
     * Find exactly one object by its id.
     * @param extId extId
     * @return the FluxReport with matching extId
     */
    Optional<FluxReport> findByExtId(@NotNull String extId);

    /**
     * Filter FluxReports by {@link ReportQuery}. If no objects have been found, an empty list is returned
     * @param fluxReportQuery The query parameters
     * @return a list of FluxReports matching the given query
     */
    List<FluxReport> search(@NotNull ReportQuery fluxReportQuery);

    /**
     * Count result of FluxReports by {@link ReportQuery}
     * @param fluxReportQuery The query parameters
     * @return the number of results for the given query
     */
    long count(@NotNull ReportQuery fluxReportQuery);

    /**
     * Get a {@link FluxReport} with all products in {@link Document} eagerly loaded. If no object has been found, NoResultException is thrown
     * @param extId extId
     * @return the FluxReport with matching extId
     */
    FluxReport findDetailsByExtId(@NotNull String extId);

    /**
     *  Retrieves the sales report (note or take over document) which is a correction or deletion of the given sales
     *  report.
     **/
    Optional<FluxReport> findCorrectionOrDeletionOf(@NotNull String extId);

    /**
     * Returns the latest version of the given report. A report being an older version means that another report
     * corrects or deletes the given report.
     */
    FluxReport findLatestVersion(FluxReport fluxReport);
}
