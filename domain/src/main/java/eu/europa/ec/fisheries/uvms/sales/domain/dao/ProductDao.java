package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;

import javax.validation.constraints.NotNull;
import java.util.List;

/** DAO for all operations concerning a product. **/
public interface ProductDao extends DaoForSales<Product, Integer> {

    /**
     * Retrieves all documents belonging to a flux report
     * @param fluxReport non-null flux report
     * @return all documents belonging to a flux report. If nothing, found, an empty list.
     */
    List<Product> getProductsForFluxReport(@NotNull FluxReport fluxReport);

}
