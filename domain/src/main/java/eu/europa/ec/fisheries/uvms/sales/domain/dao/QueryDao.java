package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.Query;

import java.util.Optional;

/** DAO for all operations concerning a query. **/
public interface QueryDao extends DaoForSales<Query, Integer> {
    Optional<Query> findByExtId(String extId);
}
