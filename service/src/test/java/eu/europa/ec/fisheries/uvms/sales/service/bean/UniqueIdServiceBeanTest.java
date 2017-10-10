package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.schema.sales.SalesQueryType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.QueryDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.ResponseDomainModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UniqueIdServiceBeanTest {

    @InjectMocks
    UniqueIdServiceBean service;

    @Mock
    ReportDomainModel reportDomainModel;

    @Mock
    DocumentDomainModel documentDomainModel;

    @Mock
    QueryDomainModel queryDomainModel;

    @Mock
    ResponseDomainModel responseDomainModel;

    @Test
    public void doesAnySalesDocumentExistWithAnyOfTheseIdsWhenADocumentExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(documentDomainModel).findByExtId("aaa");
        doReturn(Optional.of(new SalesDocumentType())).when(documentDomainModel).findByExtId("bbb");

        Boolean notUnique = service.doesAnySalesDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(documentDomainModel).findByExtId("aaa");
        verify(documentDomainModel).findByExtId("bbb");
        verifyNoMoreInteractions(reportDomainModel);
        assertTrue(notUnique);
    }

    @Test
    public void doesAnySalesDocumentExistWithAnyOfTheseIdsWhenNoDocumentExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(documentDomainModel).findByExtId("aaa");
        doReturn(Optional.absent()).when(documentDomainModel).findByExtId("bbb");
        doReturn(Optional.absent()).when(documentDomainModel).findByExtId("ccc");

        Boolean notUnique = service.doesAnySalesDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(documentDomainModel).findByExtId("aaa");
        verify(documentDomainModel).findByExtId("bbb");
        verify(documentDomainModel).findByExtId("ccc");
        verifyNoMoreInteractions(reportDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenNoReportsExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("bbb");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("ccc");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa");
        verify(reportDomainModel).findByExtId("bbb");
        verify(reportDomainModel).findByExtId("ccc");
        verifyNoMoreInteractions(reportDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenAReportExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa");
        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId("bbb");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa");
        verify(reportDomainModel).findByExtId("bbb");
        verifyNoMoreInteractions(reportDomainModel);
        assertTrue(notUnique);
    }

    /**
     * public boolean doesReferencedReportInResponseExist(String referencedId) {
     * // We have to check if the referencedId exists either as a report or as a query
     * <p>
     * boolean idExistsAsReport = Optional.fromNullable(reportDomainModel.findByExtId(referencedId)).isPresent();
     * if (idExistsAsReport) {
     * return true;
     * }
     * <p>
     * boolean idExistsAsQuery = queryDomainModel.findByExtId(referencedId).isPresent();
     * if (idExistsAsQuery) {
     * return true;
     * }
     * <p>
     * return false;
     * }
     */

    @Test
    public void doesReferencedReportInResponseExistWhenItExistsAsReport() throws Exception {
        String id = "extId";

        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verifyNoMoreInteractions(reportDomainModel, documentDomainModel, queryDomainModel, responseDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportInResponseExistWhenItExistsAsQuery() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id);
        doReturn(Optional.of(new SalesQueryType())).when(queryDomainModel).findByExtId(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verify(queryDomainModel).findByExtId(id);
        verifyNoMoreInteractions(reportDomainModel, documentDomainModel, queryDomainModel, responseDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportInResponseExistWhenItDoesNotExist() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id);
        doReturn(Optional.absent()).when(queryDomainModel).findByExtId(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verify(queryDomainModel).findByExtId(id);
        verifyNoMoreInteractions(reportDomainModel, documentDomainModel, queryDomainModel, responseDomainModel);

        assertFalse(doesExist);
    }

}