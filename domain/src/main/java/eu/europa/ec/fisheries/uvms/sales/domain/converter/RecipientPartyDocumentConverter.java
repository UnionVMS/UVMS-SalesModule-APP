package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;

public class RecipientPartyDocumentConverter extends PartyDocumentConverter {

    @Override
    protected PartyRole roleToSearchFor() {
        return PartyRole.RECIPIENT;
    }
}
