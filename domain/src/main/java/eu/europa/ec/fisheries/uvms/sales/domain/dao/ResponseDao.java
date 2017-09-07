package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Response;

/** DAO for all operations concerning a response. **/
public interface ResponseDao extends DaoForSales<Response, Integer> {
    Optional<Response> findByExtId(String extId);
    Optional<Response> findByReferencedId(String extId);
}
