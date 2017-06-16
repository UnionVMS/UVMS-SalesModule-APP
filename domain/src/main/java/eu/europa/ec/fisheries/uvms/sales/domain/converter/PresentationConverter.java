package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxProcessType;

public class PresentationConverter extends ProcessTypeConverter {

    @Override
    FluxProcessType getProcessType() {
        return FluxProcessType.FISH_PRESENTATION;
    }
}
