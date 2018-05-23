package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sales_response")
@SequenceGenerator(name = "sales_response_id_seq_generator",
        sequenceName = "sales_response_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
@NamedQueries({
        @NamedQuery(name = Response.FIND_BY_EXT_ID, query = "SELECT response from Response response WHERE lower(response.extId) = lower(:extId)"),
        @NamedQuery(name = Response.FIND_BY_REFERENCED_ID, query = "SELECT response from Response response WHERE lower(response.referencedId) = lower(:referencedId)")
})
public class Response {

    public static final String FIND_BY_EXT_ID = "response.findByExtId";
    public static final String FIND_BY_REFERENCED_ID = "response.findByReferencedId";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_response_id_seq_generator")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "ext_id", nullable = false)
    private String extId;

    @NotNull
    @Column(name = "referenced_id", nullable = false)
    private String referencedId;

    @NotNull
    @Column(name = "creation", nullable = false)
    private DateTime creationDateTime;

    @NotNull
    @Column(name = "response_code", nullable = false)
    private String responseCode;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "respondent_flux_party")
    private String respondentFLUXParty;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public DateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(DateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getRespondentFLUXParty() {
        return respondentFLUXParty;
    }

    public void setRespondentFLUXParty(String respondentFLUXParty) {
        this.respondentFLUXParty = respondentFLUXParty;
    }

    public Response extId(String extId) {
        this.extId = extId;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(String referencedId) {
        this.referencedId = referencedId;
    }


    public Response creationDateTime(DateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }

    public Response responseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public Response remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public Response rejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public Response typeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public Response respondentFLUXParty(String respondentFLUXParty) {
        this.respondentFLUXParty = respondentFLUXParty;
        return this;
    }

    public Response id(Integer id) {
        this.id = id;
        return this;
    }

    public Response referencedId(String referencedId) {
        this.referencedId = referencedId;
        return this;
    }
}
