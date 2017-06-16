package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxProcessType;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

/**
 * Created by MATBUL on 16/02/2017.
 */
public abstract class ProcessTypeConverter extends CustomConverter<List<CodeType>, String> {

    private void checkIfProcessTypeIsValid(String toBeValidated) {
        FluxProcessType.forString(toBeValidated);
    }

    abstract FluxProcessType getProcessType();

    @Override
    public String convert(List<CodeType> processTypes, Type<? extends String> destinationType, MappingContext mappingContext) {
        for (CodeType processType : processTypes) {
            String processTypeAsString = processType.getListID();
            checkIfProcessTypeIsValid(processTypeAsString);

            if (processTypeAsString.equals(getProcessType().toString())) {
                return processType.getValue();
            }
        }
        return null;
    }
}
