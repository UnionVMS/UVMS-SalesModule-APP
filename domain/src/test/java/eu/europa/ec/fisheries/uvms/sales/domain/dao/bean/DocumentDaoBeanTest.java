package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by MATBUL on 1/09/2017.
 */
public class DocumentDaoBeanTest extends AbstractDaoTest<DocumentDaoBean> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenExactlyOneDocumentWasFound() throws Exception {
        Optional<Document> document = dao.findByExtId("DocumentExternalID4");

        assertTrue(document.isPresent());
        assertEquals("DocumentExternalID4", document.get().getExtId());
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenMoreThanOneDocumentWasFound() throws Exception {
        String ID = "DocumentExternalID5";

        exception.expect(SalesNonBlockingException.class);
        exception.expectMessage("More than one result found for 'findByExtId' on entity Document in table 'sales_document', id: " + ID);

        dao.findByExtId(ID);
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenNoDocumentWasFound() throws Exception {
        Optional<Document> document = dao.findByExtId("This one definitely doesn't exist");

        assertFalse(document.isPresent());
    }

}