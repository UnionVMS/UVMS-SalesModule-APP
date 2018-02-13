package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.uvms.sales.service.arquillian.deployment.BuildSalesServiceTestDeployment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TransactionalTests extends BuildSalesServiceTestDeployment {

    @PersistenceContext
    protected EntityManager em;

}
