package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.QueryDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Query;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.QueryParameterType;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class QueryDomainModelBeanTest {

    @InjectMocks
    QueryDomainModelBean queryDomainModelBean;

    @Mock
    QueryDao dao;

    @Mock
    MapperFacade mapper;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testCreateQuery() throws Exception {
        SalesQueryType salesQueryType = createSalesQueryType();
        Query query = createQuery();

        doReturn(query).when(mapper).map(salesQueryType, Query.class);
        doReturn(query).when(dao).create(query);
        doReturn(salesQueryType).when(mapper).map(query, SalesQueryType.class);

        queryDomainModelBean.create(salesQueryType);
    }

    @Test
    public void testCreateQueryWhenQueryIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("query cannot be null in QueryDomainModelBean::create");
        queryDomainModelBean.create(null);
    }

    @Test
    public void findQueryByExtId() throws Exception {
        SalesQueryType salesQueryType = createSalesQueryType();
        Query query = createQuery();

        doReturn(salesQueryType).when(mapper).map(query, SalesQueryType.class);
        doReturn(Optional.of(query)).when(dao).findByExtId("extId");

        Optional<SalesQueryType> optionalQuery = queryDomainModelBean.findByExtId("extId");

        verify(mapper).map(query, SalesQueryType.class);
        verify(dao).findByExtId("extId");
        verifyNoMoreInteractions(mapper, dao);

        assertTrue(optionalQuery.isPresent());
        assertEquals(salesQueryType, optionalQuery.get());
    }

    @Test
    public void findQueryByExtIdWhenNoQueryWasFound() throws Exception {
        doReturn(Optional.absent()).when(dao).findByExtId("extId");

        queryDomainModelBean.findByExtId("extId");

        verify(dao).findByExtId("extId");
        verifyNoMoreInteractions(mapper, dao);
    }

    @Test
    public void findQueryByExtIdWhenExtIdIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("extId cannot be null in QueryDomainModelBean::findByExtId");

        queryDomainModelBean.findByExtId(null);
    }

    @Test
    public void findQueryByExtIdWhenExtIdIsBlank() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("extId cannot be null in QueryDomainModelBean::findByExtId");

        queryDomainModelBean.findByExtId("");
    }

    /**
     * @Override
    public SalesQueryType create(SalesQueryType query) {
    Query domainQuery = mapper.map(query, Query.class);
    return mapper.map(dao.create(domainQuery), SalesQueryType.class);
    }

     @Override
     public Optional<SalesQueryType> findByExtId(String extId) {
     checkNotNull(extId, "extId cannot be null in QueryDomainModelBean.findByExtId");

     return dao.findByExtId(extId);
     }
     */

    private SalesQueryType createSalesQueryType() {
        SalesQueryParameterType salesQueryParameterType = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("typeCode"))
                .withValueCode(new CodeType().withValue("valueCode"))
                .withValueDateTime(new DateTimeType().withDateTime(DateTime.parse("1995-11-24")))
                .withValueID(new IDType().withValue("idType"));

        return new SalesQueryType()
                .withTypeCode(new CodeType().withValue("typeCode"))
                .withID(new IDType().withValue("id"))
                .withSpecifiedDelimitedPeriod(
                        new DelimitedPeriodType()
                                .withStartDateTime(new DateTimeType().withDateTime(DateTime.parse("2016-08-01")))
                                .withEndDateTime(new DateTimeType().withDateTime(DateTime.parse("2060-08-01"))))
                .withSubmittedDateTime(new DateTimeType().withDateTime(DateTime.parse("2017-09-05")))
                .withSimpleSalesQueryParameters(salesQueryParameterType);
    }

    private Query createQuery() {
        QueryParameterType queryParameter = new QueryParameterType()
                .typeCode("typeCode")
                .valueCode("valueCode")
                .valueDateTime(DateTime.parse("1995-11-24"))
                .valueID("idType");

        return new Query()
                .queryType("typeCode")
                .extId("id")
                .startDate(DateTime.parse("2016-08-01"))
                .endDate(DateTime.parse("2060-08-01"))
                .submittedDate(DateTime.parse("2017-09-05"))
                .parameters(Arrays.asList(queryParameter));
    }
}