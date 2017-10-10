package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "sales_erroneous_message")
@SequenceGenerator(name = "sales_erroneous_message_id_seq",
        sequenceName = "sales_erroneous_message_id_seq",
        allocationSize = 50)
@NamedQueries({
        @NamedQuery(name = ErroneousMessage.FIND, query = "SELECT erroneousMessage from ErroneousMessage erroneousMessage WHERE erroneousMessage.extId = :extId")
})
@EqualsAndHashCode
@ToString
public class ErroneousMessage {

    public static final String FIND = "ErroneousMessage.FIND";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_erroneous_message_id_seq")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ext_id", nullable = false)
    private String extId;


    public ErroneousMessage() {}

    public ErroneousMessage(String extId) {
        this.extId = extId;
    }


    public Integer getId() {
        return id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

}
