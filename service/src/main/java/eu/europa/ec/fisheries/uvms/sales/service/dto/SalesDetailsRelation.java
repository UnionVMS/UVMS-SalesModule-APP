package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

@EqualsAndHashCode
@ToString
public class SalesDetailsRelation {

    private String reportExtId;
    private String documentExtId;
    private DateTime creationDate;
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

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
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

    public SalesDetailsRelation creationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}
