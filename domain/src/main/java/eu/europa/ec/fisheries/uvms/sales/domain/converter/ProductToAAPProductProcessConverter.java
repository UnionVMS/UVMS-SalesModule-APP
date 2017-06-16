package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxProcessType;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public abstract class ProductToAAPProductProcessConverter extends CustomConverter<String, CodeType> {

    abstract FluxProcessType getProcessType();


    @Override
    public CodeType convert(String source, Type<? extends CodeType> destinationType, MappingContext mappingContext) {
        return new CodeType().withListID(getProcessType().toString()).withValue(source);
    }
}
