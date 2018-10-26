package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.Response;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.junit.Assert.*;

public class ResponseDaoBeanTest extends AbstractDaoTest<ResponseDaoBean> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByExtId() throws Exception {
        Optional<Response> response = dao.findByExtId("abc123");

        assertTrue(response.isPresent());
        assertEquals("abc123", response.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByExtIdWhenNoResponseWasFound() throws Exception {
        Optional<Response> response = dao.findByExtId("321cba");

        assertFalse(response.isPresent());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByExtIdWhenMoreThanOneResponseWasFound() throws Exception {
        String id = "abc";

        exception.expect(SalesNonBlockingException.class);
        exception.expectMessage("More than one result found for 'findByExtId' on entity Response in table 'sales_response', id: " + id);

        Optional<Response> response = dao.findByExtId("abc");
        assertTrue(response.isPresent());
        assertEquals(id, response.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByReferencedId() throws Exception {
        Optional<Response> response = dao.findByReferencedId("find me");

        assertTrue(response.isPresent());
        assertEquals("find me", response.get().getReferencedId());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByReferencedIdWhenNoResultFound() throws Exception {
        Optional<Response> response = dao.findByExtId("this will not be found");

        assertFalse(response.isPresent());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByReferencedIdWhenMultipleResultsFound() throws Exception {
        String id = "321cba";

        exception.expect(SalesNonBlockingException.class);
        exception.expectMessage("More than one result found for 'findByReferencedId' on entity Response in table 'sales_response', id: " + id);

        Optional<Response> response = dao.findByReferencedId(id);

        assertFalse(response.isPresent());
    }


    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByExtIdWhenAllUppercase() throws Exception {
        Optional<Response> response = dao.findByExtId("ABC123");

        assertTrue(response.isPresent());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByExtIdWhenIdHasUppercase() throws Exception {
        Optional<Response> response = dao.findByExtId("AbC123");

        assertTrue(response.isPresent());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByExtIdWhenAllLowercase() throws Exception {
        Optional<Response> response = dao.findByExtId("abc123");

        assertTrue(response.isPresent());
    }


    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByReferencedIdWhenAllUppercase() throws Exception {
        Optional<Response> response = dao.findByReferencedId("FIND ME");

        assertTrue(response.isPresent());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByReferencedIdWhenIdHasUppercase() throws Exception {
        Optional<Response> response = dao.findByReferencedId("FiNd Me");

        assertTrue(response.isPresent());
    }

    @Test
    @DataSet(initialData = "data/responseDaoBeanTest-testFindByExtId-initial.xml")
    public void findByReferencedIdWhenAllLowercase() throws Exception {
        Optional<Response> response = dao.findByReferencedId("find me");

        assertTrue(response.isPresent());
    }


}