package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.ReportService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportServiceExportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportServiceHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.SalesDetailsHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.SearchReportsHelper;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import eu.europa.ec.fisheries.uvms.sales.service.mapper.DTO;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Stateless
public class ReportServiceBean implements ReportService {

    final static Logger LOG = LoggerFactory.getLogger(ReportServiceBean.class);

    @EJB
    private ReportDomainModel reportDomainModel;

    @EJB
    private ReportServiceExportHelper reportServiceExportHelper;

    @Inject @DTO
    private MapperFacade mapper;

    @EJB
    private SearchReportsHelper searchReportsHelper;

    @EJB
    private SalesDetailsHelper salesDetailsHelper;

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private RulesService rulesService;

    @EJB
    private ReportServiceHelper reportServiceHelper;

    @Override
    public void saveReport(Report report, String pluginToSendResponseThrough,
                           List<ValidationQualityAnalysisType> validationResults,
                           String messageValidationStatus) {
        Report alreadyExistingReport = reportDomainModel.findByExtIdOrNull(report.getFLUXSalesReportMessage().getFLUXReportDocument().getIDS().get(0).getValue());

        //If a report exists with the incoming ID, we don't save the report.
        if (alreadyExistingReport == null) {
            reportDomainModel.create(report);
        }

        reportServiceHelper.sendResponseToSenderOfReport(report, pluginToSendResponseThrough, validationResults, messageValidationStatus);
        reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);
    }

    @Override
    public Report findByExtIdOrNull(String extId) {
        return reportDomainModel.findByExtIdOrNull(extId);
    }

    @Override
    public SalesDetailsDto findSalesDetails(String extId) {
        checkArgument(!Strings.isNullOrEmpty(extId), "extId cannot be null or blank");

        Report report = reportDomainModel.findSalesDetails(extId);
        SalesDetailsDto detailsDto = mapper.map(report, SalesDetailsDto.class);

        salesDetailsHelper.convertPricesInLocalCurrency(detailsDto, report);
        salesDetailsHelper.calculateTotals(detailsDto);
        salesDetailsHelper.enrichWithLocation(detailsDto);
        salesDetailsHelper.enrichWithVesselInformation(detailsDto, report);
        salesDetailsHelper.enrichWithRelatedReport(detailsDto, report);
        salesDetailsHelper.enrichWithOtherRelevantVersions(detailsDto, report);
        salesDetailsHelper.enrichProductsWithFactor(detailsDto);

        return detailsDto;
    }

    @Override
    public PagedListDto<ReportListDto> search(@NotNull PageCriteriaDto<ReportQueryFilterDto> criteria) {
        try {
            //prepare query
            ReportQuery query = mapper.map(criteria, ReportQuery.class);
            searchReportsHelper.includeDeletedReportsInQuery(query);
            searchReportsHelper.prepareVesselFreeTextSearch(query);
            searchReportsHelper.prepareSorting(query);

            //search
            List<Report> reports = reportDomainModel.search(query);
            long amountOfReportsWithoutFilters = reportDomainModel.count(query);

            //enrich results
            List<ReportListDto> reportDtos = mapper.mapAsList(reports, ReportListDto.class);
            searchReportsHelper.enrichWithVesselInformation(reportDtos);
            searchReportsHelper.enrichWithOlderVersions(reportDtos);
            return new PagedListDto<>(query, amountOfReportsWithoutFilters, reportDtos);

        } catch (SalesServiceException e) {
            throw new SalesServiceException("Something went wrong searching for reports", e);
        }
    }

    @Override
    public void search(FLUXSalesQueryMessage fluxSalesQueryMessage,
                       String pluginToSendResponseThrough,
                       List<ValidationQualityAnalysisType> validationResults,
                       String messageValidationStatus) {
        ReportQuery query = mapper.map(fluxSalesQueryMessage, ReportQuery.class);
        searchReportsHelper.excludeDeletedReportsInQuery(query);

        List<Report> reports = reportDomainModel.search(query);

        FLUXSalesResponseMessage fluxSalesResponse = fluxSalesResponseMessageFactory.create(fluxSalesQueryMessage, reports, validationResults, messageValidationStatus);

        String recipient = fluxSalesQueryMessage.getSalesQuery().getSubmitterFLUXParty().getIDS().get(0).getValue();
        rulesService.sendResponseToRules(fluxSalesResponse, recipient, pluginToSendResponseThrough);
    }

    @Override
    public List<List<String>> exportDocuments(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters) {
        PagedListDto<ReportListDto> search = search(filters.pageSize(Integer.MAX_VALUE).pageIndex(1));

        List<ReportListExportDto> reports = mapper.mapAsList(search.getItems(), ReportListExportDto.class);

        return reportServiceExportHelper.exportToList(reports);
    }

    @Override
    public List<List<String>> exportSelectedDocuments(@NotNull ExportListsDto exportListsDto) {
        PagedListDto<ReportListDto> search;

        if (exportListsDto.isExportAll()) {
            search = getAllReports(exportListsDto);
        } else {
            search = getSelectedReports(exportListsDto);
        }

        List<ReportListExportDto> reports = mapper.mapAsList(search.getItems(), ReportListExportDto.class);

        return reportServiceExportHelper.exportToList(reports);
    }

    private PagedListDto<ReportListDto> getSelectedReports(ExportListsDto exportListsDto) {
        PageCriteriaDto<ReportQueryFilterDto> criteria = exportListsDto.getCriteria();
        criteria.getFilters().includeFluxReportIds(exportListsDto.getIds());
        try {
            return search(criteria);
        } catch (SalesServiceException e) {
            throw new SalesServiceException("Something went wrong during CSV export of selected reports", e);
        }
    }

    private PagedListDto<ReportListDto> getAllReports(ExportListsDto exportListsDto) {
        PageCriteriaDto<ReportQueryFilterDto> criteria = exportListsDto.getCriteria();
        criteria.getFilters().excludeFluxReportIds(exportListsDto.getIds());
        try {
            return search(criteria);
        } catch (SalesServiceException e) {
            throw new SalesServiceException("Something went wrong during CSV export of all reports", e);
        }
    }

    protected void setReportServiceExportHelper(ReportServiceExportHelper reportServiceExportHelper) {
        this.reportServiceExportHelper = reportServiceExportHelper;
    }
}
