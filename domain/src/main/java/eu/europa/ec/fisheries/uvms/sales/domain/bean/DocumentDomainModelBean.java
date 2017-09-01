package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.DocumentDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

/**
 * Created by MATBUL on 31/08/2017.
 */

@Stateless
public class DocumentDomainModelBean implements DocumentDomainModel {

    @EJB
    private DocumentDao documentDao;

    @Inject
    @FLUX
    private MapperFacade mapper;

    @Override
    public Optional<SalesDocumentType> findByExtId(String extId) {
        Optional<Document> document = documentDao.findByExtId(extId);
        if (document.isPresent()) {
            return Optional.of(mapper.map(document.get(), SalesDocumentType.class));
        }

        return Optional.absent();
    }
}
