package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

public class TransactionalMockTests extends BuildSalesServiceMockTestDeployment {

    @PersistenceContext
    protected EntityManager em;

    @Before
    public void before() throws SystemException, NotSupportedException {
    }

    @After
    public void after() throws SystemException {
    }

}
