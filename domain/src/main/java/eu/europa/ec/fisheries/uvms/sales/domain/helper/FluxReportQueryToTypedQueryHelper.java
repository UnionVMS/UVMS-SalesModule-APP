package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.SalesCategory;
import eu.europa.ec.fisheries.uvms.sales.domain.dto.FluxReportSearchMode;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.AuctionSale;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static eu.europa.ec.fisheries.uvms.sales.domain.dto.FluxReportSearchMode.COUNT;
import static eu.europa.ec.fisheries.uvms.sales.domain.dto.FluxReportSearchMode.FETCH;

/** Creates a TypedQuery, based on the optional {@link ReportQuery#filters}, {@link ReportQuery#sorting} and
 * {@link ReportQuery#paging} properties of the given {@link ReportQuery}. **/
public class FluxReportQueryToTypedQueryHelper {

    public static final String FISHING_ACTIVITY = "fishingActivity";
    public static final String EXT_ID = "extId";
    public static final String DOCUMENT = "document";
    public static final String VESSEL = "vessel";
    public static final String AUCTION_SALE = "auctionSale";
    public static final String FLUX_LOCATION = "fluxLocation";
    public static final String LOCATION = "location";
    private final EntityManager em;
    private Map<Parameter, Object> parameters = new HashMap<>();
    private CriteriaBuilder builder;
    private CriteriaQuery query;
    private Root<FluxReport> fluxReport;
    private Predicate whereConditions;
    private FluxReportSearchMode searchMode;
    private Paging paging;
    private boolean eagerLoadRelations;

    public static FluxReportQueryToTypedQueryHelper search(@NonNull EntityManager em, @NonNull ReportQueryFilter filters) {
        return new FluxReportQueryToTypedQueryHelper(FETCH, em, filters);
    }

    public static FluxReportQueryToTypedQueryHelper count(@NonNull EntityManager em, @NonNull ReportQueryFilter filters) {
        return new FluxReportQueryToTypedQueryHelper(COUNT, em, filters);
    }

    private FluxReportQueryToTypedQueryHelper(FluxReportSearchMode searchMode, EntityManager em, ReportQueryFilter filters) {
        this.em = em;
        this.builder = em.getCriteriaBuilder();
        this.query = builder.createQuery();
        this.fluxReport = query.from(FluxReport.class);
        this.whereConditions = builder.conjunction();
        this.parameters = new HashMap<>();
        this.searchMode = searchMode;
        filter(filters);
    }

    private void filter(@NonNull ReportQueryFilter filters) {
        withVesselName(filters.getVesselName());
        withFlagState(filters.getFlagState());
        withVesselExternalIds(filters.getVesselExtIds());
        inSalesLocation(filters.getSalesLocation());
        soldAfter(filters.getSalesStartDate());
        soldBefore(filters.getSalesEndDate());
        inLandingPort(filters.getLandingPort());
        withSalesCategory(filters.getSalesCategory());
        withAnySpecies(filters.getAnySpecies());
        withAllSpecies(filters.getAllSpecies());
        includeOnlyIds(filters.getIncludeFluxReportIds());
        excludeIds(filters.getExcludeFluxReportIds());
        withTripId(filters.getTripId());
        withLandingCountry(filters.getLandingCountry());
        withDeleted(filters.isIncludeDeleted());
        notCorrected();
        noDeletionMessages();
    }

    public FluxReportQueryToTypedQueryHelper page(Paging paging) {
        this.paging = paging;
        return this;
    }

    public FluxReportQueryToTypedQueryHelper sort(ReportQuerySorting sorting) {
        if (sorting != null) {
            SortDirection direction = sorting.getDirection();
            ReportQuerySortField field = sorting.getField();

            switch (field) {
                case FLAG_STATE:
                    sortOn(pathToFlagState(), direction);
                    break;
                case LANDING_DATE:
                    sortOn(fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get("startDate"), direction);
                    break;
                case LANDING_PORT:
                    sortOn(pathToLandingPort(), direction);
                    break;
                case VESSEL_NAME:
                    sortOn(pathToVesselName(), direction);
                    break;
                case SALES_DATE:
                    sortOn(pathToSalesDate(), direction);
                    break;
                case SALES_LOCATION:
                    sortOn(pathToSalesLocation(), direction);
                    break;
                case CATEGORY:
                    sortOn(pathToSalesCategory(), direction);
                    break;
                default:
                    throw new UnsupportedOperationException("I cannot sort on field "+ field);
            }
        }
        return this;
    }

    /**
     * When you expect a lot of reports to come back, it is probably not interesting to lazy load all relations.
     * By activating this mode, the query will eager fetch several relations, avoiding the need to do more queries.
     * This mode cannot used when executing a count query.
     */
    public FluxReportQueryToTypedQueryHelper eagerLoadRelations(boolean eagerLoadRelations) {
        this.eagerLoadRelations = eagerLoadRelations;
        return this;
    }

    public TypedQuery build() {
        //select
        if (searchMode == FluxReportSearchMode.FETCH) {
            if (eagerLoadRelations) {
                eagerFetchRelations();
            }
            query = query.select(fluxReport);
        } else if (searchMode == COUNT) {
            query = query.select(builder.count(fluxReport));
        } else {
            throw new UnsupportedOperationException("I cannot build a query for search mode " + searchMode);
        }

        //where
        query = query.where(whereConditions);

        //set parameters
        TypedQuery typedQuery = em.createQuery(query);
        for (Map.Entry<Parameter, Object> parameter : parameters.entrySet()) {
            typedQuery = typedQuery.setParameter(parameter.getKey(), parameter.getValue());
        }

        //paging
        if (paging != null) {
            typedQuery = typedQuery .setFirstResult(determineFirstResult())
                    .setMaxResults(determineMaxResults());
        }

        return typedQuery;
    }

    private void eagerFetchRelations() {
        //eager fetch relations used in ReportSummary
        fluxReport.fetch(AUCTION_SALE, JoinType.LEFT);
        Fetch<Object, Object> documentFetch = fluxReport.fetch(DOCUMENT, JoinType.LEFT);

        documentFetch   .fetch(FLUX_LOCATION, JoinType.LEFT);

        Fetch<Object, Object> fishingActivityFetch = documentFetch.fetch(FISHING_ACTIVITY, JoinType.LEFT);

        fishingActivityFetch.fetch(VESSEL, JoinType.LEFT);
        fishingActivityFetch.fetch(LOCATION, JoinType.LEFT);
    }

    private void notCorrected() {
        Predicate notCorrected = builder.isNull(fluxReport.get("correction"));
        addWhereCondition(notCorrected);
    }

    /** Filters aways the deletion messages. Note that a deletion message is the message that deletes another message.
     * This does not have an effect on these deleted messages, like withDeleted(boolean) does, but on the deletion message! **/
    private void noDeletionMessages() {
        Predicate notCorrected = builder.notEqual(fluxReport.get("purpose"), Purpose.DELETE);
        addWhereCondition(notCorrected);
    }

    private void withDeleted(Boolean includeDeleted) {
        if (BooleanUtils.isNotTrue(includeDeleted)) {
            Predicate notDeleted = builder.isNull(fluxReport.get("deletion"));
            addWhereCondition(notDeleted);
        }
    }

    private void withVesselName(String vesselName) {
        if (StringUtils.isNotBlank(vesselName)) {
            ParameterExpression<String> vesselNameParameter = addParameter(String.class, "vesselName", (vesselName + "%").toLowerCase());

            Predicate vesselNameStartsWithParameter = builder.like(builder.lower(pathToVesselName()), vesselNameParameter);

            addWhereCondition(vesselNameStartsWithParameter);
        }
    }

    private void withFlagState(String flagState) {
        if (StringUtils.isNotBlank(flagState)) {
            ParameterExpression<String> flagStateParameter = addParameter(String.class, "flagState", flagState);

            Predicate vesselCountryEqualsToFlagState = builder.equal(pathToFlagState(), flagStateParameter);

            addWhereCondition(vesselCountryEqualsToFlagState);
        }
    }

    private void withVesselExternalIds(Collection<String> vesselExternalIds) {
        if (CollectionUtils.isNotEmpty(vesselExternalIds)) {
            ParameterExpression<Collection> vesselExternalIdsParameter = addParameter(Collection.class, "vesselExtIds", vesselExternalIds);

            Predicate vesselExternalIdInParameterList =
                    fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get(VESSEL).get(EXT_ID)
                            .in(vesselExternalIdsParameter);

            addWhereCondition(vesselExternalIdInParameterList);
        }
    }

    private void inSalesLocation(String salesLocation) {
        if (StringUtils.isNotBlank(salesLocation)) {
            ParameterExpression<String> salesLocationParameter = addParameter(String.class, "salesLocation", salesLocation);

            Predicate fluxLocationEqualsToSalesLocation = builder.equal(pathToSalesLocation(), salesLocationParameter);

            addWhereCondition(fluxLocationEqualsToSalesLocation);
        }
    }

    private void soldAfter(DateTime salesStartDate) {
        if (salesStartDate != null) {
            ParameterExpression<DateTime> salesStartDateParameter = addParameter(DateTime.class, "salesStartDate", salesStartDate);

            Predicate startDateBeforeOrOnOccurrence = builder.greaterThanOrEqualTo(pathToSalesDate(), salesStartDateParameter);

            addWhereCondition(startDateBeforeOrOnOccurrence);
        }
    }

    private void soldBefore(DateTime salesEndDate) {
        if (salesEndDate != null) {
            ParameterExpression<DateTime> salesEndDateParameter = addParameter(DateTime.class, "salesEndDate", salesEndDate);

            Predicate endDateAfterOrOnOccurrence = builder.lessThanOrEqualTo(pathToSalesDate(), salesEndDateParameter);

            addWhereCondition(endDateAfterOrOnOccurrence);
        }
    }

    private void inLandingPort(String landingPort) {
        if (StringUtils.isNotBlank(landingPort)) {
            ParameterExpression<String> landingPortParameter = addParameter(String.class, "landingPort", landingPort);

            Predicate fishingActivityFluxLocationEqualsToLandingPort = builder.equal(pathToLandingPort(), landingPortParameter);

            addWhereCondition(fishingActivityFluxLocationEqualsToLandingPort);
        }
    }

    private void withSalesCategory(SalesCategoryType salesCategoryType) {
        if (salesCategoryType != null) {

            SalesCategory salesCategory = SalesCategory.valueOf(salesCategoryType.name());
            ParameterExpression<SalesCategory> salesTypeParameter = addParameter(SalesCategory.class, "salesCategory", salesCategory);

            Predicate salesTypeEqualsToParameter = builder.equal(
                    pathToSalesCategory(), salesTypeParameter);

            addWhereCondition(salesTypeEqualsToParameter);
        }
    }

    private void withAnySpecies(Collection<String> species) {
        if (CollectionUtils.isNotEmpty(species)) {
            ParameterExpression<Collection> speciesAnyParameter = addParameter(Collection.class, "speciesAny", species);

            Join<FluxReport, Document> document = fluxReport.join(DOCUMENT);
            Join<Document, Product> product = document.join("products");
            Predicate productContainsAnyOfSpecies = product.get("species").in(speciesAnyParameter);

            addWhereCondition(productContainsAnyOfSpecies);
        }
    }

    private void withAllSpecies(List<String> species) {
        if (CollectionUtils.isNotEmpty(species)) {
            Join<FluxReport, Document> document = fluxReport.join(DOCUMENT);

            for (int i = 0; i < species.size(); i++) {
                ParameterExpression<String> speciesAllParameter = addParameter(String.class, "speciesAll" + i, species.get(i));

                Subquery<String> productsOfDocument = query.subquery(String.class);
                Join<FluxReport, Document> documentToRetrieveProductsFrom = productsOfDocument.correlate(document);
                Join<Document, Product> product = documentToRetrieveProductsFrom.join("products");
                Subquery<String> retrieveSpeciesOfAllProductsOfTheDocument = productsOfDocument.select(product.<String>get("species"));

                Predicate speciesExistsInOneOfTheProducts = speciesAllParameter.in(retrieveSpeciesOfAllProductsOfTheDocument);

                addWhereCondition(speciesExistsInOneOfTheProducts);
            }
        }
    }

    private void includeOnlyIds(Collection<String> includedFluxReportIds) {
        if (CollectionUtils.isNotEmpty(includedFluxReportIds)) {
            ParameterExpression<Collection> includeFluxReportIdsParameter = addParameter(Collection.class, "includeFluxReportIds", includedFluxReportIds);

            Predicate fluxReportIdInGivenIds = fluxReport.get(EXT_ID).in(includeFluxReportIdsParameter);

            addWhereCondition(fluxReportIdInGivenIds);
        }
    }

    private void excludeIds(Collection<String> excludedFluxReportIds) {
        if (CollectionUtils.isNotEmpty(excludedFluxReportIds)) {
            ParameterExpression<Collection> excludeFluxReportIdsParameter = addParameter(Collection.class, "excludeFluxReportIds", excludedFluxReportIds);

            Predicate fluxReportNotIdInGivenIds = builder.not(fluxReport.get(EXT_ID).in(excludeFluxReportIdsParameter));

            addWhereCondition(fluxReportNotIdInGivenIds);
        }
    }

    private void withLandingCountry(String landingCountry) {
        if (landingCountry != null) {
            ParameterExpression<String> landingCountryParameter = addParameter(String.class,"landingCountry", landingCountry);

            Predicate landingCountryEqualsToParameter = builder.equal(
                    fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get("location").get("countryCode"),
                    landingCountryParameter);

            addWhereCondition(landingCountryEqualsToParameter);
        }
    }

    private void withTripId(String tripId) {
        if (tripId != null) {
            ParameterExpression<String> landingCountryParameter = addParameter(String.class,"tripId", tripId);

            Predicate tripIdEqualsToParameter = builder.equal(
                    fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get("fishingTripId"),
                    landingCountryParameter);

            addWhereCondition(tripIdEqualsToParameter);
        }
    }

    private <T> ParameterExpression<T> addParameter(Class<T> type, String name, T value) {
        ParameterExpression<T> parameter = builder.parameter(type, name);
        parameters.put(parameter, value);
        return parameter;
    }

    private void addWhereCondition(Predicate predicate) {
        whereConditions = builder.and(whereConditions, predicate);
    }

    private int determineFirstResult() {
        return paging.getPage() * paging.getItemsPerPage() - paging.getItemsPerPage();
    }

    private int determineMaxResults() {
        return paging.getItemsPerPage();
    }

    private void sortOn(Expression<?> path, SortDirection sortDirection) {
        checkNotNull(sortDirection, "sortDirection not provided");
        switch (sortDirection) {
            case ASCENDING:
                query = query.orderBy(builder.asc(path));
                break;
            case DESCENDING:
                query = query.orderBy(builder.desc(path));
                break;
            default:
                throw new UnsupportedOperationException("I don't know how to sort in sort direction " + sortDirection);
        }

    }

    private Path<String> pathToFlagState() {
        return fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get(VESSEL).get("countryCode");
    }

    private Path<DateTime> pathToSalesDate() {
        return fluxReport.get(DOCUMENT).get("occurrence");
    }

    private Path<String> pathToLandingPort() {
        return fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get("location").get(EXT_ID);
    }

    private Path<String> pathToVesselName() {
        return fluxReport.get(DOCUMENT).get(FISHING_ACTIVITY).get(VESSEL).get("name");
    }

    private Path<String> pathToSalesLocation() {
        return fluxReport.get(DOCUMENT).get(FLUX_LOCATION).get(EXT_ID);
    }

    private Expression<String> pathToSalesCategory() {
        Join<FluxReport, AuctionSale> leftJoinWithAuctionSale = fluxReport.join(AUCTION_SALE, JoinType.LEFT);
        return builder.<String>coalesce(leftJoinWithAuctionSale.<String>get("category"), builder.<String>literal("FIRST_SALE"));
    }

}
