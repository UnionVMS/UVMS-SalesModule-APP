package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.SavedSearchGroupDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup;
import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SavedSearchGroupDomainModelBeanTest {

    @Mock
    private SavedSearchGroupDao savedSearchGroupDao;

    @Mock
    private MapperFacade mapper;

    @InjectMocks
    private SavedSearchGroupDomainModelBean modelBean;

    @Test(expected = IllegalArgumentException.class)
    public void testFindByUserWhenUsernameIsNull() throws Exception {
        modelBean.findByUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByUserWhenUsernameIsEmpty() throws Exception {
        modelBean.findByUser("");
    }

    @Test
    public void testFindByUserWhenSuccess() throws Exception {
        String username = "stainii";
        List<SavedSearchGroup> savedSearchGroupEntities = new ArrayList<>();
        List<eu.europa.ec.fisheries.schema.sales.SavedSearchGroup> expected = new ArrayList<>();

        when(savedSearchGroupDao.findByUser(username)).thenReturn(savedSearchGroupEntities);
        when(mapper.mapAsList(savedSearchGroupEntities, eu.europa.ec.fisheries.schema.sales.SavedSearchGroup.class)).thenReturn(expected);

        List<eu.europa.ec.fisheries.schema.sales.SavedSearchGroup> actual = modelBean.findByUser(username);

        verify(savedSearchGroupDao).findByUser(username);
        verifyNoMoreInteractions(savedSearchGroupDao);

        assertSame(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWhenArgumentIsNull() {
        modelBean.create(null);
    }

    @Test
    public void testCreateWhenSuccess() {
        eu.europa.ec.fisheries.schema.sales.SavedSearchGroup searchGroupDomain = new eu.europa.ec.fisheries.schema.sales.SavedSearchGroup();
        SavedSearchGroup searchGroupEntity = new SavedSearchGroup();

        when(mapper.map(searchGroupDomain, SavedSearchGroup.class)).thenReturn(searchGroupEntity);
        when(savedSearchGroupDao.createOrUpdate(searchGroupEntity)).thenReturn(searchGroupEntity);
        when(mapper.map(searchGroupEntity, eu.europa.ec.fisheries.schema.sales.SavedSearchGroup.class)).thenReturn(searchGroupDomain);

        eu.europa.ec.fisheries.schema.sales.SavedSearchGroup result = modelBean.create(searchGroupDomain);

        verify(mapper).map(searchGroupDomain, SavedSearchGroup.class);
        verify(savedSearchGroupDao).createOrUpdate(searchGroupEntity);
        verify(mapper).map(searchGroupEntity, eu.europa.ec.fisheries.schema.sales.SavedSearchGroup.class);
        verifyNoMoreInteractions(mapper, savedSearchGroupDao);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteWhenArgumentIsNull() {
        modelBean.delete((eu.europa.ec.fisheries.schema.sales.SavedSearchGroup)null);
    }

    @Test
    public void testDeleteWhenSuccess() {
        eu.europa.ec.fisheries.schema.sales.SavedSearchGroup searchGroupDomain = new eu.europa.ec.fisheries.schema.sales.SavedSearchGroup();
        SavedSearchGroup searchGroupEntity = new SavedSearchGroup();

        when(mapper.map(searchGroupDomain, SavedSearchGroup.class)).thenReturn(searchGroupEntity);

        modelBean.delete(searchGroupDomain);

        verify(mapper).map(searchGroupDomain, SavedSearchGroup.class);
        verify(savedSearchGroupDao).delete(searchGroupEntity);
        verifyNoMoreInteractions(mapper, savedSearchGroupDao);
    }
    @Test
    public void testDeleteWithIdWhenSuccess() throws Exception {
        Integer id = 5;

        SavedSearchGroup searchGroupEntity = new SavedSearchGroup().id(5);

        when(savedSearchGroupDao.findByIdOrNull(5)).thenReturn(searchGroupEntity);

        modelBean.delete(id);

        verify(savedSearchGroupDao).findByIdOrNull(5);
        verify(savedSearchGroupDao).delete(searchGroupEntity);
        verifyNoMoreInteractions(mapper, savedSearchGroupDao);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteWithIdWhenArgumentIsNull() throws Exception {
        modelBean.delete((Integer) null);
    }

}