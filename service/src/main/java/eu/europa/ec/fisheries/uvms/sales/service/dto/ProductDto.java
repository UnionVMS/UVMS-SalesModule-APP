package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode
@ToString
public class ProductDto {
    private String species;
    private List<String> areas;
    private String freshness;
    private String presentation;
    private String preservation;
    private BigDecimal factor;
    private String distributionCategory;
    private String distributionClass;
    private String usage;
    private BigDecimal weight;
    private BigDecimal quantity;
    private BigDecimal price;

    public ProductDto() {
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public String getFreshness() {
        return freshness;
    }

    public void setFreshness(String freshness) {
        this.freshness = freshness;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPreservation() {
        return preservation;
    }

    public void setPreservation(String preservation) {
        this.preservation = preservation;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public String getDistributionCategory() {
        return distributionCategory;
    }

    public void setDistributionCategory(String distributionCategory) {
        this.distributionCategory = distributionCategory;
    }

    public String getDistributionClass() {
        return distributionClass;
    }

    public void setDistributionClass(String distributionClass) {
        this.distributionClass = distributionClass;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductDto species(final String species) {
        setSpecies(species);
        return this;
    }

    public ProductDto areas(final List<String> areas) {
        setAreas(areas);
        return this;
    }

    public ProductDto freshness(final String freshness) {
        setFreshness(freshness);
        return this;
    }

    public ProductDto presentation(final String presentation) {
        setPresentation(presentation);
        return this;
    }

    public ProductDto preservation(final String preservation) {
        setPreservation(preservation);
        return this;
    }

    public ProductDto factor(final BigDecimal factor) {
        setFactor(factor);
        return this;
    }

    public ProductDto distributionCategory(final String distributionCategory) {
        setDistributionCategory(distributionCategory);
        return this;
    }

    public ProductDto distributionClass(final String distributionClass) {
        setDistributionClass(distributionClass);
        return this;
    }

    public ProductDto usage(final String usage) {
        setUsage(usage);
        return this;
    }

    public ProductDto weight(final BigDecimal weight) {
        setWeight(weight);
        return this;
    }

    public ProductDto quantity(final BigDecimal quantity) {
        setQuantity(quantity);
        return this;
    }

    public ProductDto price(final BigDecimal price) {
        setPrice(price);
        return this;
    }
}
