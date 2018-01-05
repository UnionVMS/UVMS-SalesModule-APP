package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.DocumentDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;

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
    public List<Document> findByExtId(String extId) throws NoResultException {
        TypedQuery<Document> query = em.createNamedQuery(Document.FIND_BY_EXT_ID, Document.class);
        query.setParameter("extId", extId);
        return query.getResultList();
    }
}
