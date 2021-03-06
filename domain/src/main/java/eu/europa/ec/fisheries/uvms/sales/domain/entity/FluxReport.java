package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "sales_flux_report")
@SequenceGenerator( name = "sales_flux_report_id_seq",
        sequenceName = "sales_flux_report_id_seq",
        allocationSize = 50)
@EqualsAndHashCode(exclude = {"relatedTakeOverDocuments", "relatedSalesNotes"})
@ToString(exclude = {"relatedTakeOverDocuments", "relatedSalesNotes"})
@NamedQueries({
        @NamedQuery(name = FluxReport.FIND_BY_EXT_ID, query = "SELECT report from FluxReport report WHERE lower(report.extId) = lower(:extId)"),
        @NamedQuery(name = FluxReport.FIND_BY_REFERENCED_ID_AND_PURPOSE,
                query = "SELECT report from FluxReport report " +
                        "WHERE lower(report.previousFluxReportExtId) = lower(:extId) " +
                        "and report.purpose = :purpose"),
        @NamedQuery(name = FluxReport.FIND_TOD_BY_EXT_ID, query = "SELECT report from FluxReport report WHERE lower(report.extId) = lower(:extId) AND report.itemType = eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType.TAKE_OVER_DOCUMENT")
})
public class FluxReport {

    public static final String FIND_BY_EXT_ID = "FluxReport.FIND_BY_EXT_ID";
    public static final String FIND_BY_REFERENCED_ID_AND_PURPOSE = "FluxReport.FIND_BY_REFERENCED_ID_AND_PURPOSE";
    public static final String FIND_TOD_BY_EXT_ID = "FluxReport.FIND_TOD_BY_REFERRED_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_flux_report_id_seq")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "ext_id", nullable = false)
    private String extId;

    @NotNull
    @Column(name = "purpose_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private Purpose purpose;

    @Column(name = "purpose_text")
    private String purposeText;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    private FluxReportItemType itemType;

    @NotNull
    @Column(name = "creation", nullable = false)
    private DateTime creation;

    @Valid
    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_auction_sale_id")
    private AuctionSale auctionSale;

    @Column(name = "flux_report_party_id")
    private String fluxReportPartyId;

    @Column(name = "flux_report_party_name")
    private String fluxReportPartyName;

    @Valid
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_document_id")
    private Document document;

    /** Date on which this report is deleted **/
    @Column(name = "deletion")
    private DateTime deletion;

    /** Date on which this report is corrected by another report **/
    @Column(name = "correction")
    private DateTime correction;

    /**
     * When this report is a correction or deletion of another report, the attribute previousFluxReport will point
     * to the extId of the report that is being corrected or deleted.
     */
    @Column(name = "sales_flux_report_prev_ext_id")
    private String previousFluxReportExtId;

    /**
     * When this is a sales note, this attribute will contain all related take over documents.
     * When this is a take over document, this attribute will be an empty list.
     */
    @Valid
    @ManyToMany
    @JoinTable( name = "sales_note_take_over_document_relation",
            joinColumns = @JoinColumn(name = "sales_note_id"),
            inverseJoinColumns = @JoinColumn(name = "take_over_document_id"))
    private List<FluxReport> relatedTakeOverDocuments;

    /**
     * When this is a sales note, this attribute will be an empty list.
     * When this is a take over document, this attribute will contain all related sales notes.
     */
    @Valid
    @ManyToMany
    @JoinTable( name = "sales_note_take_over_document_relation",
            joinColumns = @JoinColumn(name = "take_over_document_id"),
            inverseJoinColumns = @JoinColumn(name = "sales_note_id"))
    private List<FluxReport> relatedSalesNotes;


    /**
     * This property is filled with DateTime.now() when receiving the FluxReport.
     * This differs from the 'creation' property which is filled by the sending party
     */
    @NotNull
    @Column(name = "received_on")
    private DateTime receivedOn;


    public FluxReport() {
        // Set the date on which this report was created
        this.receivedOn(DateTime.now());
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

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

    public FluxReportItemType getItemType() {
        return itemType;
    }

    public void setItemType(FluxReportItemType itemType) {
        this.itemType = itemType;
    }

    public DateTime getCreation() {
        return creation;
    }

    public void setCreation(DateTime creation) {
        this.creation = creation;
    }

    public AuctionSale getAuctionSale() {
        return auctionSale;
    }

    public void setAuctionSale(AuctionSale auctionSale) {
        this.auctionSale = auctionSale;
    }

    public String getFluxReportPartyId() {
        return fluxReportPartyId;
    }

    public void setFluxReportPartyId(String fluxReportPartyId) {
        this.fluxReportPartyId = fluxReportPartyId;
    }

    public String getFluxReportPartyName() {
        return fluxReportPartyName;
    }

    public void setFluxReportPartyName(String fluxReportPartyName) {
        this.fluxReportPartyName = fluxReportPartyName;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getPreviousFluxReportExtId() {
        return previousFluxReportExtId;
    }

    public void setPreviousFluxReportExtId(String previousFluxReport) {
        this.previousFluxReportExtId = previousFluxReport;
    }

    public List<FluxReport> getRelatedTakeOverDocuments() {
        return relatedTakeOverDocuments;
    }

    public void setRelatedTakeOverDocuments(List<FluxReport> relatedSalesReports) {
        this.relatedTakeOverDocuments = relatedSalesReports;
    }

    public List<FluxReport> getRelatedSalesNotes() {
        return relatedSalesNotes;
    }

    public void setRelatedSalesNotes(List<FluxReport> relatedSalesNotes) {
        this.relatedSalesNotes = relatedSalesNotes;
    }

    public DateTime getDeletion() {
        return deletion;
    }

    public void setDeletion(DateTime deletion) {
        this.deletion = deletion;
    }

    public DateTime getCorrection() {
        return correction;
    }

    public void setCorrection(DateTime correction) {
        this.correction = correction;
    }


    public DateTime getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(DateTime receivedOn) {
        this.receivedOn = receivedOn;
    }


    public FluxReport extId(final String extId) {
        setExtId(extId);
        return this;
    }

    public FluxReport purpose(final Purpose purpose) {
        setPurpose(purpose);
        return this;
    }

    public FluxReport purposeText(final String purposeText) {
        setPurposeText(purposeText);
        return this;
    }

    public FluxReport itemType(final FluxReportItemType itemType) {
        setItemType(itemType);
        return this;
    }

    public FluxReport creation(final DateTime creation) {
        setCreation(creation);
        return this;
    }

    public FluxReport auctionSale(final AuctionSale auctionSale) {
        setAuctionSale(auctionSale);
        return this;
    }

    public FluxReport fluxReportPartyId(final String fluxReportPartyId) {
        setFluxReportPartyId(fluxReportPartyId);
        return this;
    }

    public FluxReport fluxReportPartyName(final String fluxReportPartyName) {
        setFluxReportPartyName(fluxReportPartyName);
        return this;
    }

    public FluxReport document(final Document document) {
        setDocument(document);
        return this;
    }

    public FluxReport previousFluxReportExtId(final String previousFluxReportExtId) {
        setPreviousFluxReportExtId(previousFluxReportExtId);
        return this;
    }

    public FluxReport id(Integer id) {
        this.id = id;
        return this;
    }

    public FluxReport relatedTakeOverDocuments(List<FluxReport> relatedTakeOverDocuments) {
        this.relatedTakeOverDocuments = relatedTakeOverDocuments;
        return this;
    }

    public FluxReport relatedSalesNotes(List<FluxReport> relatedSalesNotes) {
        this.relatedSalesNotes = relatedSalesNotes;
        return this;
    }

    public FluxReport deletion(DateTime deletion) {
        this.deletion = deletion;
        return this;
    }

    public FluxReport correction(DateTime correction) {
        this.correction = correction;
        return this;
    }

    public boolean isCorrected() {
        return correction != null;
    }

    public boolean isDeleted() {
        return deletion != null;
    }

    public FluxReport receivedOn(DateTime receivedOn) {
        this.receivedOn = receivedOn;
        return this;
    }
}
