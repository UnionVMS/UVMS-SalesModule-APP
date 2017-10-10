/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.ErroneousMessageDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.ErroneousMessage;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class ErroneousMessageDaoBean extends BaseDaoForSales<ErroneousMessage, Long> implements ErroneousMessageDao {

    @Override
    public void save(String extId) {
        em.persist(new ErroneousMessage(extId));
    }

    @Override
    public boolean exists(String extId) {
        TypedQuery<ErroneousMessage> query = em.createNamedQuery(ErroneousMessage.FIND, ErroneousMessage.class);
        query.setParameter("extId", extId);

        return query.getResultList().size() > 0;
    }
}
