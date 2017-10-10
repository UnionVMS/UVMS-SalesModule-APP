package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.schema.sales.SalesQueryType;
import eu.europa.ec.fisheries.uvms.sales.domain.*;
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
    private UniqueIdServiceBean service;

    @Mock
    private ReportDomainModel reportDomainModel;

    @Mock
    private DocumentDomainModel documentDomainModel;

    @Mock
    private QueryDomainModel queryDomainModel;

    @Mock
    private ResponseDomainModel responseDomainModel;

    @Mock
    private ErroneousMessageDomainModel erroneousMessageDomainModel;

    @Test
    public void doesAnySalesDocumentExistWithAnyOfTheseIdsWhenADocumentExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(documentDomainModel).findByExtId("aaa");
        doReturn(Optional.of(new SalesDocumentType())).when(documentDomainModel).findByExtId("bbb");

        Boolean notUnique = service.doesAnySalesDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(documentDomainModel).findByExtId("aaa");
        verify(documentDomainModel).findByExtId("bbb");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);
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
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenNoReportsExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa");
        doReturn(false).when(erroneousMessageDomainModel).exists("aaa");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("bbb");
        doReturn(false).when(erroneousMessageDomainModel).exists("bbb");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("ccc");
        doReturn(false).when(erroneousMessageDomainModel).exists("ccc");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa");
        verify(erroneousMessageDomainModel).exists("aaa");
        verify(reportDomainModel).findByExtId("bbb");
        verify(erroneousMessageDomainModel).exists("bbb");
        verify(reportDomainModel).findByExtId("ccc");
        verify(erroneousMessageDomainModel).exists("ccc");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenAReportExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa");
        doReturn(false).when(erroneousMessageDomainModel).exists("aaa");
        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId("bbb");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa");
        verify(erroneousMessageDomainModel).exists("aaa");
        verify(reportDomainModel).findByExtId("bbb");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);
        assertTrue(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenAnErroneousReportExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa");
        doReturn(false).when(erroneousMessageDomainModel).exists("aaa");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("bbb");
        doReturn(true).when(erroneousMessageDomainModel).exists("bbb");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa");
        verify(erroneousMessageDomainModel).exists("aaa");
        verify(reportDomainModel).findByExtId("bbb");
        verify(erroneousMessageDomainModel).exists("bbb");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);
        assertTrue(notUnique);
    }

    @Test
    public void doesReferencedReportInResponseExistWhenItExistsAsAReport() throws Exception {
        String id = "extId";

        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportInResponseExistWhenItExistsAsAnErroneousReport() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id);
        doReturn(Optional.absent()).when(queryDomainModel).findByExtId(id);
        doReturn(true).when(erroneousMessageDomainModel).exists(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verify(queryDomainModel).findByExtId(id);
        verify(erroneousMessageDomainModel).exists(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportInResponseExistWhenItExistsAsAQuery() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id);
        doReturn(Optional.of(new SalesQueryType())).when(queryDomainModel).findByExtId(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verify(queryDomainModel).findByExtId(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportInResponseExistWhenItDoesNotExist() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id);
        doReturn(Optional.absent()).when(queryDomainModel).findByExtId(id);
        doReturn(false).when(erroneousMessageDomainModel).exists(id);

        boolean doesExist = service.doesReferencedReportInResponseExist(id);

        verify(reportDomainModel).findByExtId(id);
        verify(queryDomainModel).findByExtId(id);
        verify(erroneousMessageDomainModel).exists(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, erroneousMessageDomainModel);

        assertFalse(doesExist);
    }

}