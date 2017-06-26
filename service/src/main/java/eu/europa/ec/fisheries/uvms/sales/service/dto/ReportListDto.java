package eu.europa.ec.fisheries.uvms.sales.service.dto;

import com.google.common.base.Strings;
import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Represents one item in the list of reports
 */
@EqualsAndHashCode
@ToString
public class ReportListDto {

    /**
     * report ext id
     */
    private String extId;

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
    private String vesselExtId;

    /**
     * timestamp of the sale
     */
    private DateTime occurrence;

    /**
     * location of the sale
     */
    private String location;
    private DateTime landingDate;
    private String landingPort;
    private SalesCategoryType category;

    /**
     * buyer name
     */
    private String buyer;

    /**
     * seller name
     */
    private String seller;

    /**
     * recipient name
     */
    private String recipient;

    /**
     * If this report is a correction or deletion, all previous
     * versions of this report are put in this variable.
     */
    private List<ReportListDto> olderVersions;

    /**
     * ExtId/GUID of the previous report. Only filled in if this
     * is a corrected or deleted report, or a take over document.
     */
    private String referencedId;

    private String purpose;

    private DateTime deletion;


    public ReportListDto() {
        seller = "N/A";
        buyer = "N/A";
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

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

    public String getVesselExtId() {
        return vesselExtId;
    }

    public void setVesselExtId(String vesselExtId) {
        this.vesselExtId = vesselExtId;
    }

    public DateTime getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(DateTime occurrence) {
        this.occurrence = occurrence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DateTime getLandingDate() {
        return landingDate;
    }

    public void setLandingDate(DateTime landingDate) {
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
        if (buyer == null) {
            buyer = "N/A";
        }
        this.buyer = buyer;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        if (seller == null) {
            seller = "N/A";
        }
        this.seller = seller;
    }

    public List<ReportListDto> getOlderVersions() {
        return olderVersions;
    }

    public void setOlderVersions(List<ReportListDto> olderVersions) {
        this.olderVersions = olderVersions;
    }

    public String getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(String referencedId) {
        this.referencedId = referencedId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }


    public DateTime getDeletion() {
        return deletion;
    }

    public void setDeletion(DateTime deletion) {
        this.deletion = deletion;
    }

    public ReportListDto extId(String extId) {
        this.extId = extId;
        return this;
    }

    public ReportListDto flagState(String flagState) {
        this.flagState = flagState;
        return this;
    }

    public ReportListDto externalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
        return this;
    }

    public ReportListDto ircs(String ircs) {
        this.ircs = ircs;
        return this;
    }

    public ReportListDto vesselName(String vesselName) {
        this.vesselName = vesselName;
        return this;
    }

    public ReportListDto vesselExtId(String vesselExtId) {
        this.vesselExtId = vesselExtId;
        return this;
    }

    public ReportListDto occurrence(DateTime occurrence) {
        this.occurrence = occurrence;
        return this;
    }

    public ReportListDto location(String location) {
        this.location = location;
        return this;
    }

    public ReportListDto landingDate(DateTime landingDate) {
        this.landingDate = landingDate;
        return this;
    }

    public ReportListDto landingPort(String landingPort) {
        this.landingPort = landingPort;
        return this;
    }

    public ReportListDto category(SalesCategoryType category) {
        this.category = category;
        return this;
    }

    public ReportListDto buyer(String buyer) {
        this.setBuyer(buyer);
        return this;
    }

    public ReportListDto seller(String seller) {
        this.setSeller(seller);
        return this;
    }

    public ReportListDto referencedId(String referencedId) {
        this.setReferencedId(referencedId);
        return this;
    }

    public boolean hasReferencedId() {
        return !Strings.isNullOrEmpty(referencedId);
    }

    public ReportListDto olderVersions(List<ReportListDto> olderVersions) {
        this.olderVersions = olderVersions;
        return this;
    }

    public ReportListDto purpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public ReportListDto deletion(DateTime deletion) {
        this.deletion = deletion;
        return this;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
