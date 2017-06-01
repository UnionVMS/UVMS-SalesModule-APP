package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportListDto;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Class who's only purpose is to hide low-level logic from the search functionality of the ReportServiceBean.
 * Should not be used by any other class or functionality!
 */
@Stateless
public class SearchReportsHelper {

    final static Logger LOG = LoggerFactory.getLogger(SearchReportsHelper.class);

    @EJB
    private AssetService assetService;

    @EJB
    private ReportServiceHelper reportServiceHelper;

    @Inject
    private MapperFacade mapper;

    /**
     *  VesselName can also contain a vessel's CFR or IRCS. Because we don't have that kind of data, we ask the asset
     *  module to get their ids.
     */
    public void prepareVesselFreeTextSearch(ReportQuery query) throws ServiceException {
        ReportQueryFilter filters = query.getFilters();
        if (query.getFilters() != null && !Strings.isNullOrEmpty(filters.getVesselName())) {
            List<String> vesselExtIds = assetService.findExtIdsByNameOrCFROrIRCS(filters.getVesselName());
            if (!vesselExtIds.isEmpty()) {
                filters.getVesselExtIds()
                        .addAll(vesselExtIds);
                filters.setVesselName(null);
            }
        }
    }

    public void enrichWithVesselInformation(List<ReportListDto> reportDtos) {
        for (ReportListDto reportDto : reportDtos) {
            String vesselExtId = reportDto.getVesselExtId();
            try {
                Asset vessel = assetService.findByExtId(vesselExtId);
                reportDto.setIrcs(vessel.getIrcs());
                reportDto.setExternalMarking(vessel.getExternalMarking());
                reportDto.setVesselName(vessel.getName());
            } catch (ServiceException e) {
                LOG.error("Cannot retrieve vessel details of vessel " + vesselExtId, e);
            }
        }
    }

    /** When no sorting has been set, set a default sorting **/
    public void prepareSorting(ReportQuery query) {
        if (query.getSorting() == null
                || query.getSorting().getField() == null) {
            query.setSorting(new ReportQuerySorting()
                    .withDirection(SortDirection.ASCENDING)
                    .withField(ReportQuerySortField.SALES_DATE));
        }
    }

    public void enrichWithRelatedReports(Collection<ReportListDto> reportListDtos) {
        for (ReportListDto reportListDto: reportListDtos) {
            String referencedId = reportListDto.getReferencedId();
            List<Report> allReferencedReports = reportServiceHelper.findAllReportsThatAreCorrectedOrDeleted(referencedId);
            List<ReportListDto> mappedRelatedReports = mapper.mapAsList(allReferencedReports, ReportListDto.class);
            reportListDto.setRelatedReports(mappedRelatedReports);
        }
    }

}
