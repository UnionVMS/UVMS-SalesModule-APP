package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.List;

@EqualsAndHashCode
@ToString
public class ReportQueryFilterDto {
    private List<String> excludeFluxReportIds;
    private String flagState;
    private List<String> includeFluxReportIds;
    private String landingPort;
    private SalesCategoryType salesCategory;
    private DateTime salesEndDate;
    private String salesLocation;
    private DateTime salesStartDate;
    private List<String> allSpecies;
    private List<String> anySpecies;
    private List<String> vesselExtIds;
    private String vesselName;

    public List<String> getExcludeFluxReportIds() {
        return excludeFluxReportIds;
    }

    public void setExcludeFluxReportIds(List<String> excludeFluxReportIds) {
        this.excludeFluxReportIds = excludeFluxReportIds;
    }

    public String getFlagState() {
        return flagState;
    }

    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

    public List<String> getIncludeFluxReportIds() {
        return includeFluxReportIds;
    }

    public void setIncludeFluxReportIds(List<String> includeFluxReportIds) {
        this.includeFluxReportIds = includeFluxReportIds;
    }

    public String getLandingPort() {
        return landingPort;
    }

    public void setLandingPort(String landingPort) {
        this.landingPort = landingPort;
    }

    public SalesCategoryType getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(SalesCategoryType salesCategory) {
        this.salesCategory = salesCategory;
    }

    public DateTime getSalesEndDate() {
        return salesEndDate;
    }

    public void setSalesEndDate(DateTime salesEndDate) {
        this.salesEndDate = salesEndDate;
    }

    public String getSalesLocation() {
        return salesLocation;
    }

    public void setSalesLocation(String salesLocation) {
        this.salesLocation = salesLocation;
    }

    public DateTime getSalesStartDate() {
        return salesStartDate;
    }

    public void setSalesStartDate(DateTime salesStartDate) {
        this.salesStartDate = salesStartDate;
    }

    public List<String> getAllSpecies() {
        return allSpecies;
    }

    public void setAllSpecies(List<String> allSpecies) {
        this.allSpecies = allSpecies;
    }

    public List<String> getAnySpecies() {
        return anySpecies;
    }

    public void setAnySpecies(List<String> anySpecies) {
        this.anySpecies = anySpecies;
    }

    public List<String> getVesselExtIds() {
        return vesselExtIds;
    }

    public void setVesselExtIds(List<String> vesselExtIds) {
        this.vesselExtIds = vesselExtIds;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }


    public ReportQueryFilterDto flagState(String flagState) {
        this.flagState = flagState;
        return this;
    }


    public ReportQueryFilterDto landingPort(String landingPort) {
        this.landingPort = landingPort;
        return this;
    }

    public ReportQueryFilterDto salesCategory(SalesCategoryType salesCategory) {
        this.salesCategory = salesCategory;
        return this;
    }

    public ReportQueryFilterDto salesEndDate(DateTime salesEndDate) {
        this.salesEndDate = salesEndDate;
        return this;
    }

    public ReportQueryFilterDto salesLocation(String salesLocation) {
        this.salesLocation = salesLocation;
        return this;
    }

    public ReportQueryFilterDto salesStartDate(DateTime salesStartDate) {
        this.salesStartDate = salesStartDate;
        return this;
    }

    public ReportQueryFilterDto speciesAll(List<String> speciesAll) {
        this.allSpecies = speciesAll;
        return this;
    }

    public ReportQueryFilterDto speciesAny(List<String> speciesAny) {
        this.anySpecies = speciesAny;
        return this;
    }

    public ReportQueryFilterDto vesselExtIds(List<String> vesselExtIds) {
        this.vesselExtIds = vesselExtIds;
        return this;
    }

    public ReportQueryFilterDto vesselName(String vesselName) {
        this.vesselName = vesselName;
        return this;
    }

    public ReportQueryFilterDto excludeFluxReportIds(final List<String> excludeFluxReportIds) {
        setExcludeFluxReportIds(excludeFluxReportIds);
        return this;
    }

    public ReportQueryFilterDto includeFluxReportIds(final List<String> includeFluxReportIds) {
        setIncludeFluxReportIds(includeFluxReportIds);
        return this;
    }


}
