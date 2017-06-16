package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class SalesReportDto {
    
    private SalesCategoryType category;
        
    private DocumentDto document;
        
    private FluxReportDto fluxReport;
        
    private LocationDto location;
        
    private List<PartyDto> parties;
        
    private TotalsDto totals;
        
    private List<ProductDto> products;

    private FluxReportItemType itemType;

    /**
     * If this report is the latest version, the previous versions are kept here.
     * If this report is an older version (a correction/deletion exists), the latest version is kept here.
     */
    private List<SalesDetailsRelation> otherVersions;

    /**
     * The related take over document (in case that this is a sales note)
     * or the related sales note (in case that this is a take over document),
     * in case there is any.
     */
    private List<SalesDetailsRelation> relatedReports;

    private boolean latestVersion;

    public SalesReportDto() {
    }

    public SalesCategoryType getCategory() {
        return category;
    }

    public void setCategory(SalesCategoryType category) {
        this.category = category;
    }

    public DocumentDto getDocument() {
        return document;
    }

    public void setDocument(DocumentDto document) {
        this.document = document;
    }

    public FluxReportDto getFluxReport() {
        return fluxReport;
    }

    public void setFluxReport(FluxReportDto fluxReport) {
        this.fluxReport = fluxReport;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public List<PartyDto> getParties() {
        return parties;
    }

    public void setParties(List<PartyDto> parties) {
        this.parties = parties;
    }

    public TotalsDto getTotals() {
        return totals;
    }

    public void setTotals(TotalsDto totals) {
        this.totals = totals;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public FluxReportItemType getItemType() {
        return itemType;
    }

    public void setItemType(FluxReportItemType itemType) {
        this.itemType = itemType;
    }

    public List<SalesDetailsRelation> getOtherVersions() {
        return otherVersions;
    }

    public void setOtherVersions(List<SalesDetailsRelation> otherVersions) {
        this.otherVersions = otherVersions;
    }

    public List<SalesDetailsRelation> getRelatedReports() {
        return relatedReports;
    }

    public void setRelatedReports(List<SalesDetailsRelation> relatedReport) {
        this.relatedReports = relatedReport;
    }

    public boolean isLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(boolean latestVersion) {
        this.latestVersion = latestVersion;
    }

    public SalesReportDto category(final SalesCategoryType type) {
        setCategory(type);
        return this;
    }

    public SalesReportDto document(final DocumentDto document) {
        setDocument(document);
        return this;
    }

    public SalesReportDto fluxReport(final FluxReportDto fluxReport) {
        setFluxReport(fluxReport);
        return this;
    }

    public SalesReportDto location(final LocationDto location) {
        setLocation(location);
        return this;
    }

    public SalesReportDto parties(final List<PartyDto> parties) {
        setParties(parties);
        return this;
    }

    public SalesReportDto totals(final TotalsDto totals) {
        setTotals(totals);
        return this;
    }

    public SalesReportDto products(final List<ProductDto> products) {
        setProducts(products);
        return this;
    }

    public SalesReportDto itemType(FluxReportItemType itemType) {
        setItemType(itemType);
        return this;
    }

    public SalesReportDto otherVersions(List<SalesDetailsRelation> otherVersions) {
        this.otherVersions = otherVersions;
        return this;
    }

    public SalesReportDto relatedReports(List<SalesDetailsRelation> relatedReport) {
        this.relatedReports = relatedReport;
        return this;
    }

    public SalesReportDto latestVersion(boolean latestVersion) {
        this.latestVersion = latestVersion;
        return this;
    }
}
