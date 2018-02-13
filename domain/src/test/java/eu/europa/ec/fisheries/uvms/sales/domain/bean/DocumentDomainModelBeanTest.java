package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.DocumentDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class DocumentDomainModelBeanTest {

    @InjectMocks
    DocumentDomainModelBean documentDomainModel;

    @Mock
    DocumentDao dao;

    @Mock
    MapperFacade mapper;

    @Test
    public void findByExtIdWhenDocumentWasFound() throws Exception {
        List<Document> documents = Arrays.asList(new Document());
        List<SalesDocumentType> salesDocuments = Arrays.asList(new SalesDocumentType());

        doReturn(documents).when(dao).findByExtId("extId");
        doReturn(salesDocuments).when(mapper).mapAsList(documents, SalesDocumentType.class);

        List<SalesDocumentType> foundSalesDocument = documentDomainModel.findByExtId("extId");

        verify(dao).findByExtId("extId");
        verify(mapper).mapAsList(documents, SalesDocumentType.class);
        verifyNoMoreInteractions(dao, mapper);

        assertEquals(salesDocuments, foundSalesDocument);
    }

    @Test
    public void findByExtIdWhenNoDocumentWasFound() throws Exception {
        doReturn(new ArrayList<>()).when(dao).findByExtId("extId");
        doReturn(new ArrayList<>()).when(mapper).mapAsList(new ArrayList<>(), SalesDocumentType.class);

        List<SalesDocumentType> foundSalesDocuments = documentDomainModel.findByExtId("extId");

        verify(dao).findByExtId("extId");
        verifyNoMoreInteractions(dao);

        assertTrue(foundSalesDocuments.isEmpty());
    }


}