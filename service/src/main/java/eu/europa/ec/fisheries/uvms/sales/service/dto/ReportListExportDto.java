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
    public String _flagState; //NOSONAR

    /**
     * external marking (CFR) of the vessel
     */
    public String _externalMarking; //NOSONAR

    /**
     * IRCS of the vessel
     */
    public String _ircs; //NOSONAR

    public String _vesselName; //NOSONAR

    /**
     * timestamp of the sale
     */
    public String _occurrence; //NOSONAR

    /**
     * location of the sale
     */
    public String _location; //NOSONAR
    public String _landingDate; //NOSONAR
    public String _landingPort; //NOSONAR
    public SalesCategoryType _category; //NOSONAR

    /**
     * provider name
     */
    public String _provider; //NOSONAR

    /**
     * buyer name
     */
    public String _buyer; //NOSONAR

    public DateTime _deletion; //NOSONAR

    public String getFlagState() {
        return _flagState;
    }

    public void setFlagState(String flagState) {
        this._flagState = flagState;
    }

    public String getExternalMarking() {
        return _externalMarking;
    }

    public void setExternalMarking(String externalMarking) {
        this._externalMarking = externalMarking;
    }

    public String getIrcs() {
        return _ircs;
    }

    public void setIrcs(String ircs) {
        this._ircs = ircs;
    }

    public String getVesselName() {
        return _vesselName;
    }

    public void setVesselName(String vesselName) {
        this._vesselName = vesselName;
    }


    public String getOccurrence() {
        return _occurrence;
    }

    public void setOccurrence(String occurrence) {
        this._occurrence = occurrence;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        this._location = location;
    }

    public String getLandingDate() {
        return _landingDate;
    }

    public void setLandingDate(String landingDate) {
        this._landingDate = landingDate;
    }

    public String getLandingPort() {
        return _landingPort;
    }

    public void setLandingPort(String landingPort) {
        this._landingPort = landingPort;
    }

    public SalesCategoryType getCategory() {
        return _category;
    }

    public void setCategory(SalesCategoryType category) {
        this._category = category;
    }

    public String getBuyer() {
        return _buyer;
    }

    public void setBuyer(String buyer) {
        this._buyer = buyer;
    }

    public String getProvider() {
        return _provider;
    }

    public void setProvider(String provider) {
        this._provider = provider;
    }

    public DateTime getDeletion() {
        return _deletion;
    }

    public void setDeletion(DateTime deletion) {
        this._deletion = deletion;
    }

    public ReportListExportDto flagState(String flagState) {
        this._flagState = flagState;
        return this;
    }

    public ReportListExportDto externalMarking(String externalMarking) {
        this._externalMarking = externalMarking;
        return this;
    }

    public ReportListExportDto ircs(String ircs) {
        this._ircs = ircs;
        return this;
    }

    public ReportListExportDto vesselName(String vesselName) {
        this._vesselName = vesselName;
        return this;
    }


    public ReportListExportDto occurrence(String occurrence) {
        this._occurrence = occurrence;
        return this;
    }

    public ReportListExportDto location(String location) {
        this._location = location;
        return this;
    }

    public ReportListExportDto landingDate(String landingDate) {
        this._landingDate = landingDate;
        return this;
    }

    public ReportListExportDto landingPort(String landingPort) {
        this._landingPort = landingPort;
        return this;
    }

    public ReportListExportDto category(SalesCategoryType category) {
        this._category = category;
        return this;
    }

    public ReportListExportDto buyer(String buyer) {
        this._buyer = buyer;
        return this;
    }

    public ReportListExportDto provider(String provider) {
        this._provider = provider;
        return this;
    }

    public ReportListExportDto deletion(DateTime deletion) {
        this._deletion = deletion;
        return this;
    }
}
