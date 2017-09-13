/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.SalesConfigHelperDao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SalesConfigHelperDaoBean implements SalesConfigHelperDao {

    @PersistenceContext(unitName = "sales")
    protected EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
