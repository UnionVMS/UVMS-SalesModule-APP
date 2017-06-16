package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface SavedSearchGroupDao extends DaoForSales<SavedSearchGroup, Integer> {

    /** Finds all saved searches by the username of a user
     * @param user username
     * @return list of {@link SavedSearchGroup}. When nothing found, an empty list is returned. **/
    List<SavedSearchGroup> findByUser(@NotNull String user);

    /**
     * Persists the given saved search group to the database.
     * If it does not exist yet, the saved search group is created.
     * When it already exists, the saved search group is updated in the database.
     * @param savedSearchGroup saved search group to persist
     * @return persisted saved search group
     */
    SavedSearchGroup createOrUpdate(SavedSearchGroup savedSearchGroup);

}
