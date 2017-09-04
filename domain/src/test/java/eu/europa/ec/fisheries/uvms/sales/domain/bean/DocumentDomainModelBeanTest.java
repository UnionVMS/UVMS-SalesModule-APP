package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.DocumentDao;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.bean.DocumentDaoBean;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.MapperProducer;
import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


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
        Optional<Document> documentOptional = Optional.of(new Document());
        Optional<SalesDocumentType> salesDocumentTypeOptional = Optional.of(new SalesDocumentType());

        doReturn(documentOptional).when(dao).findByExtId("extId");
        doReturn(salesDocumentTypeOptional.get()).when(mapper).map(documentOptional.get(), SalesDocumentType.class);
        Optional<SalesDocumentType> foundSalesDocument = documentDomainModel.findByExtId("extId");

        verify(dao).findByExtId("extId");
        verify(mapper).map(documentOptional.get(), SalesDocumentType.class);
        verifyNoMoreInteractions(dao, mapper);

        assertEquals(salesDocumentTypeOptional, foundSalesDocument);
    }

    @Test
    public void findByExtIdWhenNoDocumentWasFound() throws Exception {
        Optional<Document> documentOptional = Optional.of(new Document());
        Optional<SalesDocumentType> salesDocumentTypeOptional = Optional.of(new SalesDocumentType());

        doReturn(Optional.absent()).when(dao).findByExtId("extId");
        Optional<SalesDocumentType> foundSalesDocument = documentDomainModel.findByExtId("extId");

        verify(dao).findByExtId("extId");
        verifyNoMoreInteractions(dao);

        assertEquals(Optional.absent(), foundSalesDocument);
    }


}