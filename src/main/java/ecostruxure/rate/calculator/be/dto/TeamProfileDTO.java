package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamProfileDTO {
    private UUID id;
    private TeamDTO team;
    private ProfileDTO profile;
    private BigDecimal allocationPercentageHours;
    private BigDecimal allocatedHours;
    private BigDecimal allocationPercentageCost;
    private BigDecimal allocatedCost;

    public TeamProfileDTO(UUID id,
                          TeamDTO team,
                          ProfileDTO profile,
                          BigDecimal allocationPercentageHours,
                          BigDecimal allocatedHours,
                          BigDecimal allocationPercentageCost,
                          BigDecimal allocatedCost) {
        this.id = id;
        this.team = team;
        this.profile = profile;
        this.allocationPercentageHours = allocationPercentageHours;
        this.allocatedHours = allocatedHours;
        this.allocationPercentageCost = allocationPercentageCost;
        this.allocatedCost = allocatedCost;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
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
}
