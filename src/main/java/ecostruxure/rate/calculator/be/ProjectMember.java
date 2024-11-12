package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.util.UUID;

public class ProjectMember {
    private UUID teamId;
    private UUID projectId;
    private String name;
    private BigDecimal projectAllocation;
    private BigDecimal markup;
    private BigDecimal dayRate;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getProjectAllocation() {
        return projectAllocation;
    }

    public void setProjectAllocation(BigDecimal projectAllocation) {
        this.projectAllocation = projectAllocation;
    }

    public BigDecimal getMarkup() {
        return markup;
    }

    public void setMarkup(BigDecimal markup) {
        this.markup = markup;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }
}
