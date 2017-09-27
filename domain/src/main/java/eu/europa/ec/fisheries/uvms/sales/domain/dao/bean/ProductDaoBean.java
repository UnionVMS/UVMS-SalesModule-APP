/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.ProductDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ProductDaoBean extends BaseDaoForSales<Product, Integer> implements ProductDao {

    @Override
    public List<Product> getProductsForFluxReport(FluxReport fluxReport) {
        TypedQuery<Product> productQuery = em.createNamedQuery(Product.FIND_PRODUCTS_BY_DOCUMENT, Product.class);
        productQuery.setParameter("document", fluxReport.getDocument());
        return productQuery.getResultList();
    }

}
