package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;

public class RecipientSalesPartyTypeListConverter extends SalesPartyTypeListConverter {

    @Override
    protected String roleToSearchFor() {
        return PartyRole.RECIPIENT.toString();
    }
}