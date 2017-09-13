package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;


@Entity
@Table(name = "sales_query_parameter")
@SequenceGenerator(name = "sales_query_parameter_id_seq_generator",
        sequenceName = "sales_query_parameter_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class QueryParameterType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_query_parameter_id_seq_generator")
    @Column(name = "id")
    private Integer id;

    @Column(name = "type_code", nullable = false)
    private String typeCode;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "value_date_time")
    private DateTime valueDateTime;

    @Column(name = "value_id")
    private String valueID;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "sales_query_id", nullable = false)
    private Query query;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public DateTime getValueDateTime() {
        return valueDateTime;
    }

    public void setValueDateTime(DateTime valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

    public String getValueID() {
        return valueID;
    }

    public void setValueID(String valueID) {
        this.valueID = valueID;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }


    public QueryParameterType id(Integer id) {
        this.id = id;
        return this;
    }

    public QueryParameterType typeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public QueryParameterType valueCode(String valueCode) {
        this.valueCode = valueCode;
        return this;
    }

    public QueryParameterType valueDateTime(DateTime valueDateTime) {
        this.valueDateTime = valueDateTime;
        return this;
    }

    public QueryParameterType valueID(String valueID) {
        this.valueID = valueID;
        return this;
    }

    public QueryParameterType query(Query query) {
        this.query = query;
        return this;
    }
}
