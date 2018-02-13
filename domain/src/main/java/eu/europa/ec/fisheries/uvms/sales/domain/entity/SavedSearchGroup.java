package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Entity
@Table(name = "sales_saved_search_group")
@SequenceGenerator(name = "sales_saved_search_group_id_seq",
        sequenceName = "sales_saved_search_group_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
@NamedQueries(
        @NamedQuery(name = SavedSearchGroup.FIND_BY_USER, query = "SELECT s FROM SavedSearchGroup s WHERE s.user = :user")
)
public class SavedSearchGroup {

    public static final String FIND_BY_USER = "SavedSearchGroup.findByUser";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_saved_search_group_id_seq")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String user;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="sales_saved_search_group_field", joinColumns=@JoinColumn(name="sales_saved_search_group_id"))
    private Map<String, String> fields;

    public Integer getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SavedSearchGroup user(final String user) {
        setUser(user);
        return this;
    }

    public SavedSearchGroup name(final String name) {
        setName(name);
        return this;
    }

    public SavedSearchGroup fields(final Map<String, String> fields) {
        setFields(fields);
        return this;
    }

    public SavedSearchGroup id(final int id) {
        this.id = id;
        return this;
    }
}
