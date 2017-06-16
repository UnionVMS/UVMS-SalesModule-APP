package eu.europa.ec.fisheries.uvms.sales.domain.mother;

import eu.europa.ec.fisheries.schema.sales.AAPProductType;
import eu.europa.ec.fisheries.schema.sales.AmountType;
import eu.europa.ec.fisheries.schema.sales.SalesPriceType;

import java.math.BigDecimal;

/**
 * Utility class to generate objects, meant for tests.
 * The goal is to promote reuse and keep tests smaller/more readable.
 */
public class AAPProductTypeMother {

    public static AAPProductType withTotalPrice(int totalPrice) {
        return withTotalPrice(new BigDecimal(totalPrice));
    }

    public static AAPProductType withTotalPrice(BigDecimal totalPrice) {
        return new AAPProductType()
                .withTotalSalesPrice(new SalesPriceType()
                        .withChargeAmounts(new AmountType()
                                .withValue(totalPrice)));
    }

}
