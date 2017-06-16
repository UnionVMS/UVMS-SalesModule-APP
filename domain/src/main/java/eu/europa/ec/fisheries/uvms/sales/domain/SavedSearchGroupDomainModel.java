package eu.europa.ec.fisheries.uvms.sales.domain;

import eu.europa.ec.fisheries.schema.sales.SavedSearchGroup;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesDatabaseException;

import java.util.List;

public interface SavedSearchGroupDomainModel {
    List<SavedSearchGroup> findByUser(String user);
    SavedSearchGroup create(SavedSearchGroup savedSearchGroup);
    void delete(SavedSearchGroup savedSearchGroup);
    void delete(Integer id) throws SalesDatabaseException;
}
