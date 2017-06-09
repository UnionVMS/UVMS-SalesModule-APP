package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class SalesDetailsDto {

    private FishingTripDto fishingTrip;

    private SalesReportDto salesReport;

    /**
     * If this report is the latest version, the previous versions are kept here.
     * If this report is an older version (a correction/deletion exists), the latest version is kept here.
     */
    private List<SalesDetailsRelation> otherVersions;

    /**
     * The related take over document (in case that this is a sales note)
     * or the related sales note (in case that this is a take over document),
     * in case there is any.
     */
    private SalesDetailsRelation relatedReport;

    public FishingTripDto getFishingTrip() {
        return fishingTrip;
    }

    public void setFishingTrip(FishingTripDto fishingTrip) {
        this.fishingTrip = fishingTrip;
    }

    public SalesReportDto getSalesReport() {
        return salesReport;
    }

    public void setSalesReport(SalesReportDto salesReport) {
        this.salesReport = salesReport;
    }

    public List<SalesDetailsRelation> getOtherVersions() {
        return otherVersions;
    }

    public void setOtherVersions(List<SalesDetailsRelation> otherVersions) {
        this.otherVersions = otherVersions;
    }

    public SalesDetailsRelation getRelatedReport() {
        return relatedReport;
    }

    public void setRelatedReport(SalesDetailsRelation relatedReport) {
        this.relatedReport = relatedReport;
    }

    public SalesDetailsDto fishingTrip(FishingTripDto fishingTrip) {
        this.fishingTrip = fishingTrip;
        return this;
    }

    public SalesDetailsDto salesNote(SalesReportDto salesNote) {
        this.salesReport = salesNote;
        return this;
    }

    public SalesDetailsDto otherVersions(List<SalesDetailsRelation> otherVersions) {
        this.otherVersions = otherVersions;
        return this;
    }

    public SalesDetailsDto relatedReport(SalesDetailsRelation relatedReport) {
        this.relatedReport = relatedReport;
        return this;
    }
}
