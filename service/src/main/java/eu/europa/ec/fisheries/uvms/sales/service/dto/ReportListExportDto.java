package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * Represents one item in the list of reports to be exported.
 * The properties are defined in the order that the values in CSV format are printed.
 */
@EqualsAndHashCode
@ToString
public class ReportListExportDto {

    /**
     * flag state of the vessel
     */
    private String flagState;

    /**
     * external marking (CFR) of the vessel
     */
    private String externalMarking;

    /**
     * IRCS of the vessel
     */
    private String ircs;

    private String vesselName;

    /**
     * timestamp of the sale
     */
    private String occurrence;

    /**
     * location of the sale
     */
    private String location;
    private String landingDate;
    private String landingPort;
    private SalesCategoryType category;

    /**
     * seller name
     */
    private String seller;

    /**
     * buyer name
     */
    private String buyer;

    private DateTime deletion;

    public String getFlagState() {
        return flagState;
    }

    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

    public String getExternalMarking() {
        return externalMarking;
    }

    public void setExternalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
    }

    public String getIrcs() {
        return ircs;
    }

    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }


    public String getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLandingDate() {
        return landingDate;
    }

    public void setLandingDate(String landingDate) {
        this.landingDate = landingDate;
    }

    public String getLandingPort() {
        return landingPort;
    }

    public void setLandingPort(String landingPort) {
        this.landingPort = landingPort;
    }

    public SalesCategoryType getCategory() {
        return category;
    }

    public void setCategory(SalesCategoryType category) {
        this.category = category;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public DateTime getDeletion() {
        return deletion;
    }

    public void setDeletion(DateTime deletion) {
        this.deletion = deletion;
    }

    public ReportListExportDto flagState(String flagState) {
        this.flagState = flagState;
        return this;
    }

    public ReportListExportDto externalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
        return this;
    }

    public ReportListExportDto ircs(String ircs) {
        this.ircs = ircs;
        return this;
    }

    public ReportListExportDto vesselName(String vesselName) {
        this.vesselName = vesselName;
        return this;
    }


    public ReportListExportDto occurrence(String occurrence) {
        this.occurrence = occurrence;
        return this;
    }

    public ReportListExportDto location(String location) {
        this.location = location;
        return this;
    }

    public ReportListExportDto landingDate(String landingDate) {
        this.landingDate = landingDate;
        return this;
    }

    public ReportListExportDto landingPort(String landingPort) {
        this.landingPort = landingPort;
        return this;
    }

    public ReportListExportDto category(SalesCategoryType category) {
        this.category = category;
        return this;
    }

    public ReportListExportDto buyer(String buyer) {
        this.buyer = buyer;
        return this;
    }

    public ReportListExportDto seller(String seller) {
        this.seller = seller;
        return this;
    }

    public ReportListExportDto deletion(DateTime deletion) {
        this.deletion = deletion;
        return this;
    }
}
