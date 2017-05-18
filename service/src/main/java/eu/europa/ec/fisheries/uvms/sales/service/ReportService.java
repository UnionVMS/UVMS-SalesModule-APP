package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
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
     * @return page of search results
     * @throws ServiceException when something goes wrong
     */
    PagedListDto<ReportListDto> search(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters) throws ServiceException;

    /**
     * Search for sales reports asynchronously. Used over JMS.
     * When the search has finished, the results will be returned via the Exchange plugin over JMS.
     * @param fluxSalesQueryMessage filter criteria
     * @param pluginToSendResponseThrough the plugin through which the query result should be sent
     */
    void search(@NotNull FLUXSalesQueryMessage fluxSalesQueryMessage, String pluginToSendResponseThrough) throws ServiceException;

    /**
     * Exports documents based on the same criterias you can use for search()
     * @param filters criteria to search on
     * @return the matching flux reports formatted in the manner that the frontend expects it to convert to CSV.
     * @throws ServiceException
     */
    List<List<String>> exportDocuments(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters) throws ServiceException;

    /**
     * Exports either the selected documents or all documents based on the {@link ExportListsDto} passed.
     * @param exportListsDto
     * @return the matching flux reports formatted in the manner that the frontend expects it to convert to CSV.
     * @throws ServiceException
     */
    List<List<String>> exportSelectedDocuments(@NotNull ExportListsDto exportListsDto) throws ServiceException;

    /**
     * Saves an incoming report whether it's PurposeCode is 9, 5 or 3
     * and it'll do the necessary actions to the previous report if the code is 5 or 3
     * @param report
     * @param pluginToSendResponseThrough the plugin through which the query result should be sent
     */
    void saveReport(Report report, String pluginToSendResponseThrough) throws ServiceException;
}
