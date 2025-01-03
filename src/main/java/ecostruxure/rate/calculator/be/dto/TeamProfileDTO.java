package ecostruxure.rate.calculator.be.dto;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamProfileDTO {
    private UUID teamProfileId;
    private Team team;
    private Profile profile;
    private BigDecimal allocationPercentageHours;
    private BigDecimal allocatedHours;
    private BigDecimal allocationPercentageCost;
    private BigDecimal allocatedCost;

    public TeamProfileDTO(UUID teamProfileId,
                          Team team,
                          Profile profile,
                          BigDecimal allocationPercentageHours,
                          BigDecimal allocatedHours,
                          BigDecimal allocationPercentageCost,
                          BigDecimal allocatedCost) {
        this.teamProfileId = teamProfileId;
        this.team = team;
        this.profile = profile;
        this.allocationPercentageHours = allocationPercentageHours;
        this.allocatedHours = allocatedHours;
        this.allocationPercentageCost = allocationPercentageCost;
        this.allocatedCost = allocatedCost;
    }

    //empty constructor for model mapping
    public TeamProfileDTO() {
    }

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
}
