package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProjectTeamDTO {
    private UUID projectTeamId;
    private TeamDTO team;
    private BigDecimal allocationPercentage;

    public ProjectTeamDTO(UUID projectTeamId, TeamDTO team, BigDecimal allocationPercentage) {
        this.projectTeamId = projectTeamId;
        this.team = team;
        this.allocationPercentage = allocationPercentage;
    }

    public ProjectTeamDTO() {
    }

    public UUID getProjectTeamId() {
        return projectTeamId;
    }

    public void setProjectTeamId(UUID projectTeamId) {
        this.projectTeamId = projectTeamId;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public BigDecimal getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(BigDecimal allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }
}
