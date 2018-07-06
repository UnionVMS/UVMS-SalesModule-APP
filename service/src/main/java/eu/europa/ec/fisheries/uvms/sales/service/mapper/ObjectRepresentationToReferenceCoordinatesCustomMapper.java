package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ObjectRepresentationToReferenceCoordinatesCustomMapper extends CustomMapper<ObjectRepresentation, ReferenceCoordinates> {

    @Override
    public void mapAtoB(ObjectRepresentation objectRepresentation, ReferenceCoordinates referenceCoordinates, MappingContext context) {
        if (objectRepresentation == null || isEmpty(objectRepresentation.getFields())) {
            return;
        }

        List<ColumnDataType> fields = objectRepresentation.getFields();
        for(ColumnDataType field: fields) {
            mapField(field, referenceCoordinates);
        }
    }

    private void mapField(ColumnDataType field, ReferenceCoordinates referenceCoordinates) {
        if (isNotEmpty(field.getColumnName()) && isNotEmpty(field.getColumnValue())) {
            switch (field.getColumnName()) {
                case "unloCode":
                    referenceCoordinates.setLocationCode(field.getColumnValue());
                    break;
                case "latitude":
                    referenceCoordinates.setLatitude(Double.parseDouble(field.getColumnValue()));
                    break;
                case "longitude":
                    referenceCoordinates.setLongitude(Double.parseDouble(field.getColumnValue()));
                    break;
                default:
                    break;
            }
        }
    }
}
