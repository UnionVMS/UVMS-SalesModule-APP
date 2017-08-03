package eu.europa.ec.fisheries.uvms.sales.service.dto.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReferenceCode {

    private String code;
    private String text;

    public ReferenceCode() {

    }

    public ReferenceCode(String code, String text) {
        this.code = code;
        this.text = text;
    }

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
}
