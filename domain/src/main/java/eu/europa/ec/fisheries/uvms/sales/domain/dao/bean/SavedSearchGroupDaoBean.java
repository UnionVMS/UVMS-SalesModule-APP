/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.SavedSearchGroupDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class SavedSearchGroupDaoBean extends BaseDaoForSales<SavedSearchGroup, Integer> implements SavedSearchGroupDao {

    final static Logger LOG = LoggerFactory.getLogger(SavedSearchGroupDaoBean.class);

    @Override
    public SavedSearchGroup createOrUpdate(SavedSearchGroup savedSearchGroup) {
        em.merge(savedSearchGroup);
        return savedSearchGroup;
    }

    @Override
    public List<SavedSearchGroup> findByUser(String user) throws NoResultException {
        TypedQuery<SavedSearchGroup> savedSearchGroupQuery = em.createNamedQuery(SavedSearchGroup.FIND_BY_USER, SavedSearchGroup.class);
        savedSearchGroupQuery.setParameter("user", user);

        return savedSearchGroupQuery.getResultList();
    }


}
