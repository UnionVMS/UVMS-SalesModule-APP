package eu.europa.ec.fisheries.uvms.sales.service;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;

import javax.ejb.Local;

@Local
public interface ResponseService {

    void saveResponse(FLUXResponseDocumentType query);

    Optional<FLUXResponseDocumentType> findResponseByExtId(String extId);
}
