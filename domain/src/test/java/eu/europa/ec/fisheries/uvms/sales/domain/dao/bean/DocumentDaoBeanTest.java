package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by MATBUL on 1/09/2017.
 */
public class DocumentDaoBeanTest extends AbstractDaoTest<DocumentDaoBean> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenExactlyOneDocumentWasFound() throws Exception {
        List<Document> document = dao.findByExtId("DocumentExternalID4");

        assertEquals(1, document.size());
        assertEquals("DocumentExternalID4", document.get(0).getExtId());
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenMoreThanOneDocumentWasFound() throws Exception {
        List<Document> document = dao.findByExtId("DocumentExternalID5");

        assertEquals(2, document.size());
        assertEquals("DocumentExternalID5", document.get(0).getExtId());
        assertEquals("DocumentExternalID5", document.get(1).getExtId());
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenNoDocumentWasFound() throws Exception {
        List<Document> document = dao.findByExtId("This one definitely doesn't exist");

        assertTrue(document.isEmpty());
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenAllUppercase() throws Exception {
        List<Document> document = dao.findByExtId("DOCUMENTEXTERNALID4");

        assertFalse(document.isEmpty());
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenIdHasUppercase() throws Exception {
        List<Document> document = dao.findByExtId("DocumentExternalID4");

        assertFalse(document.isEmpty());
    }

    @Test
    @DataSet(initialData = "data/documentDaoBeanTest-testMapping-initial.xml")
    public void findByExtIdWhenAllLowercase() throws Exception {
        List<Document> document = dao.findByExtId("documentexternalid4");

        assertFalse(document.isEmpty());
    }

}