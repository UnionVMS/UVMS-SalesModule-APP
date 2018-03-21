package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.uvms.sales.service.SavedSearchService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.SavedSearchGroupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/savedSearch")
@Stateless
public class SavedSearchResource extends UnionVMSResource {

    static final Logger LOG = LoggerFactory.getLogger(SavedSearchResource.class);

    @EJB
    private SavedSearchService savedSearchService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @RequiresFeature(UnionVMSFeature.viewSalesReports)
    public Response getSavedSearchesByUser(@QueryParam(value = "user") final String user) {
        MDC.put("requestId", UUID.randomUUID().toString());
        LOG.info("Get saved searches by user");
        return createSuccessResponse(savedSearchService.getSavedSearches(user));
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @RequiresFeature(UnionVMSFeature.manageSalesReports)
    public Response createSavedSearch(SavedSearchGroupDto searchGroupDto) {
        MDC.put("requestId", UUID.randomUUID().toString());
        LOG.info("Save search group");
        return createSuccessResponse(savedSearchService.saveSearch(searchGroupDto));
    }

    @DELETE
    @Produces(value = {MediaType.APPLICATION_JSON})
    @RequiresFeature(UnionVMSFeature.manageSalesReports)
    public Response deleteSavedSearch(@QueryParam("id") Integer id) {
        MDC.put("requestId", UUID.randomUUID().toString());
        LOG.info("Delete search for id");
        savedSearchService.deleteSearch(id);
        return createSuccessResponse();
    }
}
