package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportListDto;
import eu.europa.ec.fisheries.uvms.sales.service.mapper.DTO;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Class who's only purpose is to hide low-level logic from the search functionality of the ReportServiceBean.
 * Should not be used by any other class or functionality!
 */
@Stateless
public class SearchReportsHelper {

    static final Logger LOG = LoggerFactory.getLogger(SearchReportsHelper.class);

    @EJB
    private AssetService assetService;

    @EJB
    private ReportDomainModel reportDomainModel;

    @Inject @DTO
    private MapperFacade mapper;

    /**
     *  VesselName can also contain a vessel's CFR or IRCS. Because we don't have that kind of data, we ask the asset
     *  module to get their ids.
     */
    public void prepareVesselFreeTextSearch(ReportQuery query) {
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

            if (isNotBlank(vesselExtId)) {
                try {
                    Asset vessel = assetService.findByCFR(vesselExtId);
                    reportDto.setIrcs(vessel.getIrcs());
                    reportDto.setExternalMarking(vessel.getExternalMarking());
                } catch (SalesServiceException e) {
                    LOG.error("Cannot retrieve vessel details of vessel " + vesselExtId, e);
                }
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

    public void enrichWithOlderVersions(Collection<ReportListDto> reportListDtos) {
        for (ReportListDto reportListDto: reportListDtos) {
            String referencedId = reportListDto.getReferencedId();
            List<ReportSummary> allReferencedReports = reportDomainModel.findOlderVersionsOrderedByCreationDateDescending(referencedId);
            List<ReportListDto> mappedRelatedReports = mapper.mapAsList(allReferencedReports, ReportListDto.class);
            reportListDto.setOlderVersions(mappedRelatedReports);
        }
    }

    public void includeDeletedReportsInQuery(ReportQuery query) {
        setIncludeDeletedReportsInQuery(query, true);
    }

    public void excludeDeletedReportsInQuery(ReportQuery query) {
        setIncludeDeletedReportsInQuery(query, false);
    }

    private void setIncludeDeletedReportsInQuery(ReportQuery query, boolean includeDeleted) {
        if (query.getFilters() == null) {
            query.setFilters(new ReportQueryFilter());
        }

        query.getFilters().setIncludeDeleted(includeDeleted);
    }
}
