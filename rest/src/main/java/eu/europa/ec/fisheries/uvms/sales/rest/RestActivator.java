package eu.europa.ec.fisheries.uvms.sales.rest;

import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeatureFilter;
import eu.europa.ec.fisheries.uvms.sales.rest.constants.RestConstants;
import eu.europa.ec.fisheries.uvms.sales.rest.filter.JacksonObjectMapperProvider;
import eu.europa.ec.fisheries.uvms.sales.rest.filter.SalesServiceExceptionHandler;
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

    static final Logger LOG = LoggerFactory.getLogger(RestActivator.class);

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(ReportResource.class);
        set.add(CodeListResource.class);
        set.add(SavedSearchResource.class);
        set.add(SalesServiceExceptionHandler.class);
        set.add(JacksonObjectMapperProvider.class);
        set.add(UnionVMSFeatureFilter.class);
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
