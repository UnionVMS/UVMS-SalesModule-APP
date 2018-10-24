/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.ProductDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.FluxReportQueryToTypedQueryHelper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Stateless
@Slf4j
public class FluxReportDaoBean extends BaseDaoForSales<FluxReport, Integer> implements FluxReportDao {
    private static final String EXT_ID = "extId";
    private static final String PURPOSE = "purpose";

    @EJB
    protected ProductDao productDao;

    @Override
    public Optional<FluxReport> findByExtId(String extId) {
        TypedQuery<FluxReport> query = em.createNamedQuery(FluxReport.FIND_BY_EXT_ID, FluxReport.class);
        query.setParameter(EXT_ID, extId);

        List<FluxReport> resultList = query.getResultList();

        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }

        if (resultList.size() > 1) {
            throw new SalesNonBlockingException("More than one result found for 'findByExtId' on entity FluxReport in table 'sales_flux_report', " +
                    "id: " + extId);
        }

        return Optional.empty();
    }

    @Override
    public List<FluxReport> search(ReportQuery reportQuery) {
        return search(reportQuery, false);
    }

    @Override
    public List<FluxReport> search(ReportQuery reportQuery, boolean eagerLoadRelations) {
        return FluxReportQueryToTypedQueryHelper
                                    .search(em, reportQuery.getFilters())
                                    .sort(reportQuery.getSorting())
                                    .page(reportQuery.getPaging())
                                    .eagerLoadRelations(eagerLoadRelations)
                                    .build()
                                    .getResultList();
    }

    @Override
    public long count(ReportQuery reportQuery) {
        return (long) FluxReportQueryToTypedQueryHelper
                                        .count(em, reportQuery.getFilters())
                                        .build()
                                        .getSingleResult();
    }

    @Override
    public FluxReport findDetailsByExtId(String extId) {
        FluxReport fluxReport = findByExtId(extId).get();

        List<Product> products = productDao.getProductsForFluxReport(fluxReport);
        fluxReport.getDocument().setProducts(products);

        return fluxReport;
    }

    @Override
    public Optional<FluxReport> findCorrectionOf(@NotNull String extId) {
        return findCorrectionOrDeletionOf(extId, Purpose.CORRECTION);
    }

    @Override
    public Optional<FluxReport> findDeletionOf(@NotNull String extId) {
        return findCorrectionOrDeletionOf(extId, Purpose.DELETE);
    }

    private Optional<FluxReport> findCorrectionOrDeletionOf(@NotNull String extId, Purpose purpose) {
        TypedQuery<FluxReport> query = em.createNamedQuery(FluxReport.FIND_BY_REFERENCED_ID_AND_PURPOSE, FluxReport.class);
        query.setParameter(EXT_ID, extId);
        query.setParameter(PURPOSE, purpose);

        List<FluxReport> results = query.getResultList();
        if (results.size() == 1) {
            return Optional.of(results.get(0));
        } else if (results.isEmpty()) {
            return Optional.empty();
        } else {
            log.error("Found more than 1 correction or deletion of report with extId " + extId + "! Going to return the last one.");
            return Optional.of(results.get(results.size()-1));
        }
    }

    @Override
    public FluxReport findLatestVersion(FluxReport fluxReport) {
        Optional<FluxReport> newerVersion = findCorrectionOf(fluxReport.getExtId());
        if (newerVersion.isPresent()) {
            return findLatestVersion(newerVersion.get());
        } else {
            return fluxReport;
        }
    }

    @Override
    public List<FluxReport> findOlderVersions(FluxReport fluxReport) {
        List<FluxReport> olderVersions = new ArrayList<>();

        if (isNotBlank(fluxReport.getPreviousFluxReportExtId())) {

            Optional<FluxReport> previousReport = findByExtId(fluxReport.getPreviousFluxReportExtId());
            if (previousReport.isPresent()) {
                olderVersions.add(previousReport.get());
                olderVersions.addAll(findOlderVersions(previousReport.get()));
            }

        }

        return olderVersions;
    }

}
