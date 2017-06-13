package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.model.constant.Purpose;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

// TODO: copied these files from the converter in sales-db because we need this here too.
public class PurposeConverter extends BidirectionalConverter<Purpose, CodeType> {

    @Override
    public CodeType convertTo(Purpose purpose, Type<CodeType> type, MappingContext mappingContext) {
        if (purpose == null) {
            return null;
        } else {
            return new CodeType().withValue(Integer.toString(purpose.getNumericCode()));
        }
    }

    @Override
    public Purpose convertFrom(CodeType codeType, Type<Purpose> type, MappingContext mappingContext) {
        if (codeType == null) {
            return null;
        } else {
            return Purpose.forNumericCode(Integer.parseInt(codeType.getValue()));
        }
    }
}
