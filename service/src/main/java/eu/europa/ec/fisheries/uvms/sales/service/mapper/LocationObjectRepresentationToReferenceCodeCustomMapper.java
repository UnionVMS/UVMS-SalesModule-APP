package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocationObjectRepresentationToReferenceCodeCustomMapper extends ObjectRepresentationToReferenceCodeCustomMapper {

    @Override
    protected String columnNameForCode() {
        return "unlo_code";
    }

    public List<ReferenceCode> mapAsList(Collection<ObjectRepresentation> locations) {
        List<ReferenceCode> mappedLocations = new ArrayList<>();

        LocationObjectRepresentationToReferenceCodeCustomMapper locationMapper = new LocationObjectRepresentationToReferenceCodeCustomMapper();
        for (ObjectRepresentation location : locations) {
            ReferenceCode referenceCode = new ReferenceCode();
            locationMapper.mapAtoB(location, referenceCode, null);
            mappedLocations.add(referenceCode);
        }

        return mappedLocations;
    }

}
