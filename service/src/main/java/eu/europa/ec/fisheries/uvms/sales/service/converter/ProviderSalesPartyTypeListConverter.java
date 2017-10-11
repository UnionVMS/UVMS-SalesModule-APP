package eu.europa.ec.fisheries.uvms.sales.service.converter;

public class ProviderSalesPartyTypeListConverter extends SalesPartyTypeListConverter {

    @Override
    protected String roleToSearchFor() {
        return "PROVIDER";
    }
}
