package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamProfile {
    private Profile profile;
    private Team team;
    private UUID teamId;
    private UUID profileId;
    private String name;
    private BigDecimal dayRateOnTeam;
    private BigDecimal costAllocation;
    private BigDecimal hourAllocation;
    private BigDecimal allocatedCostOnTeam;
    private BigDecimal allocatedHoursOnTeam;

    public TeamProfile() {

    }
    public TeamProfile(UUID teamId, UUID profileId, String name, BigDecimal dayRateOnTeam,
                       BigDecimal costAllocation, BigDecimal hourAllocation,
                       BigDecimal allocatedCostOnTeam, BigDecimal allocatedHoursOnTeam) {
        this.teamId = teamId;
        this.profileId = profileId;
        this.name = name;
        this.dayRateOnTeam = dayRateOnTeam;
        this.costAllocation = costAllocation;
        this.hourAllocation = hourAllocation;
        this.allocatedCostOnTeam = allocatedCostOnTeam;
        this.allocatedHoursOnTeam = allocatedHoursOnTeam;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
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

    public BigDecimal getDayRateOnTeam() {
        return dayRateOnTeam;
    }

    public BigDecimal getCostAllocation() {
        return costAllocation;
    }

    public BigDecimal getHourAllocation() {
        return hourAllocation;
    }

    public BigDecimal getAllocatedCostOnTeam() {
        return allocatedCostOnTeam;
    }

    public BigDecimal getAllocatedHoursOnTeam() {
        return allocatedHoursOnTeam;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDayRateOnTeam(BigDecimal dayRateOnTeam) {
        this.dayRateOnTeam = dayRateOnTeam;
    }

    public void setCostAllocation(BigDecimal costAllocation) {
        this.costAllocation = costAllocation;
    }

    public void setHourAllocation(BigDecimal hourAllocation) {
        this.hourAllocation = hourAllocation;
    }

    public void setAllocatedCostOnTeam(BigDecimal allocatedCostOnTeam) {
        this.allocatedCostOnTeam = allocatedCostOnTeam;
    }

    public void setAllocatedHoursOnTeam(BigDecimal allocatedHoursOnTeam) {
        this.allocatedHoursOnTeam = allocatedHoursOnTeam;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team){
        this.team = team;
    }
}
