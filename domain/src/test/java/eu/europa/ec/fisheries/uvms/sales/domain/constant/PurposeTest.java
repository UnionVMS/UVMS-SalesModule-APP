package eu.europa.ec.fisheries.uvms.sales.domain.constant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class PurposeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testForNumericCodeWhenArgumentIs9() {
        assertEquals(Purpose.ORIGINAL, Purpose.forNumericCode(9));
    }

    @Test
    public void testForNumericCodeWhenArgumentIs5() {
        assertEquals(Purpose.CORRECTION, Purpose.forNumericCode(5));
    }

    @Test
    public void testForNumericCodeWhenArgumentIs1() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No purpose code exists for number 1");
        Purpose.forNumericCode(1);
    }
}
