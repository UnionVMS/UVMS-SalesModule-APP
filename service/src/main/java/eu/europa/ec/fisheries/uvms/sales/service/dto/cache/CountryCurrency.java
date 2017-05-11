package eu.europa.ec.fisheries.uvms.sales.service.dto.cache;

public class CountryCurrency {
    private String countryCode;
    private String currency;

    public CountryCurrency(String countryCode, String currency) {
        this.countryCode = countryCode;
        this.currency = currency;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
