package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Party;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.PartyDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SalesPartyDocumentConverterTest {

    private PartyDocumentConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new ProviderPartyDocumentConverter();
    }

    @Test
    public void convertWhenSuccess() throws Exception {
        List<PartyDocument> partyDocuments = Arrays.asList(
                new PartyDocument()
                        .role(PartyRole.BUYER)
                        .party(new Party().name("Mathiblaa")),
                new PartyDocument()
                        .role(PartyRole.PROVIDER)
                        .party(new Party().name("Superstijn")));

        assertEquals("Superstijn", converter.convert(partyDocuments, null, null));
    }


    @Test
    public void convertWhenNoRoleIsFilledIn() throws Exception {
        List<PartyDocument> partyDocuments = Arrays.asList(
                new PartyDocument()
                        .party(new Party().name("Mathiblaa")),
                new PartyDocument()
                        .party(new Party().name("Superstijn")));

        assertEquals("N/A", converter.convert(partyDocuments, null, null));

    }

    @Test
    public void convertWhenPartyIsNull() throws Exception {
        List<PartyDocument> partyDocuments = Arrays.asList(
                new PartyDocument()
                        .role(PartyRole.BUYER)
                        .party(new Party().name("Mathiblaa")),
                new PartyDocument()
                        .role(PartyRole.PROVIDER));

        assertEquals("N/A", converter.convert(partyDocuments, null, null));
    }

    @Test
    public void convertWhenNameIsNull() throws Exception {
        List<PartyDocument> partyDocuments = Arrays.asList(
                new PartyDocument()
                        .role(PartyRole.BUYER)
                        .party(new Party().name("Mathiblaa")),
                new PartyDocument()
                        .role(PartyRole.PROVIDER)
                        .party(new Party()));

        assertEquals("N/A", converter.convert(partyDocuments, null, null));
    }

    @Test
    public void convertWhenNoMatchingRole() throws Exception {
        List<PartyDocument> partyDocuments = Arrays.asList(
                new PartyDocument()
                        .role(PartyRole.BUYER)
                        .party(new Party().name("Mathiblaa")),
                new PartyDocument()
                        .role(PartyRole.RECIPIENT)
                        .party(new Party().name("Superstijn")));

        assertEquals("N/A", converter.convert(partyDocuments, null, null));
    }

}