package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.sales.service.ReportService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ExportListsDto;
import eu.europa.ec.fisheries.uvms.sales.service.dto.PageCriteriaDto;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportQueryFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/report")
@Stateless
public class ReportResource extends UnionVMSResource {

    final static Logger LOG = LoggerFactory.getLogger(ReportResource.class);

    @EJB
    private ReportService reportService;

    /**
     * Finds the details of a sales report by extId
     * @param extId externalId of the sales report
     * @responseType eu.europa.ec.fisheries.uvms.sales.rest.dto.ResponseDto<eu.europa.ec.fisheries.uvms.sales.service.dto>
     * @return details of a sales report
     *
     */
    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response findByExtId(@QueryParam(value = "id") final String extId) {
        LOG.info("Find report by ext id invoked in rest layer");
        return createSuccessResponse(reportService.findSalesDetails(extId));
    }

    /**
     * Search for sales reports matching the given criteria. Return a list of the found reports. When nothing found, an empty list is returned.
     * @param filters criteria to search on
     * @return found flux reports. An empty list when nothing found.
     *
     */
    @POST
    @Path("search")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response search(PageCriteriaDto<ReportQueryFilterDto> filters) throws ServiceException {
        LOG.info("Search reports invoked in rest layer");
        return createSuccessResponse(reportService.search(filters));
    }

    @POST
    @Path("export")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response export(PageCriteriaDto<ReportQueryFilterDto> filters) throws ServiceException {
        return createSuccessResponse(reportService.exportDocuments(filters));
    }

    @POST
    @Path("exportSelected")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response export(ExportListsDto exportListsDto) throws ServiceException {
        return createSuccessResponse(reportService.exportSelectedDocuments(exportListsDto));
    }


}
