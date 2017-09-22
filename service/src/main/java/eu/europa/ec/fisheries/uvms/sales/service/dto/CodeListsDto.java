package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class CodeListsDto {
    private List<RefCodeListItemDto> flagStates;
    private List<RefCodeListItemDto> salesCategories;
    private List<RefCodeListItemDto> salesLocations;
    private List<RefCodeListItemDto> landingPorts;
    private List<RefCodeListItemDto> freshness;
    private List<RefCodeListItemDto> presentations;
    private List<RefCodeListItemDto> preservations;
    private List<RefCodeListItemDto> distributionClasses;
    private List<RefCodeListItemDto> usages;
    private List<RefCodeListItemDto> currencies;
    private List<RefCodeListItemDto> species;

    public List<RefCodeListItemDto> getFlagStates() {
        return flagStates;
    }

    public void setFlagStates(List<RefCodeListItemDto> flagStates) {
        this.flagStates = flagStates;
    }

    public List<RefCodeListItemDto> getSalesCategories() {
        return salesCategories;
    }

    public void setSalesCategories(List<RefCodeListItemDto> salesCategories) {
        this.salesCategories = salesCategories;
    }

    public List<RefCodeListItemDto> getSalesLocations() {
        return salesLocations;
    }

    public void setSalesLocations(List<RefCodeListItemDto> salesLocations) {
        this.salesLocations = salesLocations;
    }

    public List<RefCodeListItemDto> getLandingPorts() {
        return landingPorts;
    }

    public void setLandingPorts(List<RefCodeListItemDto> landingPorts) {
        this.landingPorts = landingPorts;
    }

    public List<RefCodeListItemDto> getFreshness() {
        return freshness;
    }

    public void setFreshness(List<RefCodeListItemDto> freshness) {
        this.freshness = freshness;
    }

    public List<RefCodeListItemDto> getPresentations() {
        return presentations;
    }

    public void setPresentations(List<RefCodeListItemDto> presentations) {
        this.presentations = presentations;
    }

    public List<RefCodeListItemDto> getPreservations() {
        return preservations;
    }

    public void setPreservations(List<RefCodeListItemDto> preservations) {
        this.preservations = preservations;
    }

    public List<RefCodeListItemDto> getDistributionClasses() {
        return distributionClasses;
    }

    public void setDistributionClasses(List<RefCodeListItemDto> distributionClasses) {
        this.distributionClasses = distributionClasses;
    }

    public List<RefCodeListItemDto> getUsages() {
        return usages;
    }

    public void setUsages(List<RefCodeListItemDto> usages) {
        this.usages = usages;
    }

    public List<RefCodeListItemDto> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<RefCodeListItemDto> currencies) {
        this.currencies = currencies;
    }

    public List<RefCodeListItemDto> getSpecies() {
        return species;
    }

    public void setSpecies(List<RefCodeListItemDto> species) {
        this.species = species;
    }

    public CodeListsDto flagStates(final List<RefCodeListItemDto> flagStates) {
        setFlagStates(flagStates);
        return this;
    }

    public CodeListsDto salesTypes(final List<RefCodeListItemDto> salesTypes) {
        setSalesCategories(salesTypes);
        return this;
    }

    public CodeListsDto salesLocations(final List<RefCodeListItemDto> salesLocations) {
        setSalesLocations(salesLocations);
        return this;
    }

    public CodeListsDto landingPorts(final List<RefCodeListItemDto> landingPorts) {
        setLandingPorts(landingPorts);
        return this;
    }

    public CodeListsDto freshness(final List<RefCodeListItemDto> freshness) {
        setFreshness(freshness);
        return this;
    }

    public CodeListsDto presentations(final List<RefCodeListItemDto> presentations) {
        setPresentations(presentations);
        return this;
    }

    public CodeListsDto preservations(final List<RefCodeListItemDto> preservations) {
        setPreservations(preservations);
        return this;
    }

    public CodeListsDto distributionClasses(final List<RefCodeListItemDto> distributionClasses) {
        setDistributionClasses(distributionClasses);
        return this;
    }

    public CodeListsDto usages(final List<RefCodeListItemDto> usages) {
        setUsages(usages);
        return this;
    }

    public CodeListsDto currencies(final List<RefCodeListItemDto> currencies) {
        setCurrencies(currencies);
        return this;
    }

    public CodeListsDto species(final List<RefCodeListItemDto> species) {
        setSpecies(species);
        return this;
    }
}
