package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

/** DAO for all operations concerning a Sales Document . **/
public interface DocumentDao extends DaoForSales<Document, Integer> {

    /**
     * Find all document with the extId.
     * It's possible that there are multiple documents with the same ext id, in case of corrections of reports.
     * @param extId extId
     * @return a list with 0, 1, or multiple documents
     */
    List<Document> findByExtId(@NotNull String extId);

}
