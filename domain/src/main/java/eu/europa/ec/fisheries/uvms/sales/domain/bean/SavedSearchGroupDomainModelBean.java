package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.SavedSearchGroup;
import eu.europa.ec.fisheries.uvms.sales.domain.SavedSearchGroupDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.SavedSearchGroupDao;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesDatabaseException;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Stateless
public class SavedSearchGroupDomainModelBean implements SavedSearchGroupDomainModel {

    @EJB
    private SavedSearchGroupDao savedSearchGroupDao;

    @Inject @FLUX
    private MapperFacade mapper;

    public List<SavedSearchGroup> findByUser(String user) {
        checkArgument(!Strings.isNullOrEmpty(user));
        return mapper.mapAsList(savedSearchGroupDao.findByUser(user), SavedSearchGroup.class);
    }

    public SavedSearchGroup create(SavedSearchGroup savedSearchGroup) {
        checkNotNull(savedSearchGroup);
        eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup searchGroupEntity = mapper.map(savedSearchGroup, eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup.class);
        return mapper.map(savedSearchGroupDao.createOrUpdate(searchGroupEntity), SavedSearchGroup.class);
    }

    public void delete(SavedSearchGroup savedSearchGroup) {
        checkNotNull(savedSearchGroup);
        savedSearchGroupDao.delete(mapper.map(savedSearchGroup, eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup.class));
    }
    public void delete(Integer id) {
        checkNotNull(id);

        eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup savedSearchGroup = savedSearchGroupDao.findByIdOrNull(id);

        if (savedSearchGroup == null) {
            throw new SalesDatabaseException("Id " + id + " not found");
        }

        savedSearchGroupDao.delete(savedSearchGroup);

    }

}
