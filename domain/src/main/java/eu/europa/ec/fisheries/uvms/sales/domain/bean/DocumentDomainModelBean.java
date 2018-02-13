package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.DocumentDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.mapper.FLUX;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

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
    public List<SalesDocumentType> findByExtId(String extId) {
        List<Document> documents = documentDao.findByExtId(extId);
        return mapper.mapAsList(documents, SalesDocumentType.class);
    }
}
