package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.AAPProductType;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
import eu.europa.ec.fisheries.uvms.sales.service.MDRService;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import eu.europa.ec.fisheries.uvms.sales.service.mapper.DTO;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Class who's only purpose is to hide low-level logic from the "get sales details" functionality of the ReportServiceBean.
 * Should not be used by any other class or functionality!
 */
@Stateless
public class SalesDetailsHelper {

    static final Logger LOG = LoggerFactory.getLogger(SalesDetailsHelper.class);

    @EJB
    private ReferenceDataCache referenceDataCache;

    @EJB
    private AssetService assetService;

    @EJB
    private ConfigService configService;

    @EJB
    private EcbProxyService ecbProxyService;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private ReportServiceHelper reportServiceHelper;

    @EJB
    private ReportDomainModel reportDomainModel;

    @Inject @DTO
    private MapperFacade mapper;

    @EJB
    private MDRService mdrService;

    public void enrichWithLocation(SalesDetailsDto detailsDto) {
        List<ReferenceCoordinates> referenceCoordinates = referenceDataCache.getMarketAndStorageLocations();

        for (ReferenceCoordinates referenceCoordinate : referenceCoordinates) {
            LocationDto location = detailsDto.getSalesReport().getLocation();
            if (StringUtils.equals(referenceCoordinate.getLocationCode(), location.getExtId())) {
                location.setLatitude(referenceCoordinate.getLatitude());
                location.setLongitude(referenceCoordinate.getLongitude());
            }
        }
    }

    public void enrichWithVesselInformation(SalesDetailsDto detailsDto, Report report) {
        if (report.getAuctionSale() == null
                || report.getAuctionSale().getSalesCategory() != SalesCategoryType.VARIOUS_SUPPLY) {
            String vesselExtId = null;

            try {
                vesselExtId = reportHelper.getVesselExtId(report);

                if (isNotBlank(vesselExtId)) {
                    Asset vessel = assetService.findByCFR(vesselExtId);

                    FishingTripDto fishingTrip = detailsDto.getFishingTrip();
                    fishingTrip.setVesselGuid(vessel.getAssetId().getGuid());
                    fishingTrip.setVesselName(vessel.getName());
                    fishingTrip.setVesselCFR(vessel.getCfr());
                }
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                LOG.error("Cannot retrieve vessel details because not all required fields are provided in the report.", e);
            } catch (SalesServiceException e) {
                LOG.error("Cannot retrieve vessel details of vessel " + vesselExtId, e);
            }
        }
    }

    public void convertPricesInLocalCurrency(SalesDetailsDto detailsDto, Report report) {
        //fetch data
        List<AAPProductType> products = reportHelper.getProductsOfReport(report);
        List<ProductDto> productDtos = detailsDto.getSalesReport().getProducts();

        String localCurrency = configService.getParameter(ParameterKey.CURRENCY);
        String documentCurrency = reportHelper.getDocumentCurrency(report);

        // determine exchange rate, and if it can be determined, update the document currency,
        BigDecimal exchangeRate = BigDecimal.ONE;
        try {
            exchangeRate = determineExchangeRate(report, localCurrency, documentCurrency);
            updateCurrency(detailsDto, localCurrency);
        } catch (SalesServiceException e) {
            LOG.error("Cannot convert product prices in the local currency, because I cannot retrieve the exchange rate", e);
        }

        // copy over the prices. If determination of exchange rate has failed, prices are multiplied with one and the
        // currency has not been updated in the document
        copyOverPricesInNewExchangeRate(products, productDtos, exchangeRate);
    }

    private void updateCurrency(SalesDetailsDto detailsDto, String currency) {
        //update currency
        detailsDto.getSalesReport()
                .getDocument()
                .setCurrency(currency);
    }

    private void copyOverPricesInNewExchangeRate(List<AAPProductType> products, List<ProductDto> productDtos, BigDecimal exchangeRate) {
        //update prices
        for (int i = 0; i < products.size(); i++) {
            AAPProductType product = products.get(i);
            BigDecimal originalPrice = getPriceOfProduct(product);

            ProductDto productDto = productDtos.get(i);
            productDto.setPrice(originalPrice.multiply(exchangeRate));
        }
    }

    private BigDecimal determineExchangeRate(Report report, String localCurrency, String documentCurrency) {
        BigDecimal exchangeRate;
        exchangeRate = BigDecimal.ONE;
        if (!localCurrency.equals(documentCurrency)) {
            DateTime documentDate = reportHelper.getDocumentDate(report);
            exchangeRate = ecbProxyService.findExchangeRate(documentCurrency, localCurrency, documentDate);
        }
        return exchangeRate;
    }

    public void calculateTotals(SalesDetailsDto detailsDto) {
        List<ProductDto> products = detailsDto.getSalesReport().getProducts();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (ProductDto product : products) {
            if (product.getPrice() != null) {
                totalPrice = totalPrice.add(product.getPrice());
            }
            if (product.getWeight() != null) {
                totalWeight = totalWeight.add(product.getWeight());
            }
        }

        detailsDto.getSalesReport()
                .setTotals(new TotalsDto()
                        .totalPrice(totalPrice)
                        .totalWeight(totalWeight));
    }


    private BigDecimal getPriceOfProduct(AAPProductType product) {
        return product
                .getTotalSalesPrice()
                .getChargeAmounts().get(0)
                .getValue();
    }

    /**
     * If the given report is the latest version, the dto is enriched with the previous versions.
     * If the given report is an older version (a correction/deletion exists), the dto is enriched with the latest
     * version (only).
     */
    public void enrichWithOtherRelevantVersions(SalesDetailsDto detailsDto, Report report) {
        SalesReportDto salesReportDto = detailsDto.getSalesReport();

        List<Report> otherRelevantVersions = new ArrayList<>();
        if (reportDomainModel.isLatestVersion(report)) {
            otherRelevantVersions.addAll(reportDomainModel.findOlderVersionsOrderedByCreationDateDescendingIncludingDetails(report));
            salesReportDto.setLatestVersion(true);
        } else {
            otherRelevantVersions.add(reportDomainModel.findLatestVersion(report));
            salesReportDto.setLatestVersion(false);
        }

        List<SalesDetailsRelation> mappedRelations = mapper.mapAsList(otherRelevantVersions, SalesDetailsRelation.class);
        salesReportDto.setOtherVersions(mappedRelations);
    }

    public void enrichWithRelatedReport(SalesDetailsDto detailsDto, Report report) {
        SalesReportDto salesReportDto = detailsDto.getSalesReport();
        List<Report> latestRelatedReport = reportDomainModel.findRelatedReportsOf(report);
        List<SalesDetailsRelation> relations = mapper.mapAsList(latestRelatedReport, SalesDetailsRelation.class);
        salesReportDto.setRelatedReports(relations);
    }

    public void enrichProductsWithFactor(SalesDetailsDto detailsDto) {
        List<ProductDto> products = detailsDto.getSalesReport().getProducts();
        for (ProductDto product : products) {
            if (product.getFactor() == null) {
                BigDecimal conversionFactor = referenceDataCache.getConversionFactorForProduct(product);
                product.setFactor(conversionFactor);
            }
        }
    }
}
