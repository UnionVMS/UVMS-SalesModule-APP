package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Party;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.PartyDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BuyerPartyDocumentConverterTest {

    private BuyerPartyDocumentConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new BuyerPartyDocumentConverter();
    }

    @Test
    public void testBuyerSalesPartyTypeListConverter() {
        List<PartyDocument> partyDocuments = Arrays.asList(
                new PartyDocument()
                        .role(PartyRole.BUYER)
                        .party(new Party().name("value")),
                new PartyDocument()
                        .role(PartyRole.PROVIDER)
                        .party(new Party().name("bla")));

        String recipient = converter.convert(partyDocuments, null, null);
        assertEquals("value", recipient);
    }


}