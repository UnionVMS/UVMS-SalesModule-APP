package eu.europa.ec.fisheries.uvms.sales.service.dto.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@EqualsAndHashCode
@ToString
public class ConversionFactor {

    private String code;
    private String presentation;
    private String preservation;
    private BigDecimal factor;

    public ConversionFactor() {

    }

    public ConversionFactor(String code, String presentation, String preservation, BigDecimal factor) {
        this.code = code;
        this.presentation = presentation;
        this.preservation = preservation;
        this.factor = factor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPreservation() {
        return preservation;
    }

    public void setPreservation(String preservation) {
        this.preservation = preservation;
    }
}
