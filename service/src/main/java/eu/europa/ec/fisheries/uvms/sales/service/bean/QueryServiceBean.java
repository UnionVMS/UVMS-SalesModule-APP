package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.SalesQueryType;
import eu.europa.ec.fisheries.uvms.sales.domain.QueryDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.QueryService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class QueryServiceBean implements QueryService {

    @EJB
    QueryDomainModel queryDomainModel;

    @Override
    public void saveQuery(SalesQueryType query) {
        queryDomainModel.create(query);
    }
}
