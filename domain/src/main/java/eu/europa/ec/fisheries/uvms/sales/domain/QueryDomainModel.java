package eu.europa.ec.fisheries.uvms.sales.domain;

import eu.europa.ec.fisheries.schema.sales.SalesQueryType;

import java.util.Optional;


public interface QueryDomainModel {

    /**
     * Saves a query to database
     *
     * @param query to be saved
     * @return the created query
     */
    SalesQueryType create(SalesQueryType query);

    /**
     * Queries a query by extId
     * @param extId of the query
     */
    Optional<SalesQueryType> findByExtId(String extId);
}
