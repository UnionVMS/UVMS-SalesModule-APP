package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.uvms.sales.model.constant.FluxReportItemType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

@EqualsAndHashCode
@ToString
public class SalesDetailsRelation {

    private String extId;
    private FluxReportItemType type;
    private DateTime date;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public FluxReportItemType getType() {
        return type;
    }

    public void setType(FluxReportItemType type) {
        this.type = type;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public SalesDetailsRelation extId(String extId) {
        this.extId = extId;
        return this;
    }

    public SalesDetailsRelation type(FluxReportItemType type) {
        this.type = type;
        return this;
    }

    public SalesDetailsRelation date(DateTime date) {
        this.date = date;
        return this;
    }
}
