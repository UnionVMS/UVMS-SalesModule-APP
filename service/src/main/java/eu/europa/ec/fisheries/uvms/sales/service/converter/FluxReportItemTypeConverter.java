package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.model.constant.FluxReportItemType;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/** Duplicated code for now: this class also exists in DB. When APP and DB are merged, this is no longer an issue. **/
public class FluxReportItemTypeConverter extends BidirectionalConverter<FluxReportItemType, CodeType> {

    @Override
    public CodeType convertTo(FluxReportItemType itemType, Type<CodeType> type, MappingContext mappingContext) {
        if (itemType == null) {
            return null;
        } else {
            return new CodeType().withValue(itemType.getCode());
        }
    }

    @Override
    public FluxReportItemType convertFrom(CodeType codeType, Type<FluxReportItemType> type, MappingContext mappingContext) {
        if (codeType == null) {
            return null;
        } else {
            return FluxReportItemType.forCode(codeType.getValue());
        }
    }
}
