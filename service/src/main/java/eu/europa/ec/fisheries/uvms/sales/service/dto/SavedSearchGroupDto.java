package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class SavedSearchGroupDto {
    private Integer id;

    private String user;

    private String name;

    private List<FieldDto> fields;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<FieldDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldDto> fields) {
        this.fields = fields;
    }

    public SavedSearchGroupDto id(final int id) {
        setId(id);
        return this;
    }

    public SavedSearchGroupDto user(final String user) {
        setUser(user);
        return this;
    }

    public SavedSearchGroupDto name(final String name) {
        setName(name);
        return this;
    }

    public SavedSearchGroupDto fields(final List<FieldDto> fields) {
        setFields(fields);
        return this;
    }

}
