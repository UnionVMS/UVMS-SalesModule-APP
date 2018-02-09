package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.PartyRole;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.PartyDocument;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

public abstract class PartyDocumentConverter extends CustomConverter<List<PartyDocument>, String> {

    protected abstract PartyRole roleToSearchFor();

    @Override
    public String convert(List<PartyDocument> partyDocuments, Type<? extends String> destinationType, MappingContext mappingContext) {
        for (PartyDocument partyDocument : partyDocuments) {
            if (everyNecessaryFieldIsNotNull(partyDocument) && roleToSearchFor() == partyDocument.getRole()) {
                return partyDocument.getParty().getName();
            }
        }

        return "N/A";
    }

    private boolean everyNecessaryFieldIsNotNull(PartyDocument partyDocument) {
        return partyDocument.getRole() != null
                && partyDocument.getParty() != null
                && partyDocument.getParty().getName() != null;
    }
}
