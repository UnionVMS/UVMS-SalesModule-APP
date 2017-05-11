package eu.europa.ec.fisheries.uvms.sales.service.converter;


import eu.europa.ec.fisheries.uvms.sales.service.constants.FluxProcessType;

/**
 * Created by MATBUL on 17/02/2017.
 */
public class FreshnessBToAConverter extends ProductToAAPProductProcessConverter {
    @Override
    FluxProcessType getProcessType() {
        return FluxProcessType.FISH_FRESHNESS;
    }
}
