package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by MATBUL on 31/01/2017.
 */
@Entity
@Table(name = "sales_address")
@SequenceGenerator( name = "sales_address_id_seq",
                    sequenceName = "sales_address_id_seq",
                    allocationSize = 50)
@EqualsAndHashCode
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_address_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "ext_id")
    private String extId;

    @Column(name = "block_name")
    private String block;

    @Column(name = "building_name")
    private String building;

    @Column(name = "city_name", nullable = false)
    private String city;

    @Column(name = "city_sub_division_name")
    private String citySubDivision;

    @Column(name = "country", nullable = false)
    @Size(max = 3)
    private String countryCode;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "country_sub_division_name")
    private String countrySubDivision;

    @Column(name = "plot_id")
    private String plotId;

    @Column(name = "post_office_box")
    private String postOfficeBox;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "street_name", nullable = false)
    private String street;

    public Address() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCitySubDivision() {
        return citySubDivision;
    }

    public void setCitySubDivision(String citySubDivision) {
        this.citySubDivision = citySubDivision;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountrySubDivision() {
        return countrySubDivision;
    }

    public void setCountrySubDivision(String countrySubDivision) {
        this.countrySubDivision = countrySubDivision;
    }

    public String getPlotId() {
        return plotId;
    }

    public void setPlotId(String plotId) {
        this.plotId = plotId;
    }

    public String getPostOfficeBox() {
        return postOfficeBox;
    }

    public void setPostOfficeBox(String postOfficeBox) {
        this.postOfficeBox = postOfficeBox;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Address extId(String extId) {
        this.extId = extId;
        return this;
    }

    public Address block(String block) {
        this.block = block;
        return this;
    }

    public Address building(String building) {
        this.building = building;
        return this;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    public Address citySubDivision(String citySubDivision) {
        this.citySubDivision = citySubDivision;
        return this;
    }

    public Address countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public Address countryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public Address countrySubDivision(String countrySubDivision) {
        this.countrySubDivision = countrySubDivision;
        return this;
    }

    public Address plotId(String plotId) {
        this.plotId = plotId;
        return this;
    }

    public Address postOfficeBox(String postOfficeBox) {
        this.postOfficeBox = postOfficeBox;
        return this;
    }

    public Address postcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public Address street(String street) {
        this.street = street;
        return this;
    }
}
