package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.schema.sales.SalesQueryType;
import eu.europa.ec.fisheries.uvms.sales.domain.QueryDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.QueryDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Query;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Stateless
public class QueryDomainModelBean implements QueryDomainModel {

    @EJB
    private QueryDao dao;

    @Inject
    @FLUX
    private MapperFacade mapper;


    @Override
    public SalesQueryType create(SalesQueryType query) {
        checkNotNull(query, "query cannot be null in QueryDomainModelBean::create");

        Query domainQuery = mapper.map(query, Query.class);
        return mapper.map(dao.create(domainQuery), SalesQueryType.class);
    }

    @Override
    public Optional<SalesQueryType> findByExtId(String extId) {
        if (isBlank(extId)) {
            throw new NullPointerException("extId cannot be null in QueryDomainModelBean::findByExtId");
        }

        Optional<Query> optionalQuery = dao.findByExtId(extId);

        if (optionalQuery.isPresent()) {
            return Optional.of(mapper.map(optionalQuery.get(), SalesQueryType.class));
        }

        return Optional.empty();
    }
}
