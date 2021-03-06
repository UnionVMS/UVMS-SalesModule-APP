package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.sales.service.CodeListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/codelists")
@Stateless
public class CodeListResource extends UnionVMSResource {

    static final Logger LOG = LoggerFactory.getLogger(CodeListResource.class);

    @EJB
    private CodeListService codeListService;

    /**
     * Returns the MDR code lists
     * @return details of a sales report
     *
     */
    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getCodeLists() {
        LOG.info("Get code lists");
        return createSuccessResponse(codeListService.getCodeLists());
    }

}
