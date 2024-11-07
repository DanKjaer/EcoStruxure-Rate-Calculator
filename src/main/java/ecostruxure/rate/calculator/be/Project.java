package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Project {
    private UUID projectId;
    private String projectName;
    private String projectDescription;
    private List<Profile> projectMembers;
    private BigDecimal projectCost;
    private BigDecimal projectMargin;
    private BigDecimal projectPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    public Project() {}

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<Profile> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<Profile> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public BigDecimal getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(BigDecimal projectCost) {
        this.projectCost = projectCost;
    }

    public BigDecimal getProjectMargin() {
        return projectMargin;
    }

    public void setProjectMargin(BigDecimal projectMargin) {
        this.projectMargin = projectMargin;
    }

    public BigDecimal getProjectPrice() {
        return projectPrice;
    }

    public void setProjectPrice(BigDecimal projectPrice) {
        this.projectPrice = projectPrice;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
