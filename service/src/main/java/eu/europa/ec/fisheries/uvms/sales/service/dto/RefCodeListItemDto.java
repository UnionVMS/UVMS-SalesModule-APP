package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RefCodeListItemDto {
    private String code;
    private String text;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RefCodeListItemDto code(final String code) {
        setCode(code);
        return this;
    }

    public RefCodeListItemDto text(final String text) {
        setText(text);
        return this;
    }
}
