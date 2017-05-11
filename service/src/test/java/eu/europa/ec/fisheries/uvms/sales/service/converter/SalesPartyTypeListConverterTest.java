package eu.europa.ec.fisheries.uvms.sales.service.converter;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.schema.sales.FLUXOrganizationType;
import eu.europa.ec.fisheries.schema.sales.SalesPartyType;
import eu.europa.ec.fisheries.schema.sales.TextType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SalesPartyTypeListConverterTest {

    private SalesPartyTypeListConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new SellerSalesPartyTypeListConverter();
    }

    @Test
    public void convertWhenSuccess() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("SELLER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("Superstijn", converter.convert(salesParties, null, null));
    }

    @Test
    public void convertWhenListOfRolesIsNull() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType())
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));

    }

    @Test
    public void convertWhenListOfRolesIsEmpty() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new ArrayList<CodeType>())
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType())
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));
    }

    @Test
    public void convertWhenRoleIsNull() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType())
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));

    }

    @Test
    public void convertWhenRoleValueIsNull() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType())
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType())
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));
    }

    @Test
    public void convertWhenFLUXOrganizationIsNull() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("SELLER"));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));
    }

    @Test
    public void convertWhenFLUXOrganizationNameIsNull() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType());

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("SELLER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType());

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));
    }

    @Test
    public void convertWhenFLUXOrganizationNameValueIsNull() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType()));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("SELLER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType()));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));
    }

    @Test
    public void convertWhenNoMatchingRole() throws Exception {
        SalesPartyType salesParty1 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("BUYER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Mathiblaa")));

        SalesPartyType salesParty2 = new SalesPartyType()
                .withRoleCodes(new CodeType().withValue("RECEIVER"))
                .withSpecifiedFLUXOrganization(new FLUXOrganizationType().withName(new TextType().withValue("Superstijn")));

        List<SalesPartyType> salesParties = Lists.newArrayList(salesParty1, salesParty2);

        assertEquals("N/A", converter.convert(salesParties, null, null));
    }

}