package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class PartyDto {
    private String extId;
    private String name;
    private String role;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PartyDto extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public PartyDto name(final String name) {
        setName(name);
        return this;
    }

    public PartyDto role(final String role) {
        setRole(role);
        return this;
    }


}
