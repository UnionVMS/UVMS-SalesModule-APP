package eu.europa.ec.fisheries.uvms.sales.domain.entity;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sales_query")
@SequenceGenerator(name = "sales_query_id_seq_generator",
        sequenceName = "sales_query_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
@NamedQueries({
        @NamedQuery(name = Query.FIND_BY_EXT_ID, query = "SELECT query from Query query WHERE query.extId = :extId"),
})
public class Query {

    public static final String FIND_BY_EXT_ID = "query.findByExtId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_query_id_seq_generator")
    @Column(name = "id")
    private Integer id;

    @Column(name = "ext_id", nullable = false)
    private String extId;

    @Column(name = "submitted_date", nullable = false)
    private DateTime submittedDate;

    @Column(name = "query_type", nullable = false)
    private String queryType;

    @Column(name = "start_date")
    private DateTime startDate;

    @Column(name = "end_date")
    private DateTime endDate;

    @Column(name = "submitted_by")
    private String submitterFLUXParty;

    @OneToMany(mappedBy = "query", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<QueryParameterType> parameters;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public DateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(DateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getSubmitterFLUXParty() {
        return submitterFLUXParty;
    }

    public void setSubmitterFLUXParty(String submitterFLUXParty) {
        this.submitterFLUXParty = submitterFLUXParty;
    }

    public List<QueryParameterType> getParameters() {
        return parameters;
    }

    public void setParameters(List<QueryParameterType> parameters) {
        this.parameters = parameters;
    }


    public Query id(Integer id) {
        this.id = id;
        return this;
    }

    public Query extId(String extId) {
        this.extId = extId;
        return this;
    }

    public Query submittedDate(DateTime submittedDate) {
        this.submittedDate = submittedDate;
        return this;
    }

    public Query queryType(String queryType) {
        this.queryType = queryType;
        return this;
    }

    public Query startDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public Query endDate(DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public Query submitterFLUXParty(String submitterFLUXParty) {
        this.submitterFLUXParty = submitterFLUXParty;
        return this;
    }

    public Query parameters(List<QueryParameterType> parameters) {
        this.parameters = parameters;
        return this;
    }
}
