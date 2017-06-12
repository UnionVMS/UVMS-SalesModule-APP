package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SalesDetailsDto {

    private FishingTripDto fishingTrip;

    private SalesReportDto salesReport;

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


    public SalesDetailsDto fishingTrip(FishingTripDto fishingTrip) {
        this.fishingTrip = fishingTrip;
        return this;
    }

    public SalesDetailsDto salesReport(SalesReportDto salesReport) {
        this.salesReport = salesReport;
        return this;
    }
}
