package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.SavedSearchGroup;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesDatabaseException;
import eu.europa.ec.fisheries.uvms.sales.model.remote.SavedSearchGroupDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.SavedSearchService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.sales.service.dto.SavedSearchGroupDto;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class SavedSearchServiceBean implements SavedSearchService {

    @EJB(lookup = ServiceConstants.DB_ACCESS_SEARCH_GROUP_DOMAIN_MODEL)
    private SavedSearchGroupDomainModel savedSearchGroupDomainModel;

    @Inject
    private MapperFacade mapper;

    @Override
    public List<SavedSearchGroupDto> getSavedSearches(@NotNull String user) {
        List<SavedSearchGroup> savedSearchGroups = savedSearchGroupDomainModel.findByUser(user);

        List<SavedSearchGroupDto> savedSearchGroupDtos = mapper.mapAsList(savedSearchGroups, SavedSearchGroupDto.class);
        return savedSearchGroupDtos;
    }

    @Override
    public SavedSearchGroupDto saveSearch(@NotNull SavedSearchGroupDto searchGroupDto) {
        SavedSearchGroup savedSearchGroup = mapper.map(searchGroupDto, SavedSearchGroup.class);

        SavedSearchGroup created = savedSearchGroupDomainModel.create(savedSearchGroup);

        return mapper.map(created, SavedSearchGroupDto.class);
    }

    @Override
    public void deleteSearch(@NotNull Integer id) throws ServiceException {
        try {
            savedSearchGroupDomainModel.delete(id);
        } catch (SalesDatabaseException e) {
            throw new ServiceException("Exception when deleting a saved search group with id " + id, e);
        }
    }
}
