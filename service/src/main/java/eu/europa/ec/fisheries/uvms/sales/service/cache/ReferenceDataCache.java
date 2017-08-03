package eu.europa.ec.fisheries.uvms.sales.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import eu.europa.ec.fisheries.uvms.sales.service.MDRService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import eu.europa.ec.fisheries.uvms.sales.service.mapper.DTO;
import ma.glasnost.orika.MapperFacade;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class ReferenceDataCache {

    private LoadingCache<MDRCodeListKey, List<ObjectRepresentation>> cache;

    @EJB
    private MDRService mdrService;

    @Inject @DTO
    private MapperFacade mapper;

    public ReferenceDataCache() {

    }

    public ReferenceDataCache(MDRService mdrService, MapperFacade mapper) {
        this.mdrService = mdrService;
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() {
        if (cache == null) {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build(
                            new CacheLoader<MDRCodeListKey, List<ObjectRepresentation>>() {
                                @Override
                                public List<ObjectRepresentation> load(MDRCodeListKey key) throws Exception {
                                    return mdrService.findCodeList(key);
                                }
                            }
                    );
        }
    }

    public List<ReferenceCode> getFlagStates() {
        return map(todays(MDRCodeListKey.FLAG_STATES), ReferenceCode.class);
    }

    public List<ReferenceCode> getSalesCategories() {
        return Lists.newArrayList(  new ReferenceCode(SalesCategoryType.FIRST_SALE.toString(), "First Sale"),
                                    new ReferenceCode(SalesCategoryType.NEGOTIATED_SALE.toString(), "Negotiated Sale"),
                                    new ReferenceCode(SalesCategoryType.VARIOUS_SUPPLY.toString(), "Various Supply"));
    }

    public List<ReferenceCode> getSalesLocations() {
        return map(todays(MDRCodeListKey.SALES_LOCATIONS), ReferenceCode.class);
    }

    public List<ReferenceCode> getFreshness() {
        return map(todays(MDRCodeListKey.FRESHNESS), ReferenceCode.class);
    }

    public List<ReferenceCode> getPresentations() {
        return map(todays(MDRCodeListKey.PRESENTATIONS), ReferenceCode.class);
    }

    public List<ReferenceCode> getPreservations() {
        return map(todays(MDRCodeListKey.PRESERVATIONS), ReferenceCode.class);
    }

    public List<ReferenceCode> getDistributionClasses() {
        return map(todays(MDRCodeListKey.DISTRIBUTION_CLASSES), ReferenceCode.class);
    }

    public List<ReferenceCode> getUsages() {
        return map(todays(MDRCodeListKey.USAGES), ReferenceCode.class);
    }

    public List<ReferenceCode> getCurrencies() {
        return map(todays(MDRCodeListKey.CURRENCIES), ReferenceCode.class);
    }

    public List<ReferenceCode> getSpecies() {
        return map(todays(MDRCodeListKey.SPECIES), ReferenceCode.class);
    }

    public List<ReferenceCoordinates> getMarketAndStorageLocations() {
        return map(todays(MDRCodeListKey.MARKET_AND_STORAGE_LOCATIONS), ReferenceCoordinates.class);
    }

    private <T> List<T> map(List<ObjectRepresentation> objectRepresentations, Class<T> classToMapTo) {
        return mapper.mapAsList(objectRepresentations, classToMapTo);
    }

    private List<ObjectRepresentation> todays(MDRCodeListKey mdrCodeListKey) {
        //TODO: select only those who are valid today
        return getFromCache(mdrCodeListKey);
    }

    private List<ObjectRepresentation> getFromCache(MDRCodeListKey mdrCodeListKey) {
        return cache.getUnchecked(mdrCodeListKey);
    }
}
