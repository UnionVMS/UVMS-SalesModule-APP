package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;

public class BuyerPartyDocumentConverter extends PartyDocumentConverter {

    @Override
    protected PartyRole roleToSearchFor() {
        return PartyRole.BUYER;
    }
}
