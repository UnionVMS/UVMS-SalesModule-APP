package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ErroneousMessageDaoBeanTest extends AbstractDaoTest<ErroneousMessageDaoBean> {

    @Test
    @DataSet(expectedData = "data/ErroneousMessageDaoBeanTest-save-expected.xml")
    public void save() throws Exception {
        dao.save("test");
    }

    @Test
    @DataSet(initialData = "data/ErroneousMessageDaoBeanTest-exists-initial.xml")
    public void existsWhenTrue() throws Exception {
        assertTrue(dao.exists("test"));
    }

    @Test
    @DataSet(initialData = "data/ErroneousMessageDaoBeanTest-exists-initial.xml")
    public void existsWhenFalse() throws Exception {
        assertFalse(dao.exists("bla"));
    }

}