package eu.europa.ec.fisheries.uvms.sales.service.constants;

public enum MDRCodeListKey {

    CONVERSION_FACTOR("CONVERSION_FACTOR"),
    FLAG_STATES("TERRITORY"),
    SALES_LOCATIONS("LOCATION"),
    FRESHNESS("FISH_FRESHNESS"),
    PRESENTATIONS("FISH_PRESENTATION"),
    PRESERVATIONS("FISH_PRESERVATION"),
    DISTRIBUTION_CLASSES("FISH_SIZE_CLASS"),
    USAGES("PROD_USAGE"),
    CURRENCIES("TERRITORY_CURR"),
    SPECIES("FAO_SPECIES"),
    MARKET_AND_STORAGE_LOCATIONS("LOCATION");


    private String internalName;

    MDRCodeListKey(String internalName) {
        this.internalName = internalName;
    }

    public String getInternalName() {
        return internalName;
    }
}
