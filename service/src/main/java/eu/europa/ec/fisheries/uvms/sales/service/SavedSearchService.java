package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.service.dto.SavedSearchGroupDto;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.util.List;

@Local
public interface SavedSearchService {

    List<SavedSearchGroupDto> getSavedSearches(@NotNull String user);

    SavedSearchGroupDto saveSearch(@NotNull SavedSearchGroupDto searchGroupDto);

    void deleteSearch(@NotNull Integer id);
}
