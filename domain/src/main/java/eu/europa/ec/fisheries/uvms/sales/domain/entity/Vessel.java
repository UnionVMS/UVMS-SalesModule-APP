package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "sales_vessel")
@SequenceGenerator( name = "sales_vessel_id_seq",
        sequenceName = "sales_vessel_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class Vessel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_vessel_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "cfr", nullable = false)
    private String extId;

    @Column(name = "name")
    private String name;

    @Column(name = "country", nullable = false)
    @Size(max = 3)
    private String countryCode;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_vessel_id", nullable = false)
    private List<VesselContact> vesselContacts;

    public Integer getId() {
        return id;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<VesselContact> getVesselContacts() {
        return vesselContacts;
    }

    public void setVesselContacts(List<VesselContact> vesselContacts) {
        this.vesselContacts = vesselContacts;
    }

    public Vessel id(Integer id) {
        this.id = id;
        return this;
    }

    public Vessel extId(String extId) {
        this.extId = extId;
        return this;
    }

    public Vessel name(String name) {
        this.name = name;
        return this;
    }

    public Vessel countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public Vessel vesselContacts(List<VesselContact> vesselContacts) {
        this.vesselContacts = vesselContacts;
        return this;
    }
}
