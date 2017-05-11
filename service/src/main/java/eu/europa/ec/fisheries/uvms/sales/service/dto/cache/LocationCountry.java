package eu.europa.ec.fisheries.uvms.sales.service.dto.cache;

public class LocationCountry {
    private String locationId;
    private String countryId;

    public LocationCountry(String locationId, String countryId) {
        this.locationId = locationId;
        this.countryId = countryId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
