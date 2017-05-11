package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.uvms.sales.model.constant.PartyRole;

public class SellerSalesPartyTypeListConverter extends SalesPartyTypeListConverter {

    @Override
    protected String roleToSearchFor() {
        return PartyRole.SELLER.toString();
    }
}
