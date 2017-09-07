package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.ResponseDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.ResponseService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ResponseServiceBean implements ResponseService {

    @EJB
    private ResponseDomainModel responseDomainModel;

    @Override
    public void saveResponse(FLUXResponseDocumentType document) {
        responseDomainModel.create(document);
    }

    @Override
    public Optional<FLUXResponseDocumentType> findResponseByExtId(String extId) {
        return responseDomainModel.findByExtId(extId);
    }
}
