package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.sales.service.ReportService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.*;
import eu.europa.ec.fisheries.uvms.sales.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
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

    @EJB(lookup = ServiceConstants.DB_ACCESS_REPORT_DOMAIN_MODEL)
    private ReportDomainModel reportDomainModel;

    @EJB
    private ReportServiceExportHelper reportServiceExportHelper;

    @Inject
    private MapperFacade mapper;

    @EJB
    private SearchReportsHelper searchReportsHelper;

    @EJB
    private SalesDetailsHelper salesDetailsHelper;

    @EJB
    private FLUXSalesResponseMessageFactory fluxSalesResponseMessageFactory;

    @EJB
    private ExchangeService exchangeService;

    @EJB
    private ReportServiceHelper reportServiceHelper;

    @EJB
    private ReportHelper reportHelper;

    @Override
    public void saveReport(Report report) throws ServiceException {
        reportDomainModel.create(report);
        reportServiceHelper.sendResponseToSenderOfReport(report);
        reportServiceHelper.forwardReportToOtherRelevantParties(report);
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

        return detailsDto;
    }

    @Override
    public PagedListDto<ReportListDto> search(@NotNull PageCriteriaDto<ReportQueryFilterDto> criteria) throws ServiceException {
        try {
            //prepare query
            ReportQuery query = mapper.map(criteria, ReportQuery.class);
            searchReportsHelper.prepareVesselFreeTextSearch(query);
            searchReportsHelper.prepareSorting(query);

            //search
            List<Report> reports = reportDomainModel.search(query);
            long amountOfReportsWithoutFilters = reportDomainModel.count(query);

            //enrich results
            List<ReportListDto> reportDtos = mapper.mapAsList(reports, ReportListDto.class);
            searchReportsHelper.enrichWithVesselInformation(reportDtos);
            return new PagedListDto<>(query, amountOfReportsWithoutFilters, reportDtos);

        } catch (ServiceException e) {
            throw new ServiceException("Something went wrong searching for reports", e);
        }
    }

    @Override
    public void search(FLUXSalesQueryMessage fluxSalesQueryMessage) throws ServiceException {
        ReportQuery query = mapper.map(fluxSalesQueryMessage, ReportQuery.class);
        List<Report> reports = reportDomainModel.search(query);
        ValidationResultDocumentType validationResultDocumentType = new ValidationResultDocumentType(); //TODO: implement correctly

        FLUXSalesResponseMessage fluxSalesResponse = fluxSalesResponseMessageFactory.create(fluxSalesQueryMessage, reports, validationResultDocumentType);

        //TODO: logic to decide which recipient this should be sent to
        exchangeService.sendToExchange(fluxSalesResponse, fluxSalesQueryMessage.getSalesQuery().getSubmitterFLUXParty().getIDS().get(0).getValue());
        //salesMessageProducer.sendModuleMessage("", U)
    }

    @Override
    public List<List<String>> exportDocuments(@NotNull PageCriteriaDto<ReportQueryFilterDto> filters) throws ServiceException {
        PagedListDto<ReportListDto> search = search(filters.pageSize(Integer.MAX_VALUE).pageIndex(1));

        List<ReportListExportDto> reports = mapper.mapAsList(search.getItems(), ReportListExportDto.class);

        return reportServiceExportHelper.exportToList(reports);
    }

    @Override
    public List<List<String>> exportSelectedDocuments(@NotNull ExportListsDto exportListsDto) throws ServiceException {
        PagedListDto<ReportListDto> search;

        if (exportListsDto.isExportAll()) {
            search = getAllReports(exportListsDto);
        } else {
            search = getSelectedReports(exportListsDto);
        }

        List<ReportListExportDto> reports = mapper.mapAsList(search.getItems(), ReportListExportDto.class);

        return reportServiceExportHelper.exportToList(reports);
    }

    private PagedListDto<ReportListDto> getSelectedReports(ExportListsDto exportListsDto) throws ServiceException {
        PageCriteriaDto<ReportQueryFilterDto> criteria = exportListsDto.getCriteria();
        criteria.getFilters().includeFluxReportIds(exportListsDto.getIds());
        try {
            return search(criteria);
        } catch (ServiceException e) {
            throw new ServiceException("Something went wrong during CSV export of selected reports", e);
        }
    }

    private PagedListDto<ReportListDto> getAllReports(ExportListsDto exportListsDto) throws ServiceException {
        PageCriteriaDto<ReportQueryFilterDto> criteria = exportListsDto.getCriteria();
        criteria.getFilters().excludeFluxReportIds(exportListsDto.getIds());
        try {
            return search(criteria);
        } catch (ServiceException e) {
            throw new ServiceException("Something went wrong during CSV export of all reports", e);
        }
    }

    protected void setReportServiceExportHelper(ReportServiceExportHelper reportServiceExportHelper) {
        this.reportServiceExportHelper = reportServiceExportHelper;
    }
}
