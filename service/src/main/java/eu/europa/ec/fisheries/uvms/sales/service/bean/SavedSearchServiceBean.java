package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.SavedSearchGroup;
import eu.europa.ec.fisheries.uvms.sales.domain.SavedSearchGroupDomainModel;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesDatabaseException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.SavedSearchService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.SavedSearchGroupDto;
import eu.europa.ec.fisheries.uvms.sales.service.mapper.DTO;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class SavedSearchServiceBean implements SavedSearchService {

    @EJB
    private SavedSearchGroupDomainModel savedSearchGroupDomainModel;

    @Inject @DTO
    private MapperFacade mapper;

    @Override
    public List<SavedSearchGroupDto> getSavedSearches(@NotNull String user) {
        List<SavedSearchGroup> savedSearchGroups = savedSearchGroupDomainModel.findByUser(user);

        return mapper.mapAsList(savedSearchGroups, SavedSearchGroupDto.class);
    }

    @Override
    public SavedSearchGroupDto saveSearch(@NotNull SavedSearchGroupDto searchGroupDto) {
        SavedSearchGroup savedSearchGroup = mapper.map(searchGroupDto, SavedSearchGroup.class);

        SavedSearchGroup created = savedSearchGroupDomainModel.create(savedSearchGroup);

        return mapper.map(created, SavedSearchGroupDto.class);
    }

    @Override
    public void deleteSearch(@NotNull Integer id) {
        try {
            savedSearchGroupDomainModel.delete(id);
        } catch (SalesDatabaseException e) {
            throw new SalesServiceException("Exception when deleting a saved search group with id " + id, e);
        }
    }
}
