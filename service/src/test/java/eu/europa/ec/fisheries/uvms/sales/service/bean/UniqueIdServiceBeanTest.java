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

import java.util.ArrayList;
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
    private UnsavedMessageDomainModel unsavedMessageDomainModel;

    @Test
    public void doesAnySalesDocumentExistWithAnyOfTheseIdsWhenADocumentExistsWithGivenIds() throws Exception {
        doReturn(new ArrayList<>()).when(documentDomainModel).findByExtId("aaa");
        doReturn(Arrays.asList(new SalesDocumentType())).when(documentDomainModel).findByExtId("bbb");

        Boolean notUnique = service.doesAnySalesDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(documentDomainModel).findByExtId("aaa");
        verify(documentDomainModel).findByExtId("bbb");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);
        assertTrue(notUnique);
    }

    @Test
    public void doesAnySalesDocumentExistWithAnyOfTheseIdsWhenNoDocumentExistsWithGivenIds() throws Exception {
        doReturn(new ArrayList<>()).when(documentDomainModel).findByExtId("aaa");
        doReturn(new ArrayList<>()).when(documentDomainModel).findByExtId("bbb");
        doReturn(new ArrayList<>()).when(documentDomainModel).findByExtId("ccc");

        Boolean notUnique = service.doesAnySalesDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(documentDomainModel).findByExtId("aaa");
        verify(documentDomainModel).findByExtId("bbb");
        verify(documentDomainModel).findByExtId("ccc");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenNoReportsExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa", true);
        doReturn(false).when(unsavedMessageDomainModel).exists("aaa");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("bbb", true);
        doReturn(false).when(unsavedMessageDomainModel).exists("bbb");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("ccc", true);
        doReturn(false).when(unsavedMessageDomainModel).exists("ccc");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa", true);
        verify(unsavedMessageDomainModel).exists("aaa");
        verify(reportDomainModel).findByExtId("bbb", true);
        verify(unsavedMessageDomainModel).exists("bbb");
        verify(reportDomainModel).findByExtId("ccc", true);
        verify(unsavedMessageDomainModel).exists("ccc");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenAReportExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa", true);
        doReturn(false).when(unsavedMessageDomainModel).exists("aaa");
        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId("bbb", true);

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa", true);
        verify(unsavedMessageDomainModel).exists("aaa");
        verify(reportDomainModel).findByExtId("bbb", true);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);
        assertTrue(notUnique);
    }

    @Test
    public void doesAnySalesReportExistWithAnyOfTheseIdsWhenAnErroneousReportExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("aaa", true);
        doReturn(false).when(unsavedMessageDomainModel).exists("aaa");
        doReturn(Optional.absent()).when(reportDomainModel).findByExtId("bbb", true);
        doReturn(true).when(unsavedMessageDomainModel).exists("bbb");

        Boolean notUnique = service.doesAnySalesReportExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findByExtId("aaa", true);
        verify(unsavedMessageDomainModel).exists("aaa");
        verify(reportDomainModel).findByExtId("bbb", true);
        verify(unsavedMessageDomainModel).exists("bbb");
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);
        assertTrue(notUnique);
    }

    @Test
    public void doesReferencedReportExistWhenItExistsAsAReport() throws Exception {
        String id = "extId";

        doReturn(Optional.of(new Report())).when(reportDomainModel).findByExtId(id, false);

        boolean doesExist = service.doesReferencedReportExist(id);

        verify(reportDomainModel).findByExtId(id, false);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportExistWhenItExistsAsAnErroneousReport() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id, false);
        doReturn(Optional.absent()).when(queryDomainModel).findByExtId(id);
        doReturn(true).when(unsavedMessageDomainModel).exists(id);

        boolean doesExist = service.doesReferencedReportExist(id);

        verify(reportDomainModel).findByExtId(id, false);
        verify(queryDomainModel).findByExtId(id);
        verify(unsavedMessageDomainModel).exists(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportExistWhenItExistsAsAQuery() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id, false);
        doReturn(Optional.of(new SalesQueryType())).when(queryDomainModel).findByExtId(id);

        boolean doesExist = service.doesReferencedReportExist(id);

        verify(reportDomainModel).findByExtId(id, false);
        verify(queryDomainModel).findByExtId(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);

        assertTrue(doesExist);
    }

    @Test
    public void doesReferencedReportExistWhenItDoesNotExist() throws Exception {
        String id = "extId";

        doReturn(Optional.absent()).when(reportDomainModel).findByExtId(id, false);
        doReturn(Optional.absent()).when(queryDomainModel).findByExtId(id);
        doReturn(false).when(unsavedMessageDomainModel).exists(id);

        boolean doesExist = service.doesReferencedReportExist(id);

        verify(reportDomainModel).findByExtId(id, false);
        verify(queryDomainModel).findByExtId(id);
        verify(unsavedMessageDomainModel).exists(id);
        verifyNoMoreInteractions(documentDomainModel, queryDomainModel, reportDomainModel, responseDomainModel, unsavedMessageDomainModel);

        assertFalse(doesExist);
    }

}