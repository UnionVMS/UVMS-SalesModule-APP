package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "sales_document")
@SequenceGenerator( name = "sales_document_id_seq",
        sequenceName = "sales_document_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
@NamedQueries({
        @NamedQuery(name = Document.FIND_BY_EXT_ID, query = "SELECT document from Document document WHERE document.extId = :extId"),
})
public class Document {

    public static final String FIND_BY_EXT_ID = "findByExtId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_document_id_seq")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "ext_id", nullable = false)
    private String extId;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;


    @Column(name = "currency_local")
    private String currencyLocal;


    @Column(name = "total_price_local")
    private BigDecimal totalPriceLocal;

    @NotNull
    @Column(name = "occurrence", nullable = false)
    private DateTime occurrence;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Valid
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_fishing_activity_id")
    private FishingActivity fishingActivity;

    @Valid
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_flux_location_id")
    private FluxLocation fluxLocation;

    @Valid
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_document_id", nullable = false)
    @BatchSize(size = 1000)
    private List<PartyDocument> partyDocuments;

    @Valid
    @OneToMany(mappedBy = "document", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @BatchSize(size = 1000)
    private List<Product> products;

    public BigDecimal getTotalPriceLocal() {
        return totalPriceLocal;
    }

    public void setTotalPriceLocal(BigDecimal totalPriceLocal) {
        this.totalPriceLocal = totalPriceLocal;
    }

    public Integer getId() {
        return id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DateTime getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(DateTime occurrence) {
        this.occurrence = occurrence;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public FishingActivity getFishingActivity() {
        return fishingActivity;
    }

    public void setFishingActivity(FishingActivity fishingActivity) {
        this.fishingActivity = fishingActivity;
    }

    public FluxLocation getFluxLocation() {
        return fluxLocation;
    }

    public void setFluxLocation(FluxLocation fluxLocation) {
        this.fluxLocation = fluxLocation;
    }

    public List<PartyDocument> getPartyDocuments() {
        return partyDocuments;
    }

    public void setPartyDocuments(List<PartyDocument> partyDocuments) {
        this.partyDocuments = partyDocuments;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getCurrencyLocal() {
        return currencyLocal;
    }

    public void setCurrencyLocal(String currencyLocal) {
        this.currencyLocal = currencyLocal;
    }

    public Document id(Integer id) {
        this.id = id;
        return this;
    }

    public Document extId(String extId) {
        this.extId = extId;
        return this;
    }

    public Document currency(String currency) {
        this.currency = currency;
        return this;
    }

    public Document occurrence(DateTime occurrence) {
        this.occurrence = occurrence;
        return this;
    }

    public Document totalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public Document totalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
        return this;
    }

    public Document fishingActivity(FishingActivity fishingActivity) {
        this.fishingActivity = fishingActivity;
        return this;
    }

    public Document fluxLocation(FluxLocation fluxLocation) {
        this.fluxLocation = fluxLocation;
        return this;
    }

    public Document partyDocuments(List<PartyDocument> partyDocuments) {
        this.partyDocuments = partyDocuments;
        return this;
    }

    public Document products(List<Product> products) {
        this.products = products;
        return this;
    }


    public Document totalPriceLocal(BigDecimal totalPriceLocal) {
        this.totalPriceLocal = totalPriceLocal;
        return this;
    }

    public Document currencyLocal(String currencyLocal) {
        this.currencyLocal = currencyLocal;
        return this;
    }
}
