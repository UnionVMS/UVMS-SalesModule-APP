package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PurposeConverterTest {

    private PurposeConverter purposeConverter;


    @Before
    public void setUp() throws Exception {
        purposeConverter = new PurposeConverter();
    }

    @Test
    public void testConvertFromWhenCodeTypeIsNull() {
        assertNull(purposeConverter.convertFrom(null, null, null));
    }

    @Test
    public void testCovertFromWhenCodeTypeIsNotNull() {
        CodeType codeType = new CodeType()
                .withValue("9");
        assertEquals(Purpose.ORIGINAL, purposeConverter.convertFrom(codeType, null, null));
    }

    @Test
    public void testConvertToWhenPurposeIsNull() {
        assertNull(purposeConverter.convertTo(null, null, null));
    }

    @Test
    public void testConvertToWhenPurposeIsNotNull() {
        assertEquals("9", purposeConverter.convertTo(Purpose.ORIGINAL, null, null).getValue());
    }
}
