package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ConversionFactor;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceTerritory;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class ObjectRepresentationToConversionFactorCustomMapper extends CustomMapper<ObjectRepresentation, ConversionFactor> {

    @Override
    public void mapAtoB(ObjectRepresentation objectRepresentation, ConversionFactor conversionFactor, MappingContext context) {
        if (objectRepresentation == null || isEmpty(objectRepresentation.getFields())) {
            return;
        }

        List<ColumnDataType> fields = objectRepresentation.getFields();
        for (ColumnDataType field : fields) {
            mapField(field, conversionFactor);
        }
    }

    private void mapField(ColumnDataType field, ConversionFactor conversionFactor) {
        if (field.getColumnName() != null) {
            String columnValue = field.getColumnValue();
            switch (field.getColumnName()) {
                case "code":
                    conversionFactor.setCode(columnValue);
                    break;
                case "factor":
                    conversionFactor.setFactor(new BigDecimal(columnValue));
                    break;
                case "state":
                    conversionFactor.setPreservation(columnValue);
                    break;
                case "presentation":
                    conversionFactor.setPresentation(columnValue);
                    break;
            }
        }
    }
}
