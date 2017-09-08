package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Query;

/** DAO for all operations concerning a query. **/
public interface QueryDao extends DaoForSales<Query, Integer> {
    Optional<Query> findByExtId(String extId);
}
