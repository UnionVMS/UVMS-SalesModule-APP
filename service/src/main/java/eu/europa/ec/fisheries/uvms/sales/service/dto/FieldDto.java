package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FieldDto {
    private String key;

    private String value;

    public FieldDto() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FieldDto key(final String key) {
        setKey(key);
        return this;
    }

    public FieldDto value(final String value) {
        setValue(value);
        return this;
    }
}

