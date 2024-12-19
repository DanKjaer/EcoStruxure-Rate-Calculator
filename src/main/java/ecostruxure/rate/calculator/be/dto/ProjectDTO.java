package ecostruxure.rate.calculator.be.dto;

import ecostruxure.rate.calculator.be.Geography;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProjectDTO {
    private UUID projectId;
    private String projectName;
    private String projectDescription;
    private List<ProjectTeamDTO> projectTeams;
    private BigDecimal projectDayRate;
    private BigDecimal projectGrossMargin;
    private BigDecimal projectPrice;
    private BigDecimal projectTotalCostAtChange;
    private int projectTotalDays;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private Geography projectLocation;
    private String projectSalesNumber;
    private boolean projectArchived;

    public ProjectDTO(UUID projectId,
                      String projectName,
                      String projectDescription,
                      List<ProjectTeamDTO> projectTeams,
                      BigDecimal projectDayRate,
                      BigDecimal projectGrossMargin,
                      BigDecimal projectPrice,
                      BigDecimal projectTotalCostAtChange,
                      int projectTotalDays,
                      LocalDate projectStartDate,
                      LocalDate projectEndDate,
                      Geography projectLocation,
                      String projectSalesNumber,
                      boolean projectArchived) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectTeams = projectTeams;
        this.projectDayRate = projectDayRate;
        this.projectGrossMargin = projectGrossMargin;
        this.projectPrice = projectPrice;
        this.projectTotalCostAtChange = projectTotalCostAtChange;
        this.projectTotalDays = projectTotalDays;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
        this.projectLocation = projectLocation;
        this.projectSalesNumber = projectSalesNumber;
        this.projectArchived = projectArchived;
    }

    public ProjectDTO() {
    }

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

    public List<ProjectTeamDTO> getProjectTeams() {
        return projectTeams;
    }

    public void setProjectTeams(List<ProjectTeamDTO> projectTeams) {
        this.projectTeams = projectTeams;
    }

    public BigDecimal getProjectDayRate() {
        return projectDayRate;
    }

    public void setProjectDayRate(BigDecimal projectDayRate) {
        this.projectDayRate = projectDayRate;
    }

    public BigDecimal getProjectGrossMargin() {
        return projectGrossMargin;
    }

    public void setProjectGrossMargin(BigDecimal projectGrossMargin) {
        this.projectGrossMargin = projectGrossMargin;
    }

    public BigDecimal getProjectPrice() {
        return projectPrice;
    }

    public void setProjectPrice(BigDecimal projectPrice) {
        this.projectPrice = projectPrice;
    }

    public BigDecimal getProjectTotalCostAtChange() {
        return projectTotalCostAtChange;
    }

    public void setProjectTotalCostAtChange(BigDecimal projectTotalCostAtChange) {
        this.projectTotalCostAtChange = projectTotalCostAtChange;
    }

    public int getProjectTotalDays() {
        return projectTotalDays;
    }

    public void setProjectTotalDays(int projectTotalDays) {
        this.projectTotalDays = projectTotalDays;
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

    public Geography getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(Geography projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectSalesNumber() {
        return projectSalesNumber;
    }

    public void setProjectSalesNumber(String projectSalesNumber) {
        this.projectSalesNumber = projectSalesNumber;
    }

    public boolean isProjectArchived() {
        return projectArchived;
    }

    public void setProjectArchived(boolean projectArchived) {
        this.projectArchived = projectArchived;
    }
}
