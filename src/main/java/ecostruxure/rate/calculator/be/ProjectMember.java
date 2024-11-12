package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.util.UUID;

public class ProjectMember {
    private UUID teamId;
    private UUID projectId;
    private String teamName;
    private BigDecimal projectAllocation;
    private BigDecimal teamDayRateWithMarkup;

    public ProjectMember() {}

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public BigDecimal getProjectAllocation() {
        return projectAllocation;
    }

    public void setProjectAllocation(BigDecimal projectAllocation) {
        this.projectAllocation = projectAllocation;
    }

    public BigDecimal getTeamDayRateWithMarkup() {
        return teamDayRateWithMarkup;
    }

    public void setTeamDayRateWithMarkup(BigDecimal teamDayRateWithMarkup) {
        this.teamDayRateWithMarkup = teamDayRateWithMarkup;
    }
}
