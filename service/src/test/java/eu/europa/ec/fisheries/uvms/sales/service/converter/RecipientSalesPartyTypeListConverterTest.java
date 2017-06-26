package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.schema.sales.SalesPartyType;
import eu.europa.ec.fisheries.schema.sales.TextType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by MATBUL on 23/06/2017.
 */
public class RecipientSalesPartyTypeListConverterTest {

    private RecipientSalesPartyTypeListConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new RecipientSalesPartyTypeListConverter();
    }

    @Test
    public void testRecipientSalesPartyTypeListConverter() {
        List<SalesPartyType> salesParties = Arrays.asList(
                new SalesPartyType()
                        .withRoleCodes(new CodeType().withValue("RECIPIENT"))
                        .withName(new TextType().withValue("value")),
                new SalesPartyType()
                        .withRoleCodes(new CodeType().withValue("SELLER"))
                        .withName(new TextType().withValue("bla")));

        String recipient = converter.convert(salesParties, null, null);
        assertEquals("value", recipient);
    }


}