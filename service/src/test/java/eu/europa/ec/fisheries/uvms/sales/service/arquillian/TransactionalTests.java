package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TransactionalTests extends BuildSalesServiceTestDeployment {

    @PersistenceContext
    protected EntityManager em;

}
