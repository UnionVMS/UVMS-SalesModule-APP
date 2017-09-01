package eu.europa.ec.fisheries.uvms.sales.service;

import java.util.List;

/**
 * Created by MATBUL on 31/08/2017.
 */
public interface UniqueIdService {
    Boolean doesAnySalesDocumentExistWithAnyOfTheseIds(List<String> extId);

    Boolean doesAnyTakeOverDocumentExistWithAnyOfTheseIds(List<String> extId);
}
