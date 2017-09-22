package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@EqualsAndHashCode
@ToString
public class TotalsDto {
    private BigDecimal totalPrice;
    private BigDecimal totalWeight;

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public TotalsDto totalPrice(final BigDecimal totalPrice) {
        setTotalPrice(totalPrice);
        return this;
    }

    public TotalsDto totalWeight(final BigDecimal totalWeight) {
        setTotalWeight(totalWeight);
        return this;
    }
}

