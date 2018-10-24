package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;

import javax.ejb.Local;
import java.util.Optional;

@Local
public interface ResponseService {

    void saveResponse(FLUXResponseDocumentType query);

    Optional<FLUXResponseDocumentType> findResponseByExtId(String extId);
}
