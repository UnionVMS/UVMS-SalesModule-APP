package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

@EqualsAndHashCode
@ToString
public class FluxReportDto {
    private String extId;
    private DateTime creation;
    private String purposeCode;
    private String purposeText;
    private String fluxReportParty;

    public FluxReportDto() {
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public DateTime getCreation() {
        return creation;
    }

    public void setCreation(DateTime creation) {
        this.creation = creation;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

    public String getFluxReportParty() {
        return fluxReportParty;
    }

    public void setFluxReportParty(String fluxReportParty) {
        this.fluxReportParty = fluxReportParty;
    }

    public FluxReportDto extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public FluxReportDto creation(final DateTime creation) {
        setCreation(creation);
        return this;
    }

    public FluxReportDto purposeCode(final String purposeCode) {
        setPurposeCode(purposeCode);
        return this;
    }

    public FluxReportDto purposeText(final String purposeText) {
        setPurposeText(purposeText);
        return this;
    }

    public FluxReportDto fluxReportParty(final String fluxReportParty) {
        setFluxReportParty(fluxReportParty);
        return this;
    }
}
