package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by MATBUL on 31/01/2017.
 */
@Entity
@Table(name = "sales_contact")
@SequenceGenerator( name = "sales_contact_id_seq",
        sequenceName = "sales_contact_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_contact_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "family_name", nullable = false)
    private String familyName;

    @Column(name = "name_suffix")
    private String nameSuffix;

    @Column(name = "gender")
    private String gender;

    @Column(name = "alias")
    private String alias;

    public Contact() {
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    public Contact title(final String title) {
        setTitle(title);
        return this;
    }

    public Contact givenName(final String givenName) {
        setGivenName(givenName);
        return this;
    }

    public Contact middleName(final String middleName) {
        setMiddleName(middleName);
        return this;
    }

    public Contact familyName(final String familyName) {
        setFamilyName(familyName);
        return this;
    }

    public Contact nameSuffix(final String nameSuffix) {
        setNameSuffix(nameSuffix);
        return this;
    }

    public Contact gender(final String gender) {
        setGender(gender);
        return this;
    }

    public Contact alias(final String alias) {
        setAlias(alias);
        return this;
    }


}
