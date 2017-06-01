package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class SalesDetailsDto {

    private FishingTripDto fishingTrip;

    private SalesNoteDto salesNote;

    /**
     * If this report is a correction or deletion, all previous
     * versions of this report are put in this variable. Also,
     * sales documents are related to take over documents.
     * This relation will also be reflected here.
     */
    private List<SalesDetailsRelation> relatedReports;

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

    public List<SalesDetailsRelation> getRelatedReports() {
        return relatedReports;
    }

    public void setRelatedReports(List<SalesDetailsRelation> relatedReports) {
        this.relatedReports = relatedReports;
    }

    public SalesDetailsDto fishingTrip(FishingTripDto fishingTrip) {
        this.fishingTrip = fishingTrip;
        return this;
    }

    public SalesDetailsDto salesNote(SalesNoteDto salesNote) {
        this.salesNote = salesNote;
        return this;
    }

    public SalesDetailsDto relatedReports(List<SalesDetailsRelation> relatedReportsExtIds) {
        this.relatedReports = relatedReportsExtIds;
        return this;
    }
}
