package ecostruxure.rate.calculator.be;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "teamProfileId")
public class TeamProfile {

    @Id
    @GeneratedValue
    private UUID teamProfileId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference(value = "team-teamProfiles")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference(value = "profile-teamProfiles")
    private Profile profile;

    // Percentage of time allocated
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPercentageHours; // Percentage of hours (e.g., 75%)

    // Actual allocated hours for this profile on this team
    @Column(precision = 15, scale = 2)
    private BigDecimal allocatedHours; // Derived: annual_hours * allocationPercentageHours / 100

    // Percentage of cost allocated
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPercentageCost; // Percentage of cost (e.g., 50%)

    // Pre-calculated allocated cost for this team-profile relationship
    @Column(precision = 15, scale = 2)
    private BigDecimal allocatedCost; // Derived: annual_cost * allocationPercentageCost / 100

    //region Getters and Setters
    public UUID getTeamProfileId() {
        return teamProfileId;
    }

    public void setTeamProfileId(UUID teamProfileId) {
        this.teamProfileId = teamProfileId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public BigDecimal getAllocationPercentageHours() {
        return allocationPercentageHours;
    }

    public void setAllocationPercentageHours(BigDecimal allocationPercentageHours) {
        this.allocationPercentageHours = allocationPercentageHours;
    }

    public BigDecimal getAllocatedHours() {
        return allocatedHours;
    }

    public void setAllocatedHours(BigDecimal allocatedHours) {
        this.allocatedHours = allocatedHours;
    }

    public BigDecimal getAllocationPercentageCost() {
        return allocationPercentageCost;
    }

    public void setAllocationPercentageCost(BigDecimal allocationPercentageCost) {
        this.allocationPercentageCost = allocationPercentageCost;
    }

    public BigDecimal getAllocatedCost() {
        return allocatedCost;
    }

    public void setAllocatedCost(BigDecimal allocatedCost) {
        this.allocatedCost = allocatedCost;
    }
    //endregion
}