package eu.europa.ec.fisheries.uvms.sales.domain;

import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;

import java.util.Optional;

public interface ResponseDomainModel {

    /**
     * Saves a response to database
     *
     * @param response to be saved
     * @return the created response
     */
    FLUXResponseDocumentType create(FLUXResponseDocumentType response);

    /**
     * Queries a response by extId
     * @param extId of the response
     */
    Optional<FLUXResponseDocumentType> findByExtId(String extId);


}
