package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.bean.ReportDomainModelBean;
import eu.europa.ec.fisheries.uvms.sales.service.UniqueIdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by MATBUL on 1/09/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UniqueIdServiceBeanTest {

    @InjectMocks
    UniqueIdServiceBean service;

    @Mock
    ReportDomainModel reportDomainModel;

    @Mock
    DocumentDomainModel documentDomainModel;

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
    public void doesAnyTakeOverDocumentExistWithAnyOfTheseIdsWhenNoTODExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findTakeOverDocumentByExtId("aaa");
        doReturn(Optional.absent()).when(reportDomainModel).findTakeOverDocumentByExtId("bbb");
        doReturn(Optional.absent()).when(reportDomainModel).findTakeOverDocumentByExtId("ccc");

        Boolean notUnique = service.doesAnyTakeOverDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findTakeOverDocumentByExtId("aaa");
        verify(reportDomainModel).findTakeOverDocumentByExtId("bbb");
        verify(reportDomainModel).findTakeOverDocumentByExtId("ccc");
        verifyNoMoreInteractions(reportDomainModel);
        assertFalse(notUnique);
    }

    @Test
    public void doesAnyTakeOverDocumentExistWithAnyOfTheseIdsWhenATODExistsWithGivenIds() throws Exception {
        doReturn(Optional.absent()).when(reportDomainModel).findTakeOverDocumentByExtId("aaa");
        doReturn(Optional.of(new Report())).when(reportDomainModel).findTakeOverDocumentByExtId("bbb");

        Boolean notUnique = service.doesAnyTakeOverDocumentExistWithAnyOfTheseIds(Arrays.asList("aaa", "bbb", "ccc"));

        verify(reportDomainModel).findTakeOverDocumentByExtId("aaa");
        verify(reportDomainModel).findTakeOverDocumentByExtId("bbb");
        verifyNoMoreInteractions(reportDomainModel);
        assertTrue(notUnique);
    }

}