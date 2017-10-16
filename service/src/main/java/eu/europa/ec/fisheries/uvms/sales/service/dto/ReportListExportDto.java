package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * Represents one item in the list of reports to be exported.
 * The properties are defined in the order that the values in CSV format are printed.
 *
 *
 * Why public fields? This DTO can be transformed to a CSV. In order to do that, reflection is used.
 * Via reflection, it's possible to make private fields accessible and read the contents.
 *
 * However, when running with Java agents, like Jacoco, it's possible that these classes are "enriched": extra
 * private attributes are being added. This messes up the functionality (and the tests).
 *
 * The quickest solution to avoid this problem, is to use public fields only. Ugly, I know. :(
 */
@EqualsAndHashCode
@ToString
public class ReportListExportDto {

    /**
     * flag state of the vessel
     */
    public String flagState; //NOSONAR

    /**
     * external marking (CFR) of the vessel
     */
    public String externalMarking; //NOSONAR

    /**
     * IRCS of the vessel
     */
    public String ircs; //NOSONAR

    public String vesselName; //NOSONAR

    /**
     * timestamp of the sale
     */
    public String occurrence; //NOSONAR

    /**
     * location of the sale
     */
    public String location; //NOSONAR
    public String landingDate; //NOSONAR
    public String landingPort; //NOSONAR
    public SalesCategoryType category; //NOSONAR

    /**
     * provider name
     */
    public String provider; //NOSONAR

    /**
     * buyer name
     */
    public String buyer; //NOSONAR

    public DateTime deletion; //NOSONAR

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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public ReportListExportDto provider(String provider) {
        this.provider = provider;
        return this;
    }

    public ReportListExportDto deletion(DateTime deletion) {
        this.deletion = deletion;
        return this;
    }
}
