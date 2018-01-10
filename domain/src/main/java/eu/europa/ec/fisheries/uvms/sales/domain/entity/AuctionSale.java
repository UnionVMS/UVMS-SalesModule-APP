package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.SalesCategory;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sales_auction_sale")
@SequenceGenerator( name = "sales_auction_sale_id_seq",
                    sequenceName = "sales_auction_sale_id_seq",
                    allocationSize = 50)
@EqualsAndHashCode
@ToString
public class AuctionSale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "sales_auction_sale_id_seq")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SalesCategory category;

    @Column(name = "country")
    private String countryCode;

    public Integer getId() {
        return id;
    }

    public SalesCategory getCategory() {
        return category;
    }

    public void setCategory(SalesCategory type) {
        this.category = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String country) {
        this.countryCode = country;
    }

    public AuctionSale category(final SalesCategory type) {
        setCategory(type);
        return this;
    }

    public AuctionSale countryCode(final String country) {
        setCountryCode(country);
        return this;
    }

}
