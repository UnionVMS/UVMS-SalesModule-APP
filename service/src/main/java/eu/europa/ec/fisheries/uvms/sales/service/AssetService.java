package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import javax.ejb.Local;
import java.util.List;

/**
 * With this service, you can retrieve data from the Asset module.
 */
@Local
public interface AssetService {

    /**
     * Retrieves an asset by its extId (GUID).
     *
     * @param extId the guid, in the sales internals referred to as extId.
     * @return the found asset.
     * @throws SalesServiceException, when something goes wrong, or the asset is not found.
     */
    Asset findByCFR(String extId);

    /**
     * Searches assets by their name, CFR of IRCS.
     *
     * @param searchString an asset name, CFR or IRCS
     * @return a list of the found assets. This list is empty when nothing is found.
     * @throws SalesServiceException, when something goes wrong.
     */
    List<Asset> findByNameOrCFROrIRCS(String searchString);

    /**
     * Searches assets by their name, CFR of IRCS. Only their GUIDs, in Sales called "extIds", are returned.
     *
     * @param searchString an asset name, CFR or IRCS
     * @return a list containing the GUIDs, in Sales called "extIds", of the found assets. This list is empty when
     * nothing is found.
     * @throws SalesServiceException, when something goes wrong.
     */
    List<String> findExtIdsByNameOrCFROrIRCS(String searchString);
}