package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.SalesQueryType;

import javax.ejb.Local;
@Local
public interface QueryService {
    void saveQuery(SalesQueryType query);
}
