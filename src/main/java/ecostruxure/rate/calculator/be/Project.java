package ecostruxure.rate.calculator.be;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
public class Project {

    @Id
    @GeneratedValue
    private UUID projectId;

    @Column(nullable = false)
    private String projectName;

    private String projectDescription;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTeam> projectTeams;

    @Column(precision = 15, scale = 2)
    private BigDecimal projectDayRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal projectGrossMargin;

    @Column(precision = 15, scale = 2)
    private BigDecimal projectPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal projectTotalCostAtChange;

    private int projectTotalDays;

    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    @ManyToOne
    @JoinColumn(name = "project_location_id")
    private Geography projectLocation;

    private Boolean projectArchived;

    private LocalDate projectRestCostDate;

    @Column(unique = true)
    private String projectSalesNumber;

    //region Getters and Setters
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

    public List<ProjectTeam> getProjectTeams() {
        return projectTeams;
    }

    public void setProjectTeams(List<ProjectTeam> projectTeams) {
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

    public Boolean getProjectArchived() {
        return projectArchived;
    }

    public void setProjectArchived(Boolean projectArchived) {
        this.projectArchived = projectArchived;
    }

    public LocalDate getProjectRestCostDate() {
        return projectRestCostDate;
    }

    public void setProjectRestCostDate(LocalDate projectRestCostDate) {
        this.projectRestCostDate = projectRestCostDate;
    }

    public String getProjectSalesNumber() {
        return projectSalesNumber;
    }

    public void setProjectSalesNumber(String projectSalesNumber) {
        this.projectSalesNumber = projectSalesNumber;
    }
    //endregion
}