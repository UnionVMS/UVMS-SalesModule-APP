package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "sales_party_document")
@SequenceGenerator( name = "sales_party_document_id_seq",
        sequenceName = "sales_party_document_id_seq",
        allocationSize = 50)
@EqualsAndHashCode(exclude = "document")
@ToString(exclude = "document")
public class PartyDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_party_document_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "country")
    @Size(max = 3)
    private String country;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PartyRole role;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_party_id", nullable = false)
    private Party party;

    public Integer getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public PartyRole getRole() {
        return role;
    }

    public void setRole(PartyRole role) {
        this.role = role;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public PartyDocument country(final String country) {
        setCountry(country);
        return this;
    }

    public PartyDocument role(final PartyRole role) {
        setRole(role);
        return this;
    }

    public PartyDocument party(final Party party) {
        setParty(party);
        return this;
    }


}
