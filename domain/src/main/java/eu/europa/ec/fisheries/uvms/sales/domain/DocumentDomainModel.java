package eu.europa.ec.fisheries.uvms.sales.domain;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;

/**
 * Created by MATBUL on 31/08/2017.
 */
public interface DocumentDomainModel {
    Optional<SalesDocumentType> findByExtId(String extId);
}
