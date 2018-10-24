package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.schema.sales.DateTimeType;
import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.schema.sales.IDType;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.ResponseDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Response;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ResponseDomainModelBeanTest {

    @InjectMocks
    ResponseDomainModelBean domainModelBean;

    @Mock
    ResponseDao dao;

    @Mock
    MapperFacade mapper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void create() throws Exception {
        DateTime now = DateTime.now();
        FLUXResponseDocumentType responseDocumentType = createFluxResponseDocumentType(now);
        Response response = createResponse(now);

        doReturn(response).when(mapper).map(responseDocumentType, Response.class);
        doReturn(response).when(dao).create(response);
        doReturn(responseDocumentType).when(mapper).map(response, FLUXResponseDocumentType.class);

        FLUXResponseDocumentType result = domainModelBean.create(responseDocumentType);

        assertEquals(responseDocumentType, result);
        verify(mapper).map(responseDocumentType, Response.class);
        verify(dao).create(response);
        verify(mapper).map(response, FLUXResponseDocumentType.class);
        verifyNoMoreInteractions(mapper, dao);
    }

    @Test
    public void createWhenInputIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("response cannot be null in ResponseDomainModelBean::create");
        domainModelBean.create(null);
    }

    @Test
    public void findByExtId() throws Exception {
        DateTime now = DateTime.now();
        FLUXResponseDocumentType responseDocumentType = createFluxResponseDocumentType(now);
        Response response = createResponse(now);

        doReturn(responseDocumentType).when(mapper).map(response, FLUXResponseDocumentType.class);
        doReturn(Optional.of(response)).when(dao).findByExtId("extId");

        Optional<FLUXResponseDocumentType> optionalQuery = domainModelBean.findByExtId("extId");

        verify(mapper).map(response, FLUXResponseDocumentType.class);
        verify(dao).findByExtId("extId");
        verifyNoMoreInteractions(mapper, dao);

        assertTrue(optionalQuery.isPresent());
        assertEquals(responseDocumentType, optionalQuery.get());
    }

    @Test
    public void findByExtIdWhenNoResultWasFound() throws Exception {
        doReturn(Optional.empty()).when(dao).findByExtId("extId");

        Optional<FLUXResponseDocumentType> optionalQuery = domainModelBean.findByExtId("extId");

        verify(dao).findByExtId("extId");
        verifyNoMoreInteractions(mapper, dao);

        assertFalse(optionalQuery.isPresent());
    }

    @Test
    public void findByExtIdWhenInputIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("extId cannot be null or blank in ResponseDomainModelBean::findByExtId");
        domainModelBean.findByExtId(null);
    }

    @Test
    public void findByExtIdWhenInputIsBlank() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("extId cannot be null or blank in ResponseDomainModelBean::findByExtId");
        domainModelBean.findByExtId("");
    }

    @Test
    public void findByReferencedId() throws Exception {
    }

    private FLUXResponseDocumentType createFluxResponseDocumentType(DateTime now) {
        return new FLUXResponseDocumentType()
                .withReferencedID(new IDType().withValue("referencedID"))
                .withCreationDateTime(new DateTimeType().withDateTime(now))
                .withResponseCode(new CodeType().withValue("OK"));
    }

    private Response createResponse(DateTime now) {
        return new Response()
                .referencedId("referencedID")
                .creationDateTime(now)
                .responseCode("OK");
    }

}