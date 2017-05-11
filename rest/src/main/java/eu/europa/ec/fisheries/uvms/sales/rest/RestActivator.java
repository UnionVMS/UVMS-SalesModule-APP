package eu.europa.ec.fisheries.uvms.sales.rest;

import eu.europa.ec.fisheries.uvms.sales.rest.constants.RestConstants;
import eu.europa.ec.fisheries.uvms.sales.rest.filter.JacksonObjectMapperProvider;
import eu.europa.ec.fisheries.uvms.sales.rest.filter.ServiceExceptionHandler;
import eu.europa.ec.fisheries.uvms.sales.rest.service.CodeListResource;
import eu.europa.ec.fisheries.uvms.sales.rest.service.ReportResource;
import eu.europa.ec.fisheries.uvms.sales.rest.service.SavedSearchResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(RestConstants.MODULE_REST)
public class RestActivator extends Application {

    final static Logger LOG = LoggerFactory.getLogger(RestActivator.class);

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(ReportResource.class);
        set.add(CodeListResource.class);
        set.add(SavedSearchResource.class);
        set.add(ServiceExceptionHandler.class);
        set.add(JacksonObjectMapperProvider.class);
        LOG.info(RestConstants.MODULE_NAME + " module starting up");
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
