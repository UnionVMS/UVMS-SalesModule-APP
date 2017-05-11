package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SalesDetailsDto {

    private FishingTripDto fishingTrip;

    private SalesNoteDto salesNote;

    public FishingTripDto getFishingTrip() {
        return fishingTrip;
    }

    public void setFishingTrip(FishingTripDto fishingTrip) {
        this.fishingTrip = fishingTrip;
    }

    public SalesNoteDto getSalesNote() {
        return salesNote;
    }

    public void setSalesNote(SalesNoteDto salesNote) {
        this.salesNote = salesNote;
    }

    public SalesDetailsDto fishingTrip(FishingTripDto fishingTrip) {
        this.fishingTrip = fishingTrip;
        return this;
    }

    public SalesDetailsDto salesNote(SalesNoteDto salesNote) {
        this.salesNote = salesNote;
        return this;
    }
}
