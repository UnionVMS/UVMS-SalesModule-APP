package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.IDType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FLUXSalesResponseMessageHelperTest {

    @InjectMocks
    private FLUXSalesResponseMessageHelper helper;

    @Test
    public void testGetId() {
        FLUXSalesResponseMessage responseMessage = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                        .withIDS(new IDType().withValue("id")));

        assertEquals("id", helper.getId(responseMessage));
    }

}