package eu.europa.ec.fisheries.uvms.sales.rest.service;

import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
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

    final static Logger LOG = LoggerFactory.getLogger(CodeListResource.class);

    @EJB
    private CodeListService codeListService;

    /**
     * Returns the MDR code lists
     * @responseType eu.europa.ec.fisheries.uvms.sales.rest.dto.ResponseDto<eu.europa.ec.fisheries.uvms.sales.service.dto>
     * @return details of a sales report
     *
     */
    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getCodeLists() {
        return createSuccessResponse(codeListService.getCodeLists());
    }

}
