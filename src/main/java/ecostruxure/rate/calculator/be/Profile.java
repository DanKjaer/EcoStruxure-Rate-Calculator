package ecostruxure.rate.calculator.be;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "profileId")
public class Profile {

    @Id
    @GeneratedValue
    private UUID profileId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "geography_id")
    private Geography geography;

    private Boolean resourceType;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal annualCost;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal annualHours;

    @Column(precision = 15, scale = 2)
    private BigDecimal hoursPerDay;

    @Column(precision = 5, scale = 2)
    private BigDecimal effectivenessPercentage;

    @Column(precision = 15, scale = 2)
    private BigDecimal effectiveWorkHours;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalCostAllocation;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalHourAllocation;

    private boolean archived;

    private Timestamp updatedAt;

    // Relationships
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TeamProfile> teamProfiles;

    //region Getters and Setters
    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geography getGeography() {
        return geography;
    }

    public void setGeography(Geography geography) {
        this.geography = geography;
    }

    public Boolean getResourceType() {
        return resourceType;
    }

    public void setResourceType(Boolean resourceType) {
        this.resourceType = resourceType;
    }

    public BigDecimal getAnnualCost() {
        return annualCost;
    }

    public void setAnnualCost(BigDecimal annualCost) {
        this.annualCost = annualCost;
    }

    public BigDecimal getAnnualHours() {
        return annualHours;
    }

    public void setAnnualHours(BigDecimal annualHours) {
        this.annualHours = annualHours;
    }

    public BigDecimal getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(BigDecimal hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public BigDecimal getEffectivenessPercentage() {
        return effectivenessPercentage;
    }

    public void setEffectivenessPercentage(BigDecimal effectivenessPercentage) {
        this.effectivenessPercentage = effectivenessPercentage;
    }

    public BigDecimal getEffectiveWorkHours() {
        return effectiveWorkHours;
    }

    public void setEffectiveWorkHours(BigDecimal effectiveWorkHours) {
        this.effectiveWorkHours = effectiveWorkHours;
    }

    public BigDecimal getTotalCostAllocation() {
        return totalCostAllocation;
    }

    public void setTotalCostAllocation(BigDecimal totalCostAllocation) {
        this.totalCostAllocation = totalCostAllocation;
    }

    public BigDecimal getTotalHourAllocation() {
        return totalHourAllocation;
    }

    public void setTotalHourAllocation(BigDecimal totalHourAllocation) {
        this.totalHourAllocation = totalHourAllocation;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TeamProfile> getTeamProfiles() {
        return teamProfiles;
    }

    public void setTeamProfiles(List<TeamProfile> teamProfiles) {
        this.teamProfiles = teamProfiles;
    }
    //endregion
}