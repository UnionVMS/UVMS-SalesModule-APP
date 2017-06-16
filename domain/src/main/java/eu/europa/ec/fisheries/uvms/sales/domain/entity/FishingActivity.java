package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "sales_fishing_activity")
@SequenceGenerator(name = "sales_fishing_activity_id_seq",
        sequenceName = "sales_fishing_activity_id_seq",
        allocationSize = 50)
@EqualsAndHashCode
@ToString
public class FishingActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sales_fishing_activity_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "ext_id")
    private String extId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "union_trip_id", nullable = false)
    private String fishingTripId;

    @Column(name = "start_date", nullable = false)
    private DateTime startDate;

    @Column(name = "end_date", nullable = false)
    private DateTime endDate;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_vessel_id", nullable = false)
    private Vessel vessel;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sales_flux_location_id", nullable = false)
    private FluxLocation location;

    public FishingActivity() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFishingTripId() {
        return fishingTripId;
    }

    public void setFishingTripId(String fishingTripId) {
        this.fishingTripId = fishingTripId;
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

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public FluxLocation getLocation() {
        return location;
    }

    public void setLocation(FluxLocation location) {
        this.location = location;
    }

    public FishingActivity id(Integer id) {
        this.id = id;
        return this;
    }

    public FishingActivity extId(String extId) {
        this.extId = extId;
        return this;
    }

    public FishingActivity type(String type) {
        this.type = type;
        return this;
    }

    public FishingActivity fishingTripId(String fishingTripId) {
        this.fishingTripId = fishingTripId;
        return this;
    }

    public FishingActivity startDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public FishingActivity endDate(DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public FishingActivity vessel(Vessel vessel) {
        this.vessel = vessel;
        return this;
    }

    public FishingActivity location(FluxLocation location) {
        this.location = location;
        return this;
    }
}
