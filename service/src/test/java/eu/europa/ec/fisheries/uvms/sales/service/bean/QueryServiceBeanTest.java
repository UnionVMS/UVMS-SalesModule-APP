package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.SalesQueryType;
import eu.europa.ec.fisheries.uvms.sales.domain.QueryDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.bean.QueryDomainModelBean;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.QueryDao;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class QueryServiceBeanTest {

    @InjectMocks
    QueryServiceBean service;

    @Mock
    QueryDomainModel domainModel;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void saveQuery() throws Exception {
        SalesQueryType query = new SalesQueryType();

        service.saveQuery(query);

        verify(domainModel).create(query);
        verifyNoMoreInteractions(domainModel);
    }

    @Test
    public void saveQueryWhenInputIsNull() throws Exception {
        exception.expect(NullPointerException.class);

        service.saveQuery(null);

        verifyNoMoreInteractions(domainModel);
    }

}