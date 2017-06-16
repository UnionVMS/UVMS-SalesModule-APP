package eu.europa.ec.fisheries.uvms.sales.domain.constant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class FluxReportItemTypeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testForCodeWhenArgumentIsSN() {
        assertEquals(FluxReportItemType.SALES_NOTE, FluxReportItemType.forCode("SN"));
    }

    @Test
    public void testForCodeWhenArgumentIsTOD() {
        assertEquals(FluxReportItemType.TAKE_OVER_DOCUMENT, FluxReportItemType.forCode("TOD"));
    }

    @Test
    public void testForCodeWhenArgumentIsStijnIsTheBest() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No FluxReportItemType exists for code Stijn is the best");
        FluxReportItemType.forCode("Stijn is the best");
    }
}
