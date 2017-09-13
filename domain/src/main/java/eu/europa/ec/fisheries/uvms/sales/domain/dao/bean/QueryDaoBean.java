/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.QueryDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Query;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class QueryDaoBean extends BaseDaoForSales<Query, Integer> implements QueryDao {


    @Override
    public Optional<Query> findByExtId(String extId) {
        TypedQuery<Query> query = em.createNamedQuery(Query.FIND_BY_EXT_ID, Query.class);
        query.setParameter("extId", extId);

        List<Query> resultList = query.getResultList();

        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }

        if (resultList.size() > 1) {
            throw new SalesNonBlockingException("More than one result found for 'findByExtId' on entity Query in table 'sales_query', " +
                    "id: " + extId);
        }

        return Optional.absent();
    }
}
