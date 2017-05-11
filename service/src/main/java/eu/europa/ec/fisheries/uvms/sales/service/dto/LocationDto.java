package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LocationDto {

    private String extId;
    private Double longitude;
    private Double latitude;
    private String country;

    public LocationDto() {
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocationDto extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public LocationDto longitude(final Double longitude) {
        setLongitude(longitude);
        return this;
    }

    public LocationDto latitude(final Double latitude) {
        setLatitude(latitude);
        return this;
    }

    public LocationDto country(final String country) {
        setCountry(country);
        return this;
    }


}
