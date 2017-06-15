package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.uvms.init.AbstractModuleInitializerBean;

import javax.ejb.Singleton;
import java.io.InputStream;

/**
 * Created by MATBUL on 13/06/2017.
 */
@Singleton
public class SalesInitializerBean extends AbstractModuleInitializerBean {
    @Override
    protected InputStream getDeploymentDescriptorRequest() {
        return this.getClass().getClassLoader().getResourceAsStream("usmDeploymentDescriptor.xml");
    }

    @Override
    protected boolean mustRedeploy() {
        return false;
    }
}
