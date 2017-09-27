package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "sales_vessel_contact")
@SequenceGenerator( name = "sales_vessel_contact_id_seq",
        sequenceName = "sales_vessel_contact_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class VesselContact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_vessel_contact_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "role", nullable = false)
    private String role;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_contact_id", nullable = false)
    private Contact contact;

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public VesselContact id(Integer id) {
        this.id = id;
        return this;
    }

    public VesselContact role(String role) {
        this.role = role;
        return this;
    }

    public VesselContact contact(Contact contact) {
        this.contact = contact;
        return this;
    }
}
