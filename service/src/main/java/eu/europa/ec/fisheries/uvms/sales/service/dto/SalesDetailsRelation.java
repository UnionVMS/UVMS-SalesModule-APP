package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.uvms.sales.model.constant.FluxReportItemType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SalesDetailsRelation {

    private String reportExtId;
    private String documentExtId;
    private FluxReportItemType type;


    public String getReportExtId() {
        return reportExtId;
    }

    public void setReportExtId(String reportExtId) {
        this.reportExtId = reportExtId;
    }

    public FluxReportItemType getType() {
        return type;
    }

    public void setType(FluxReportItemType type) {
        this.type = type;
    }

    public String getDocumentExtId() {
        return documentExtId;
    }

    public void setDocumentExtId(String documentExtId) {
        this.documentExtId = documentExtId;
    }

    public SalesDetailsRelation type(FluxReportItemType type) {
        this.type = type;
        return this;
    }

    public SalesDetailsRelation reportExtId(String reportExtId) {
        this.reportExtId = reportExtId;
        return this;
    }

    public SalesDetailsRelation documentExtId(String documentExtId) {
        this.documentExtId = documentExtId;
        return this;
    }
}
