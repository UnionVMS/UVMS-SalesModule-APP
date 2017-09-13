package eu.europa.ec.fisheries.uvms.sales.domain;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.SalesQueryType;


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
