package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.sales.service.CodeListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

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
        MDC.put("requestId", UUID.randomUUID().toString());
        LOG.info("Get code lists");
        return createSuccessResponse(codeListService.getCodeLists());
    }

}
