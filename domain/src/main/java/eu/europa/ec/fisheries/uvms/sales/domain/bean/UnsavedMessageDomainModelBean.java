package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.UnsavedMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.UnsavedMessageDao;

import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class UnsavedMessageDomainModelBean implements UnsavedMessageDomainModel {

    @EJB
    private UnsavedMessageDao dao;


    @Override
    public void save(String extId) {
        dao.save(extId);
    }

    @Override
    public boolean exists(String extId) {
        return dao.exists(extId);
    }
}
