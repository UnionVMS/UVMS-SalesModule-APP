package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.FLUXLocationType;
import eu.europa.ec.fisheries.schema.sales.IDType;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.ArrayList;
import java.util.List;

public class ListFLUXLocationTypeConverter extends BidirectionalConverter<List<FLUXLocationType>, List<String>> {

    @Override
    public List<String> convertTo(List<FLUXLocationType> source, Type<List<String>> destinationType, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (FLUXLocationType fluxLocationType : source) {
            IDType countryID = fluxLocationType.getCountryID();
            if (countryID != null) {
                result.add(countryID.getValue());
            }
        }
        return result;
    }

    @Override
    public List<FLUXLocationType> convertFrom(List<String> source, Type<List<FLUXLocationType>> destinationType, MappingContext mappingContext) {
        throw new UnsupportedOperationException();
    }
}
