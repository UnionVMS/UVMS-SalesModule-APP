package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by MATBUL on 31/01/2017.
 */
@Entity
@Table(name = "sales_party")
@SequenceGenerator(name = "sales_party_id_seq",
        sequenceName = "sales_party_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_party_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "ext_id")
    private String extId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "flux_organization_name")
    private String fluxOrganizationName;

    @ManyToOne(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_address_id")
    private Address address;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFluxOrganizationName() {
        return fluxOrganizationName;
    }

    public void setFluxOrganizationName(String fluxOrganizationName) {
        this.fluxOrganizationName = fluxOrganizationName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Party id(final Integer id) {
        setId(id);
        return this;
    }

    public Party extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public Party name(final String name) {
        setName(name);
        return this;
    }

    public Party fluxOrganizationName(final String fluxOrganizationName) {
        setFluxOrganizationName(fluxOrganizationName);
        return this;
    }

    public Party address(final Address address) {
        setAddress(address);
        return this;
    }


}
