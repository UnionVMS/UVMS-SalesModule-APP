package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Query;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.QueryParameterType;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class QueryDaoBeanTest extends AbstractDaoTest<QueryDaoBean> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @DataSet(expectedData = "data/queryDaoBeanTest-testCreate-expected.xml")
    public void createQuery() throws Exception {
        Query query = new Query()
                .extId("extId")
                .submittedDate(DateTime.parse("2001-9-11T00:00:00"))
                .queryType("SN+TOD")
                .startDate(DateTime.now())
                .endDate(DateTime.now())
                .submitterFLUXParty("FLUXParty");

        QueryParameterType queryParameterType = new QueryParameterType()
                .typeCode("bla")
                .query(query);

        query.parameters(Arrays.asList(queryParameterType));

        dao.create(query);
    }

    @Test
    @DataSet(initialData = "data/queryDaoBeanTest-testCreate-initial.xml")
    public void findByExtId() throws Exception {
        Optional<Query> query = dao.findByExtId("abc123");

        assertTrue(query.isPresent());
        assertEquals("abc123", query.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/queryDaoBeanTest-testCreate-initial.xml")
    public void findByExtIdWhenMoreThanOneResultWasFound() throws Exception {
        String id = "abc321";

        exception.expect(SalesNonBlockingException.class);
        exception.expectMessage("More than one result found for 'findByExtId' on entity Query in table 'sales_query', id: " + id);

        Optional<Query> query = dao.findByExtId(id);

        assertTrue(query.isPresent());
        assertEquals(id, query.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/queryDaoBeanTest-testCreate-initial.xml")
    public void findByExtIdWhenNoResultWasFound() throws Exception {
        String id = "this does not exist";
        Optional<Query> query = dao.findByExtId(id);

        assertFalse(query.isPresent());
    }

}