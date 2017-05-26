package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.sales.service.SavedSearchService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.SavedSearchGroupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/savedSearch")
@Stateless
public class SavedSearchResource extends UnionVMSResource {

    final static Logger LOG = LoggerFactory.getLogger(SavedSearchResource.class);

    @EJB
    private SavedSearchService savedSearchService;

    /**
     * Returns the MDR code lists
     *
     * @return details of a sales report
     * @responseType eu.europa.ec.fisheries.uvms.sales.rest.dto.ResponseDto<eu.europa.ec.fisheries.uvms.sales.service.dto>
     */
    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getSavedSearchesByUser(@QueryParam(value = "user") final String user) {
        return createSuccessResponse(savedSearchService.getSavedSearches(user));
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createSavedSearch(SavedSearchGroupDto searchGroupDto) {
        return createSuccessResponse(savedSearchService.saveSearch(searchGroupDto));
    }

    @DELETE
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response deleteSavedSearch(@QueryParam("id") Integer id) throws ServiceException {
        savedSearchService.deleteSearch(id);

        return createSuccessResponse();
    }
}
