package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.AbstractDaoTest;
import eu.europa.ec.fisheries.uvms.sales.domain.framework.DataSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SavedSearchGroupDaoBeanTest extends AbstractDaoTest<SavedSearchGroupDaoBean> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @DataSet(initialData = "data/SavedSearchGroupDaoBeanTest-findByUser-initial.xml")
    public void testFindByUserWhenSomethingFound() throws Exception {
        List<SavedSearchGroup> savedSearchGroups = dao.findByUser("POTUS");

        assertEquals(1, savedSearchGroups.size());
        assertEquals("Barack Obama", savedSearchGroups.get(0).getName());
        assertEquals("rules", savedSearchGroups.get(0).getFields().get("Obama"));
    }

    @Test
    @DataSet(initialData = "data/SavedSearchGroupDaoBeanTest-findByUser-initial.xml")
    public void testFindByUserWhenNothingFound() throws Exception {
        assertTrue(dao.findByUser("this_isn't_the_user_you're_looking_for").isEmpty());
    }

    @Test
    @DataSet(expectedData = "data/SavedSearchGroupDaoBeanTest-createOrUpdate-expected.xml")
    public void testCreateOrUpdateWhenItDoesNotExistYet() {
        Map<String, String> fields = new HashMap<>();
        fields.put("Hey there", "Delilah");
        fields.put("What's it like in", "New York city?");
        SavedSearchGroup savedSearchGroup = new SavedSearchGroup()
                .name("James Bond")
                .user("mr-lonely")
                .fields(fields);

        dao.createOrUpdate(savedSearchGroup);
    }

    @Test
    @DataSet(initialData = "data/SavedSearchGroupDaoBeanTest-createOrUpdate-initial.xml",
            expectedData = "data/SavedSearchGroupDaoBeanTest-createOrUpdate-expected.xml")
    public void testCreateOrUpdateWhenItExistsAlready() {
        Map<String, String> fields = new HashMap<>();
        fields.put("Hey there", "Delilah");
        fields.put("What's it like in", "New York city?");
        SavedSearchGroup savedSearchGroup = new SavedSearchGroup()
                .id(1)
                .name("James Bond")
                .user("mr-lonely")
                .fields(fields);

        dao.createOrUpdate(savedSearchGroup);
    }
}