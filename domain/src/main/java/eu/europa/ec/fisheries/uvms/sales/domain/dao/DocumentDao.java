package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;

import javax.validation.constraints.NotNull;

/** DAO for all operations concerning a Sales Document . **/
public interface DocumentDao extends DaoForSales<Document, Integer> {

    /**
     * Find exactly one object by its id.
     * @param extId extId
     * @return the Document with matching extId, wrapped in an optional
     */
    Optional<Document> findByExtId(@NotNull String extId);

}
