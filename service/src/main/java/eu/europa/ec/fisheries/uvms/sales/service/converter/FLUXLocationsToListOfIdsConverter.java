package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.FLUXLocationType;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FLUXLocationsToListOfIdsConverter extends BidirectionalConverter<Collection<FLUXLocationType>, List<String>> {

    @Override
    public List<String> convertTo(Collection<FLUXLocationType> fluxLocations, Type<List<String>> destinationType, MappingContext mappingContext) {
        List<String> ids = new ArrayList<>();
        if (CollectionUtils.isEmpty(fluxLocations)) {
            return ids;
        }

        for (FLUXLocationType fluxLocation : fluxLocations) {
            if (fluxLocation.getID() != null && StringUtils.isNotBlank(fluxLocation.getID().getValue())) {
                ids.add(fluxLocation.getID().getValue());
            }
        }
        return ids;
    }

    @Override
    public Collection<FLUXLocationType> convertFrom(List<String> source, Type<Collection<FLUXLocationType>> destinationType, MappingContext mappingContext) {
        throw new UnsupportedOperationException();
    }
}
