package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.uvms.sales.service.SavedSearchService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.SavedSearchGroupDto;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/savedSearch")
@Stateless
public class SavedSearchResource extends UnionVMSResource {

    @EJB
    private SavedSearchService savedSearchService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @RequiresFeature(UnionVMSFeature.viewSalesReports)
    public Response getSavedSearchesByUser(@QueryParam(value = "user") final String user) {
        return createSuccessResponse(savedSearchService.getSavedSearches(user));
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @RequiresFeature(UnionVMSFeature.manageSalesReports)
    public Response createSavedSearch(SavedSearchGroupDto searchGroupDto) {
        return createSuccessResponse(savedSearchService.saveSearch(searchGroupDto));
    }

    @DELETE
    @Produces(value = {MediaType.APPLICATION_JSON})
    @RequiresFeature(UnionVMSFeature.manageSalesReports)
    public Response deleteSavedSearch(@QueryParam("id") Integer id) {
        savedSearchService.deleteSearch(id);

        return createSuccessResponse();
    }
}
