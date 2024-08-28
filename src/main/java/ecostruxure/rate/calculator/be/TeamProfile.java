package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamProfile {
    private UUID teamId;
    private UUID profileId;
    private String name;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal annualCost;
    private BigDecimal annualTotalCost;
    private BigDecimal costAllocation;
    private BigDecimal hourAllocation;
    private BigDecimal annualTotalHours;
    private BigDecimal allocatedCostOnTeam;

    public TeamProfile(UUID teamId, UUID profileId, String name, BigDecimal dayRate, BigDecimal annualCost, BigDecimal annualTotalCost, BigDecimal costAllocation, BigDecimal hourAllocation, BigDecimal annualTotalHours, BigDecimal allocatedCostOnTeam) {
        this.teamId = teamId;
        this.profileId = profileId;
        this.name = name;
        this.dayRate = dayRate;
        this.annualCost = annualCost;
        this.annualTotalCost = annualTotalCost;
        this.costAllocation = costAllocation;
        this.hourAllocation = hourAllocation;
        this.annualTotalHours = annualTotalHours;
        this.allocatedCostOnTeam = allocatedCostOnTeam;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public BigDecimal getAnnualCost() {
        return annualCost;
    }

    public BigDecimal getAnnualTotalCost() {
        return annualTotalCost;
    }

    public BigDecimal getCostAllocation() {
        return costAllocation;
    }

    public BigDecimal getHourAllocation() {
        return hourAllocation;
    }

    public BigDecimal getAnnualTotalHours() {
        return annualTotalHours;
    }

    public BigDecimal getAllocatedCostOnTeam() {
        return allocatedCostOnTeam;
    }
}
