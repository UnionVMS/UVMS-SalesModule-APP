package eu.europa.ec.fisheries.uvms.sales.rest.filter;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionHandler extends UnionVMSResource implements ExceptionMapper<ServiceException> {

    final static Logger LOG = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    @Override
    @Produces("application/json")
    public Response toResponse(ServiceException exception) {
        LOG.error("Something went wrong invoking the rest service of the sales module.", exception);
        return createErrorResponse(exception.getMessage());
    }
}
