package eu.europa.ec.fisheries.uvms.sales.service.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

@EqualsAndHashCode
@ToString
public class DocumentDto {
    private String extId;
    private String currency;
    private DateTime occurrence;

    public DocumentDto() {
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DateTime getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(DateTime occurrence) {
        this.occurrence = occurrence;
    }

    public DocumentDto extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public DocumentDto currency(final String currency) {
        setCurrency(currency);
        return this;
    }

    public DocumentDto occurrence(final DateTime occurrence) {
        setOccurrence(occurrence);
        return this;
    }

}
