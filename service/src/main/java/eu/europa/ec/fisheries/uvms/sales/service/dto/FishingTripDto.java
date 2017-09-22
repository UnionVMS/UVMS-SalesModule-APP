package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

@EqualsAndHashCode
@ToString
public class FishingTripDto {

    private String extId;

    private String vesselName;

    private String vesselCFR;

    private String vesselGuid;

    private String landingLocation;

    private DateTime landingDate;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getVesselCFR() {
        return vesselCFR;
    }

    public void setVesselCFR(String vesselCFR) {
        this.vesselCFR = vesselCFR;
    }

    public String getVesselGuid() {
        return vesselGuid;
    }

    public void setVesselGuid(String vesselGuid) {
        this.vesselGuid = vesselGuid;
    }

    public String getLandingLocation() {
        return landingLocation;
    }

    public void setLandingLocation(String landingLocation) {
        this.landingLocation = landingLocation;
    }

    public DateTime getLandingDate() {
        return landingDate;
    }

    public void setLandingDate(DateTime landingDate) {
        this.landingDate = landingDate;
    }

    public FishingTripDto extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public FishingTripDto vesselName(final String vesselName) {
        setVesselName(vesselName);
        return this;
    }

    public FishingTripDto vesselCFR(final String vesselCFR) {
        setVesselCFR(vesselCFR);
        return this;
    }

    public FishingTripDto vesselGuid(final String vesselGuid) {
        setVesselGuid(vesselGuid);
        return this;
    }

    public FishingTripDto landingLocation(final String landingLocation) {
        setLandingLocation(landingLocation);
        return this;
    }

    public FishingTripDto landingDate(final DateTime landingDate) {
        setLandingDate(landingDate);
        return this;
    }

}
