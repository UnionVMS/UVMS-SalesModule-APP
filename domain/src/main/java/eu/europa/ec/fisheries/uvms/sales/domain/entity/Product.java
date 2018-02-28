package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "sales_product")
@SequenceGenerator(name = "sales_product_id_seq",
        sequenceName = "sales_product_id_seq",
        allocationSize = 50)
@EqualsAndHashCode(exclude = "document")
@ToString(exclude = "document")
@NamedQueries(
        @NamedQuery(name = Product.FIND_PRODUCTS_BY_DOCUMENT, query="SELECT product FROM Product product LEFT JOIN FETCH product.origins WHERE product.document = :document")
)
public class Product {

    public static final String FIND_PRODUCTS_BY_DOCUMENT = "FluxReport.findProductsByDocument";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_product_id_seq")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "species", nullable = false)
    private String species;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "weight")
    private Double weight;

    @NotNull
    @Column(name = "usage", nullable = false)
    private String usage;

    @Column(name = "freshness")
    private String freshness;

    @Column(name = "presentation")
    private String presentation;

    @Column(name = "preservation")
    private String preservation;

    @Column(name = "factor")
    private Double factor;

    @NotNull
    @Column(name = "distribution_class", nullable = false)
    private String distributionClass;

    @NotNull
    @Column(name = "distribution_category", nullable = false)
    private String distributionCategory;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "price_local")
    private BigDecimal priceLocal;

    @Valid
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_document_id", nullable = false)
    private Document document;

    @Valid
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "sales_origin",
                joinColumns = @JoinColumn(name = "sales_product_id"),
                inverseJoinColumns = @JoinColumn(name = "sales_flux_location_id"))
    @BatchSize(size = 1000)
    private List<FluxLocation> origins;

    public Integer getId() {
        return id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
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

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public String getDistributionClass() {
        return distributionClass;
    }

    public void setDistributionClass(String distributionClass) {
        this.distributionClass = distributionClass;
    }

    public String getDistributionCategory() {
        return distributionCategory;
    }

    public void setDistributionCategory(String distributionCategory) {
        this.distributionCategory = distributionCategory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public List<FluxLocation> getOrigins() {
        return origins;
    }

    public void setOrigins(List<FluxLocation> origins) {
        this.origins = origins;
    }

    public BigDecimal getPriceLocal() {
        return priceLocal;
    }

    public void setPriceLocal(BigDecimal priceLocal) {
        this.priceLocal = priceLocal;
    }

    public Product species(final String species) {
        setSpecies(species);
        return this;
    }

    public Product quantity(final Double quantity) {
        setQuantity(quantity);
        return this;
    }

    public Product weight(final Double weight) {
        setWeight(weight);
        return this;
    }

    public Product usage(final String usage) {
        setUsage(usage);
        return this;
    }

    public Product freshness(final String freshness) {
        setFreshness(freshness);
        return this;
    }

    public Product presentation(final String presentation) {
        setPresentation(presentation);
        return this;
    }

    public Product preservation(final String preservation) {
        setPreservation(preservation);
        return this;
    }

    public Product factor(final Double factor) {
        setFactor(factor);
        return this;
    }

    public Product distributionClass(final String distributionClass) {
        setDistributionClass(distributionClass);
        return this;
    }

    public Product distributionCategory(final String distributionCategory) {
        setDistributionCategory(distributionCategory);
        return this;
    }

    public Product price(final BigDecimal price) {
        setPrice(price);
        return this;
    }

    public Product document(final Document document) {
        setDocument(document);
        return this;
    }

    public Product origins(final List<FluxLocation> origins) {
        setOrigins(origins);
        return this;
    }

    public Product priceLocal(BigDecimal priceLocal) {
        this.priceLocal = priceLocal;
        return this;
    }

}
