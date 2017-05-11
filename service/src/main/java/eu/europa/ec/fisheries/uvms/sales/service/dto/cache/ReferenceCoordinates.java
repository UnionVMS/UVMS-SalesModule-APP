package eu.europa.ec.fisheries.uvms.sales.service.dto.cache;

public class ReferenceCoordinates {
    private String locationCode;
    private Double latitude;
    private Double longitude;

    public ReferenceCoordinates(String locationCode, Double latitude, Double longitude) {
        this.locationCode = locationCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
