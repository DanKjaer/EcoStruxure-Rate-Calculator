package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Project {
    private UUID projectId;
    private String projectName;
    private String projectSalesNumber;
    private String projectDescription;
    private List<ProjectMember> projectMembers;
    private BigDecimal projectDayRate;
    private BigDecimal ProjectGrossMargin;
    private BigDecimal projectPrice;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private int projectTotalDays;
    private Geography projectLocation;
    private Boolean projectArchived;

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

    public String getProjectSalesNumber() {
        return projectSalesNumber;
    }

    public void setProjectSalesNumber(String projectSalesNumber) {
        this.projectSalesNumber = projectSalesNumber;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<ProjectMember> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<ProjectMember> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public BigDecimal getProjectDayRate() {
        return projectDayRate;
    }

    public void setProjectDayRate(BigDecimal projectDayRate) {
        this.projectDayRate = projectDayRate;
    }

    public BigDecimal getProjectGrossMargin() {
        return ProjectGrossMargin;
    }

    public void setProjectGrossMargin(BigDecimal projectGrossMargin) {
        ProjectGrossMargin = projectGrossMargin;
    }

    public BigDecimal getProjectPrice() {
        return projectPrice;
    }

    public void setProjectPrice(BigDecimal projectPrice) {
        this.projectPrice = projectPrice;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public LocalDate getProjectEndDate() {
        return projectEndDate;
    }

    public void setProjectEndDate(LocalDate projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public int getProjectTotalDays() {
        return projectTotalDays;
    }

    public void setProjectTotalDays(int projectTotalDays) {
        this.projectTotalDays = projectTotalDays;
    }

    public Geography getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(Geography projectLocation) {
        this.projectLocation = projectLocation;
    }

    public Boolean getProjectArchived() {
        return projectArchived;
    }

    public void setProjectArchived(Boolean projectArchived) {
        this.projectArchived = projectArchived;
    }
}
