package eu.europa.ec.fisheries.uvms.sales.service.converter;


import eu.europa.ec.fisheries.uvms.sales.service.constants.FluxProcessType;

public class FreshnessConverter extends ProcessTypeConverter {

    @Override
    FluxProcessType getProcessType() {
        return FluxProcessType.FISH_FRESHNESS;
    }
}
