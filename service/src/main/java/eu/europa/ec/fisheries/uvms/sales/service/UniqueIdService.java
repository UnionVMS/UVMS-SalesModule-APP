package eu.europa.ec.fisheries.uvms.sales.service;

import java.util.List;

public interface UniqueIdService {
    boolean doesAnySalesDocumentExistWithAnyOfTheseIds(List<String> extId);

    boolean doesAnySalesReportExistWithAnyOfTheseIds(List<String> extId);

    boolean isQueryIdUnique(String extId);

    boolean isResponseIdUnique(String extId);

    boolean doesReferencedReportInResponseExist(String referencedId);
}
