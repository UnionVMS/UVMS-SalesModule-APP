package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.ErroneousMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.ErroneousMessageDao;

import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class ErroneousMessageDomainModelBean implements ErroneousMessageDomainModel {

    @EJB
    private ErroneousMessageDao dao;


    @Override
    public void save(String extId) {
        dao.save(extId);
    }

    @Override
    public boolean exists(String extId) {
        return dao.exists(extId);
    }
}
