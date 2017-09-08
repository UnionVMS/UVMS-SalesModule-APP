package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.ResponseDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.ResponseService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isBlank;

@Stateless
public class ResponseServiceBean implements ResponseService {

    @EJB
    private ResponseDomainModel responseDomainModel;

    @Override
    public void saveResponse(FLUXResponseDocumentType document) {
        checkNotNull(document);
        responseDomainModel.create(document);
    }

    @Override
    public Optional<FLUXResponseDocumentType> findResponseByExtId(String extId) {
        Preconditions.checkArgument(!isBlank(extId));
        return responseDomainModel.findByExtId(extId);
    }
}
