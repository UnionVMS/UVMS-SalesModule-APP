package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.ResponseDomainModel;
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
public class ResponseServiceBeanTest {

    @InjectMocks
    ResponseServiceBean service;

    @Mock
    ResponseDomainModel responseDomainModel;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void saveResponse() throws Exception {
        FLUXResponseDocumentType document = new FLUXResponseDocumentType();

        service.saveResponse(document);

        verify(responseDomainModel).create(document);
        verifyNoMoreInteractions(responseDomainModel);
    }

    @Test
    public void saveResponseWhenInputIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        service.saveResponse(null);
        verifyNoMoreInteractions(responseDomainModel);
    }

    @Test
    public void findResponseByExtId() throws Exception {
        service.findResponseByExtId("extId");

        verify(responseDomainModel).findByExtId("extId");
        verifyNoMoreInteractions(responseDomainModel);
    }

    @Test
    public void findResponseByExtIdWhenInputIsNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        service.findResponseByExtId(null);
        verifyNoMoreInteractions(responseDomainModel);
    }

    @Test
    public void findResponseByExtIdWhenInputIsBlank() throws Exception {
        exception.expect(IllegalArgumentException.class);
        service.findResponseByExtId("");
        verifyNoMoreInteractions(responseDomainModel);
    }

}