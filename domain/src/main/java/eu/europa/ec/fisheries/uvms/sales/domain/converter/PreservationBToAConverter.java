package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxProcessType;

/**
 * Created by MATBUL on 17/02/2017.
 */
public class PreservationBToAConverter extends ProductToAAPProductProcessConverter {
    @Override
    FluxProcessType getProcessType() {
        return FluxProcessType.FISH_PRESERVATION;
    }
}
