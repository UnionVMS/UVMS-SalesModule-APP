package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.ResponseDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.ResponseDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Response;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Stateless
public class ResponseDomainModelBean implements ResponseDomainModel {

    @EJB
    private ResponseDao dao;

    @Inject
    @FLUX
    private MapperFacade mapper;

    @Override
    public FLUXResponseDocumentType create(FLUXResponseDocumentType response) {
        checkNotNull(response, "response cannot be null in ResponseDomainModelBean::create");

        Response domainQuery = mapper.map(response, Response.class);
        return mapper.map(dao.create(domainQuery), FLUXResponseDocumentType.class);
    }

    @Override
    public Optional<FLUXResponseDocumentType> findByExtId(String extId) {
        if (isBlank(extId)) {
            throw new NullPointerException("extId cannot be null or blank in ResponseDomainModelBean::findByExtId");
        }

        Optional<Response> optionalQuery = dao.findByExtId(extId);

        if (optionalQuery.isPresent()) {
            return Optional.of(mapper.map(optionalQuery.get(), FLUXResponseDocumentType.class));
        }

        return Optional.absent();
    }
}
