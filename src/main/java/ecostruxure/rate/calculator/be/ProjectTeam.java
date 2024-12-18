package ecostruxure.rate.calculator.be;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class ProjectTeam {

    @Id
    @GeneratedValue
    private UUID projectTeamId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPercentage;

    //region Getters and Setters
    public UUID getProjectTeamId() {
        return projectTeamId;
    }

    public void setProjectTeamId(UUID projectTeamId) {
        this.projectTeamId = projectTeamId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public BigDecimal getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(BigDecimal allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }
    //endregion
}
