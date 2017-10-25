/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.UnsavedMessageDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.UnsavedMessage;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class UnsavedMessageDaoBean extends BaseDaoForSales<UnsavedMessage, Long> implements UnsavedMessageDao {

    @Override
    public void save(String extId) {
        em.persist(new UnsavedMessage(extId));
    }

    @Override
    public boolean exists(String extId) {
        TypedQuery<UnsavedMessage> query = em.createNamedQuery(UnsavedMessage.FIND, UnsavedMessage.class);
        query.setParameter("extId", extId);

        return query.getResultList().size() > 0;
    }
}
