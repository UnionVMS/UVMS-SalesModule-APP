package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class ObjectRepresentationToReferenceCodeCustomMapper extends CustomMapper<ObjectRepresentation, ReferenceCode> {

    @Override
    public void mapAtoB(ObjectRepresentation objectRepresentation, ReferenceCode referenceCode, MappingContext context) {
        if (objectRepresentation == null || isEmpty(objectRepresentation.getFields())) {
            return;
        }

        List<ColumnDataType> fields = objectRepresentation.getFields();
        for(ColumnDataType field: fields) {
            mapField(field, referenceCode);
        }
    }

    private void mapField(ColumnDataType field, ReferenceCode referenceCode) {
        if (field.getColumnName() != null) {
            switch (field.getColumnName()) {
                case "code":
                    referenceCode.setCode(field.getColumnValue());
                    break;
                case "description":
                    referenceCode.setText(field.getColumnValue());
                    break;
            }
        }
    }
}
