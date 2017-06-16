package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.IDType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FLUXSalesResponseMessageHelperTest {

    private FLUXSalesResponseMessageHelper helper;

    @Before
    public void init() {
        helper = new FLUXSalesResponseMessageHelper();
    }

    @Test
    public void testGetId() {
        FLUXSalesResponseMessage responseMessage = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                        .withIDS(new IDType().withValue("id")));

        assertEquals("id", helper.getId(responseMessage));
    }

}