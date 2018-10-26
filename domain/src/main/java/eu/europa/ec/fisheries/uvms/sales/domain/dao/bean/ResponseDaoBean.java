/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.ResponseDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Response;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class ResponseDaoBean extends BaseDaoForSales<Response, Integer> implements ResponseDao {

    @Override
    public Optional<Response> findByExtId(String extId) {
        TypedQuery<Response> query = em.createNamedQuery(Response.FIND_BY_EXT_ID, Response.class);
        query.setParameter("extId", extId);

        List<Response> resultList = query.getResultList();

        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }

        if (resultList.size() > 1) {
            throw new SalesNonBlockingException("More than one result found for 'findByExtId' on entity Response in table 'sales_response', " +
                    "id: " + extId);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Response> findByReferencedId(String referencedId) {
        TypedQuery<Response> query = em.createNamedQuery(Response.FIND_BY_REFERENCED_ID, Response.class);
        query.setParameter("referencedId", referencedId);

        List<Response> resultList = query.getResultList();

        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }

        if (resultList.size() > 1) {
            throw new SalesNonBlockingException("More than one result found for 'findByReferencedId' on entity Response in table 'sales_response', " +
                    "id: " + referencedId);
        }

        return Optional.empty();
    }
}
