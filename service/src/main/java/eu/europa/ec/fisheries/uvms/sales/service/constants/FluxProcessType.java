package eu.europa.ec.fisheries.uvms.sales.service.constants;

/**
 * Created by MATBUL on 16/02/2017.
 */
public enum FluxProcessType {
    FISH_PRESENTATION,
    FISH_PRESERVATION,
    FISH_FRESHNESS;

    public static FluxProcessType forString(String input) {
        for (FluxProcessType processType : FluxProcessType.values()) {
            if (processType.toString().equals(input)) {
                return processType;
            }
        }
        throw new IllegalArgumentException("No process type exists for string " + input);
    }
}
