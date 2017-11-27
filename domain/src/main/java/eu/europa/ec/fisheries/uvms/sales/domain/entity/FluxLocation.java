package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "sales_flux_location")
@SequenceGenerator( name = "sales_flux_location_id_seq",
        sequenceName = "sales_flux_location_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class FluxLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_flux_location_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "ext_id")
    private String extId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "country")
    @Size(max = 3)
    private String countryCode;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @ManyToOne(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_address_id")
    private Address address;

    public Integer getId() {
        return id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public FluxLocation extId(String extId) {
        this.extId = extId;
        return this;
    }

    public FluxLocation type(String type) {
        this.type = type;
        return this;
    }

    public FluxLocation countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public FluxLocation longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public FluxLocation latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public FluxLocation address(Address address) {
        this.address = address;
        return this;
    }
}
