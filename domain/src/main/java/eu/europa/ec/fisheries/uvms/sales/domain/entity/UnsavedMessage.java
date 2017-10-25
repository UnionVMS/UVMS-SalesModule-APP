package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "sales_unsaved_message")
@SequenceGenerator(name = "sales_unsaved_message_id_seq",
        sequenceName = "sales_unsaved_message_id_seq",
        allocationSize = 50)
@NamedQueries({
        @NamedQuery(name = UnsavedMessage.FIND, query = "SELECT unsavedMessage from UnsavedMessage unsavedMessage WHERE unsavedMessage.extId = :extId")
})
@EqualsAndHashCode
@ToString
public class UnsavedMessage {

    public static final String FIND = "UnsavedMessage.FIND";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_unsaved_message_id_seq")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ext_id", nullable = false)
    private String extId;


    public UnsavedMessage() {}

    public UnsavedMessage(String extId) {
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
