package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Stateless
public class ReportServiceBean implements ReportService {

    static final Logger LOG = LoggerFactory.getLogger(ReportServiceBean.class);
    public static final int MAX_EXPORT_RESULTS = 1000;

    @EJB
    private ReportDomainModel reportDomainModel;

    @Inject @DTO
    private MapperFacade mapper;

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private RulesService rulesService;

    @EJB
    private EcbProxyService ecbProxyService;

    @EJB
    private ParameterService parameterService;

    @EJB
    private ReportServiceHelper reportServiceHelper;

    @EJB
    private SearchReportsHelper searchReportsHelper;

    @EJB
    private SalesDetailsHelper salesDetailsHelper;

    @EJB
    private ReportServiceExportHelper reportServiceExportHelper;

    @EJB
    private ReportHelper reportHelper;


    @Override
    public void saveReport(Report report, String pluginToSendResponseThrough,
                           List<ValidationQualityAnalysisType> validationResults,
                           String messageValidationStatus) throws ConfigServiceException {
        if (!doesReportAlreadyExistInDatabase(report)) {
            try {
                String targetCurrency = parameterService.getStringValue(ParameterKey.CURRENCY.getKey());
                BigDecimal exchangeRate = findExchangeRateForCurrencyInReport(report, targetCurrency);
                reportDomainModel.create(report, targetCurrency, exchangeRate);
            } catch (SalesNonBlockingException e) {
                LOG.error("Unable to create sales report. Reason: " + e.getMessage());
                return;
            }
        }

        try {
            reportServiceHelper.sendResponseToSenderOfReport(report, pluginToSendResponseThrough, validationResults, messageValidationStatus);
        } catch (SalesNonBlockingException e) {
            LOG.error("Error when sending a response to the sender of the report", e);
        }

        try {
            reportServiceHelper.forwardReportToOtherRelevantParties(report, pluginToSendResponseThrough);
        } catch (SalesNonBlockingException e) {
            LOG.error("Error when forwarding a report to other relevant parties", e);
        }
    }

    private boolean doesReportAlreadyExistInDatabase(Report report) {
        return reportDomainModel.findByExtId(report.getFLUXSalesReportMessage().getFLUXReportDocument().getIDS().get(0).getValue())
                                .isPresent();
    }

    private BigDecimal findExchangeRateForCurrencyInReport(Report report, String targetCurrency) {
        if (reportHelper.isReportDeleted(report)) {
            return BigDecimal.ONE;
        } else {
            // We need to find the exchange rate for the incoming report's currency to the local currency
            // so Sales can convert it later on
            String currencyOfIncomingReport = report.getFLUXSalesReportMessage().getSalesReports().get(0).getIncludedSalesDocuments().get(0).getCurrencyCode().getValue();
            DateTime creationDateOfReport = report.getFLUXSalesReportMessage().getFLUXReportDocument().getCreationDateTime().getDateTime();

            // Only contact ECB Proxy when the report's currency differs from the local currency
            if (!Objects.equals(currencyOfIncomingReport, targetCurrency)) {
                return ecbProxyService.findExchangeRate(currencyOfIncomingReport, targetCurrency, creationDateOfReport);
            } else {
                return BigDecimal.ONE;
            }
        }
    }
    @Override
    public Optional<Report> findByExtId(String extId) {
        return reportDomainModel.findByExtId(extId);
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
    public PagedListDto<ReportListDto> search(@NotNull PageCriteriaDto<ReportQueryFilterDto> criteria, boolean eagerLoadRelations) {
        try {
            //prepare query
            ReportQuery query = mapper.map(criteria, ReportQuery.class);
            searchReportsHelper.includeDeletedReportsInQuery(query);
            searchReportsHelper.prepareVesselFreeTextSearch(query);
            searchReportsHelper.prepareSorting(query);

            //search
            List<ReportSummary> reports = reportDomainModel.search(query, eagerLoadRelations);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void search(FLUXSalesQueryMessage fluxSalesQueryMessage,
                       String pluginToSendResponseThrough,
                       List<ValidationQualityAnalysisType> validationResults,
                       String messageValidationStatus) {
        try {
            ReportQuery query = mapper.map(fluxSalesQueryMessage, ReportQuery.class);
            searchReportsHelper.excludeDeletedReportsInQuery(query);

            List<Report> reports = reportDomainModel.searchIncludingDetails(query);

            FLUXSalesResponseMessage fluxSalesResponse = fluxSalesResponseMessageFactory.create(fluxSalesQueryMessage, reports, validationResults, messageValidationStatus);

            String recipient = fluxSalesQueryMessage.getSalesQuery().getSubmitterFLUXParty().getIDS().get(0).getValue();
            rulesService.sendResponseToRules(fluxSalesResponse, recipient, pluginToSendResponseThrough);
        } catch (Exception e) {
            throw new SalesNonBlockingException("Error when sending the response of a query", e);
        }
    }

    @Override
    public List<List<String>> exportDocuments(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters) {
        PagedListDto<ReportListDto> search = search(filters.pageSize(MAX_EXPORT_RESULTS).pageIndex(1), true);

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

        boolean eagerLoadRelations = exportListsDto.getIds().size() > 10;

        try {
            return search(criteria, eagerLoadRelations);
        } catch (SalesServiceException e) {
            throw new SalesServiceException("Something went wrong during CSV export of selected reports", e);
        }
    }

    private PagedListDto<ReportListDto> getAllReports(ExportListsDto exportListsDto) {
        PageCriteriaDto<ReportQueryFilterDto> criteria = exportListsDto.getCriteria();
        criteria.getFilters().excludeFluxReportIds(exportListsDto.getIds());
        try {
            return search(criteria, true);
        } catch (SalesServiceException e) {
            throw new SalesServiceException("Something went wrong during CSV export of all reports", e);
        }
    }

    protected void setReportServiceExportHelper(ReportServiceExportHelper reportServiceExportHelper) {
        this.reportServiceExportHelper = reportServiceExportHelper;
    }

}
