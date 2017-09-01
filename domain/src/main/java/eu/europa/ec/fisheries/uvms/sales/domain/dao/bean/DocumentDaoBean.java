package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.DocumentDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by MATBUL on 31/08/2017.
 */
@Stateless
public class DocumentDaoBean extends BaseDaoForSales<Document, Integer> implements DocumentDao {


    @Override
    public Optional<Document> findByExtId(String extId) throws NoResultException {
        TypedQuery<Document> query = em.createNamedQuery(Document.FIND_BY_EXT_ID, Document.class);
        query.setParameter("extId", extId);

        List<Document> resultList = query.getResultList();

        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }

        if (resultList.size() > 1) {
            throw new SalesNonBlockingException("More than one result found for 'findByExtId' on entity Document in table 'sales_document', " +
                    "this should not happen");
        }

        return Optional.absent();
    }
}
