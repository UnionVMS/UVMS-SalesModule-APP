package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sales_flux_report")
@SequenceGenerator( name = "sales_flux_report_id_seq",
        sequenceName = "sales_flux_report_id_seq",
        allocationSize = 50)
@EqualsAndHashCode(exclude = {"previousFluxReport", "relatedTakeOverDocuments", "relatedSalesNotes"})
@ToString(exclude = {"previousFluxReport", "relatedTakeOverDocuments", "relatedSalesNotes"})
@NamedQueries({
        @NamedQuery(name = FluxReport.FIND_BY_EXT_ID, query = "SELECT report from FluxReport report WHERE report.extId = :extId"),
        @NamedQuery(name = FluxReport.FIND_BY_REFERRED_ID, query = "SELECT report from FluxReport report WHERE report.previousFluxReport.extId = :extId"),
        @NamedQuery(name = FluxReport.FIND_TOD_BY_EXT_ID, query = "SELECT report from FluxReport report WHERE report.extId = :extId AND report.itemType = eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType.TAKE_OVER_DOCUMENT")
})
public class FluxReport {

    public static final String FIND_BY_EXT_ID = "FluxReport.FIND_BY_EXT_ID";
    public static final String FIND_BY_REFERRED_ID = "FluxReport.FIND_BY_REFERRED_ID";
    public static final String FIND_TOD_BY_EXT_ID = "FluxReport.FIND_TOD_BY_REFERRED_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_flux_report_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "ext_id", nullable = false)
    private String extId;

    @Column(name = "purpose_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private Purpose purpose;

    @Column(name = "purpose_text")
    private String purposeText;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    private FluxReportItemType itemType;

    @Column(name = "creation", nullable = false)
    private DateTime creation;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_auction_sale_id")
    private AuctionSale auctionSale;

    @Column(name = "flux_report_party")
    private String fluxReportParty;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_document_id")
    private Document document;

    @Column(name = "deletion")
    private DateTime deletion;

    /**
     * When this report is a correction or deletion of another report, the attribute previousFluxReport will point
     * to the report that is being corrected or deleted.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_flux_report_prev_id")
    private FluxReport previousFluxReport;

    /**
     * When this is a sales note, this attribute will contain all related take over documents.
     * When this is a take over document, this attribute will be an empty list.
     */
    @ManyToMany
    @JoinTable( name = "sales_note_take_over_document_relation",
                joinColumns = @JoinColumn(name = "sales_note_id"),
                inverseJoinColumns = @JoinColumn(name = "take_over_document_id"))
    private List<FluxReport> relatedTakeOverDocuments;

    /**
     * When this is a sales note, this attribute will be an empty list.
     * When this is a take over document, this attribute will contain all related sales notes.
     */
    @ManyToMany
    @JoinTable( name = "sales_note_take_over_document_relation",
            joinColumns = @JoinColumn(name = "take_over_document_id"),
            inverseJoinColumns = @JoinColumn(name = "sales_note_id"))
    private List<FluxReport> relatedSalesNotes;

    public FluxReport() {
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

    public String getFluxReportParty() {
        return fluxReportParty;
    }

    public void setFluxReportParty(String fluxReportParty) {
        this.fluxReportParty = fluxReportParty;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public FluxReport getPreviousFluxReport() {
        return previousFluxReport;
    }

    public void setPreviousFluxReport(FluxReport previousFluxReport) {
        this.previousFluxReport = previousFluxReport;
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

    public FluxReport fluxReportParty(final String fluxReportParty) {
        setFluxReportParty(fluxReportParty);
        return this;
    }

    public FluxReport document(final Document document) {
        setDocument(document);
        return this;
    }

    public FluxReport previousFluxReport(final FluxReport previousFluxReport) {
        setPreviousFluxReport(previousFluxReport);
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
}