package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.bean.AddressDaoBean;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Address;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

//TODO: liquibase

/**
 * This is a test for the functionality of the BaseDAO. We use the AddressDao as an implementation.
 **/
public class BaseDaoTest extends AbstractDaoTest<AddressDaoBean> {

    @Test
    @DataSet(expectedData = "data/BaseDaoTest-testCreate-expected.xml")
    public void testCreate() {
        Address address = new Address();
        address.setCity("test-city");
        address.setCountryCode("BEL");
        address.setStreet("test-street");
        dao.create(address);
    }

    @Test
    @DataSet(initialData = "data/BaseDaoTest-testFindByIdOrNull-initial.xml")
    public void testFindByIdOrNullWhenSomethingFound() {
        assertNotNull(dao.findByIdOrNull(1));
    }

    @Test
    @DataSet(initialData = "data/BaseDaoTest-testFindByIdOrNull-initial.xml")
    public void testFindByIdOrNullWhenNothingFound() {
        assertNull(dao.findByIdOrNull(50));
    }

    @Test
    @DataSet(initialData = "data/BaseDaoTest-testFindByIdOrNull-initial.xml", expectedData = "data/BaseDaoTest-testDelete-expected.xml")
    public void testDelete() {
        dao.delete(dao.findByIdOrNull(1));
    }
}
