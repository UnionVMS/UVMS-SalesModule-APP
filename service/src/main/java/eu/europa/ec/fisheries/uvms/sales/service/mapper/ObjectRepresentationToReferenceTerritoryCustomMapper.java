package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceTerritory;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class ObjectRepresentationToReferenceTerritoryCustomMapper extends CustomMapper<ObjectRepresentation, ReferenceTerritory> {

    @Override
    public void mapAtoB(ObjectRepresentation objectRepresentation, ReferenceTerritory referenceTerritory, MappingContext context) {
        if (objectRepresentation == null || isEmpty(objectRepresentation.getFields())) {
            return;
        }

        List<ColumnDataType> fields = objectRepresentation.getFields();
        for(ColumnDataType field: fields) {
            mapField(field, referenceTerritory);
        }
    }

    private void mapField(ColumnDataType field, ReferenceTerritory referenceTerritory) {
        if (field.getColumnName() != null) {
            switch (field.getColumnName()) {
                case "code":
                    referenceTerritory.setCode(field.getColumnValue());
                    break;
                case "enName":
                    referenceTerritory.setEnglishName(field.getColumnValue());
                    break;
            }
        }
    }
}
