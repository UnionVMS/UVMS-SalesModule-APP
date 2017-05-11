package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.AAPProductType;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.AssetService;
import eu.europa.ec.fisheries.uvms.sales.service.EcbProxyService;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.List;

/**
 * Class who's only purpose is to hide low-level logic from the "get sales details" functionality of the ReportServiceBean.
 * Should not be used by any other class or functionality!
 */
@Stateless
public class SalesDetailsHelper {

    final static Logger LOG = LoggerFactory.getLogger(SalesDetailsHelper.class);

    @EJB
    private ReferenceDataCache referenceDataCache;

    @EJB
    private AssetService assetService;

    @EJB
    private ParameterService parameterService;

    @EJB
    private EcbProxyService ecbProxyService;

    @EJB
    private ReportHelper reportHelper;

    public void enrichWithLocation(SalesDetailsDto detailsDto) {
        //find coordinates for a FLUX location in the cache. TODO: this info should come from MDR
        List<ReferenceCoordinates> referenceCoordinates = referenceDataCache.getReferenceCoordinates();

        for (ReferenceCoordinates referenceCoordinate : referenceCoordinates) {
            LocationDto location = detailsDto.getSalesNote().getLocation();
            if (referenceCoordinate.getLocationCode().equals(location.getExtId())) {
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

                Asset vessel = assetService.findByExtId(vesselExtId);

                FishingTripDto fishingTrip = detailsDto.getFishingTrip();
                fishingTrip.setVesselGuid(vessel.getAssetId().getGuid());
                fishingTrip.setVesselName(vessel.getName());
                fishingTrip.setVesselCFR(vessel.getCfr());

            } catch (NullPointerException | IndexOutOfBoundsException e) {
                LOG.error("Cannot retrieve vessel details because not all required fields are provided in the report.", e);
            } catch (ServiceException e) {
                LOG.error("Cannot retrieve vessel details of vessel " + vesselExtId, e);
            }
        }
    }

    public void convertPricesInLocalCurrency(SalesDetailsDto detailsDto, Report report) {
        try {
            String localCurrency = parameterService.getStringValue(ParameterKey.CURRENCY.getKey());
            String documentCurrency = reportHelper.getDocumentCurrency(report);

            BigDecimal exchangeRate = BigDecimal.ONE;
            if (!localCurrency.equals(documentCurrency)) {
                DateTime documentDate = reportHelper.getDocumentDate(report);
                exchangeRate = ecbProxyService.findExchangeRate(documentCurrency, localCurrency, documentDate);
            }

            List<AAPProductType> products = reportHelper.getProductsOfReport(report);
            List<ProductDto> productDtos = detailsDto.getSalesNote().getProducts();

            for (int i = 0; i < products.size(); i++) {
                AAPProductType product = products.get(i);
                BigDecimal originalPrice = getPriceOfProduct(product);

                ProductDto productDto = productDtos.get(i);
                productDto.setPrice(originalPrice.multiply(exchangeRate));
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            LOG.error("Cannot convert product prices in the local currency details because not all required fields are provided in the report.", e);
        } catch (ConfigServiceException e) {
            LOG.error("Cannot convert product prices in the local currency, because I cannot retrieve the local currency");
        } catch (ServiceException e) {
            LOG.error("Cannot convert product prices in the local currency, because I cannot retrieve the exchange rate", e);
        }
    }

    public void calculateTotals(SalesDetailsDto detailsDto) {
        List<ProductDto> products = detailsDto.getSalesNote().getProducts();
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

        detailsDto.getSalesNote()
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

}
