package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.SalesPartyType;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public abstract class SalesPartyTypeListConverter extends CustomConverter<List<SalesPartyType>, String> {

    protected abstract String roleToSearchFor();

    @Override
    public String convert(List<SalesPartyType> salesParties, Type<? extends String> destinationType, MappingContext mappingContext) {
        for (SalesPartyType salesParty : salesParties) {
            if (!everyNecessaryFieldIsNotNull(salesParty)) { continue; }

            String role = salesParty.getRoleCodes().get(0).getValue();

            if (roleToSearchFor().equalsIgnoreCase(role)) {
                return salesParty.getName().getValue();
            }

            //Mapping recipient
            if (salesParty.getRoleCodes().get(0).getValue().equals("RECIPIENT")) {
                return salesParty.getName().getValue();
            }
        }

        return "N/A";
    }

    private boolean everyNecessaryFieldIsNotNull(SalesPartyType salesParty) {
        return CollectionUtils.isNotEmpty(salesParty.getRoleCodes())
                && salesParty.getRoleCodes().get(0) != null
                && salesParty.getRoleCodes().get(0).getValue() != null
                && salesParty.getName() != null
                && salesParty.getName().getValue() != null;
    }
}
