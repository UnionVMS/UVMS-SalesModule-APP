package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.model.constant.FluxReportItemType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FluxReportItemTypeConverterTest {

    private FluxReportItemTypeConverter fluxReportItemTypeConverter;


    @Before
    public void setUp() throws Exception {
        fluxReportItemTypeConverter = new FluxReportItemTypeConverter();
    }

    @Test
    public void testConvertFromWhenCodeTypeIsNull() {
        assertNull(fluxReportItemTypeConverter.convertFrom(null, null, null));
    }

    @Test
    public void testCovertFromWhenCodeTypeIsNotNull() {
        CodeType codeType = new CodeType()
                .withValue("SN");
        Assert.assertEquals(FluxReportItemType.SALES_NOTE, fluxReportItemTypeConverter.convertFrom(codeType, null, null));
    }

    @Test
    public void testConvertToWhenItemTypeIsNull() {
        assertNull(fluxReportItemTypeConverter.convertTo(null, null, null));
    }

    @Test
    public void testConvertToWhenItemTypeIsNotNull() {
        assertEquals("TOD", fluxReportItemTypeConverter.convertTo(FluxReportItemType.TAKE_OVER_DOCUMENT, null, null).getValue());
    }
}
