package eu.europa.ec.fisheries.uvms.sales.service.config;

public enum ParameterKey {

    ECB_ENDPOINT("sales.ebc.proxy.endpoint"),
    CURRENCY("currency");

    private final String key;

    private ParameterKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
