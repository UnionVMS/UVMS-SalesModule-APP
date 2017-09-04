/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.ProductDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.FluxReportQueryToTypedQueryHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class FluxReportDaoBean extends BaseDaoForSales<FluxReport, Integer> implements FluxReportDao {

    final static Logger LOG = LoggerFactory.getLogger(FluxReportDaoBean.class);

    @EJB
    protected ProductDao productDao;

    @Override
    public FluxReport findByExtIdOrNull(String extId) {
        try {
            return findByExtId(extId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public FluxReport findByExtId(String extId) throws NoResultException {
        TypedQuery<FluxReport> query = em.createNamedQuery(FluxReport.FIND_BY_EXT_ID, FluxReport.class);
        query.setParameter("extId", extId);

        return query.getSingleResult();
    }

    @Override
    public List<FluxReport> search(ReportQuery reportQuery) {
        return FluxReportQueryToTypedQueryHelper
                                    .search(em)
                                    .filter(reportQuery.getFilters())
                                    .sort(reportQuery.getSorting())
                                    .page(reportQuery.getPaging())
                                    .build()
                                    .getResultList();
    }

    @Override
    public long count(ReportQuery reportQuery) {
        return (long) FluxReportQueryToTypedQueryHelper
                                        .count(em)
                                        .filter(reportQuery.getFilters())
                                        .build()
                                        .getSingleResult();
    }

    @Override
    public FluxReport findDetailsByExtId(String extId) {
        FluxReport fluxReport = findByExtId(extId);

        List<Product> products = productDao.getProductsForFluxReport(fluxReport);
        fluxReport.getDocument().setProducts(products);

        return fluxReport;
    }

    @Override
    public Optional<FluxReport> findCorrectionOrDeletionOf(@NotNull String extId) {
        TypedQuery<FluxReport> query = em.createNamedQuery(FluxReport.FIND_BY_REFERRED_ID, FluxReport.class);
        query.setParameter("extId", extId);

        List<FluxReport> results = query.getResultList();
        if (results.size() == 1) {
            return Optional.of(results.get(0));
        } else if (results.isEmpty()) {
            return Optional.absent();
        } else {
            throw new NonUniqueResultException("Found more than 1 correction or deletion of report with extId " + extId);
        }
    }

    @Override
    public FluxReport findLatestVersion(FluxReport fluxReport) {
        Optional<FluxReport> newerVersion = findCorrectionOrDeletionOf(fluxReport.getExtId());
        if (newerVersion.isPresent()) {
            return findLatestVersion(newerVersion.get());
        } else {
            return fluxReport;
        }
    }

    @Override
    public Optional<FluxReport> findTakeOverDocumentByExtId(String extId) {
        TypedQuery<FluxReport> query = em.createNamedQuery(FluxReport.FIND_BY_EXT_ID, FluxReport.class);
        query.setParameter("extId", extId);

        List<FluxReport> resultList = query.getResultList();

        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }

        if (resultList.size() > 1) {
            throw new SalesNonBlockingException("More than one result found for 'findByExtId' on entity FluxReport in table 'sales_flux_report', " +
                    "id: " + extId);
        }

        return Optional.absent();
    }

}
