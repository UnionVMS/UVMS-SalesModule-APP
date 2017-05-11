package eu.europa.ec.fisheries.uvms.sales.service.converter;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.FLUXLocationType;
import eu.europa.ec.fisheries.schema.sales.IDType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ListFLUXLocationTypeConverterTest {

    private ListFLUXLocationTypeConverter listFLUXLocationTypeConverter;


    @Before
    public void setUp() throws Exception {
        listFLUXLocationTypeConverter = new ListFLUXLocationTypeConverter();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testConvertFrom() {
        Assert.assertNull(listFLUXLocationTypeConverter.convertFrom(null, null, null));
    }


    @Test
    public void testConvertToWhenListIsNull() {
        Assert.assertNull(listFLUXLocationTypeConverter.convertTo(null, null, null));
    }

    @Test
    public void testConvertToWhenListIsEmpty() {
        Assert.assertEquals(new ArrayList<String>(), listFLUXLocationTypeConverter.convertTo(new ArrayList<FLUXLocationType>(), null, null));
    }

    @Test
    public void testConvertToWhenListIsFilled() {
        FLUXLocationType belgium = new FLUXLocationType().withCountryID(new IDType().withValue("BEL"));
        FLUXLocationType luxemburg = new FLUXLocationType().withCountryID(new IDType().withValue("LUX"));
        Assert.assertEquals(Lists.newArrayList("BEL", "LUX"), listFLUXLocationTypeConverter.convertTo(Lists.newArrayList(belgium, luxemburg), null, null));
    }
}
