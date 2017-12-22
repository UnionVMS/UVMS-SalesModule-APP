package eu.europa.ec.fisheries.uvms.sales.service;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.util.List;

@Local
public interface ReportService {

    /**
     * Get the details of a sales report
     * @param extId the GUID of the sales report, internally known as extId
     * @return details of the sales report
     */
    SalesDetailsDto findSalesDetails(@NotNull String extId);

    /**
     * Search for sales reports synchronously. Used over REST service.
     * @param filters filter criteria
     * @param eagerLoadRelations When you expect a lot of reports to come back, it is probably not interesting to lazy load all relations. By activating this mode, the query will eager fetch several relations, avoiding the need to do more queries.
     * @return page of search results
     * @throws SalesServiceException when something goes wrong
     */
    PagedListDto<ReportListDto> search(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters, boolean eagerLoadRelations);

    /**
     * Search for sales reports asynchronously. Used over JMS.
     * When the search has finished, the results will be returned via the Exchange plugin over JMS.
     * @param fluxSalesQueryMessage filter criteria
     * @param pluginToSendResponseThrough the plugin through which the query result should be sent
     * @param validationResults the validation results of the incoming query
     * @param messageValidationStatus the general validation status of the entire message
     */
    void search(@NotNull FLUXSalesQueryMessage fluxSalesQueryMessage, String pluginToSendResponseThrough,
                List<ValidationQualityAnalysisType> validationResults, String messageValidationStatus);

    /**
     * Exports documents based on the same criterias you can use for search()
     * @param filters criteria to search on
     * @return the matching flux reports formatted in the manner that the frontend expects it to convert to CSV.
     * @throws SalesServiceException
     */
    List<List<String>> exportDocuments(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters);

    /**
     * Exports either the selected documents or all documents based on the {@link ExportListsDto} passed.
     * @param exportListsDto
     * @return the matching flux reports formatted in the manner that the frontend expects it to convert to CSV.
     * @throws SalesServiceException
     */
    List<List<String>> exportSelectedDocuments(@NotNull ExportListsDto exportListsDto);

    /**
     * Saves an incoming report whether it's PurposeCode is 9, 5 or 3
     * and it'll do the necessary actions to the previous report if the code is 5 or 3
     * @param report
     * @param pluginToSendResponseThrough the plugin through which the query result should be sent
     * @param validationResults the validation results of the incoming report
     * @param messageValidationStatus the general validation status of the entire message
     */
    void saveReport(Report report, String pluginToSendResponseThrough,
                    List<ValidationQualityAnalysisType> validationResults,
                    String messageValidationStatus);

    /**
     * Finds a sales report by extId
     * Does not return reports that have been corrected or deleted
     * @param extId the extId of the report
     */
    Optional<Report> findByExtId(String extId);
}
