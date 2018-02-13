package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.uvms.sales.service.arquillian.deployment.BuildSalesServiceMockTestDeployment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TransactionalMockTests extends BuildSalesServiceMockTestDeployment {

    @PersistenceContext
    protected EntityManager em;

}
