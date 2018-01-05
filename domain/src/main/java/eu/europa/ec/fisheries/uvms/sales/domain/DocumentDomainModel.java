package eu.europa.ec.fisheries.uvms.sales.domain;

import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;

import java.util.List;

public interface DocumentDomainModel {

    /**
     * Find all document with the extId.
     * It's possible that there are multiple documents with the same ext id, in case of corrections of reports.
     * @param extId extId
     * @return a list with 0, 1, or multiple documents
     */
    List<SalesDocumentType> findByExtId(String extId);
}
