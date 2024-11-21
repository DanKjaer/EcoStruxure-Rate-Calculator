package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectMember;
import ecostruxure.rate.calculator.dal.dao.IProjectDAO;
import ecostruxure.rate.calculator.dal.db.ProjectDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProjectService {
    private final IProjectDAO projectDAO;

    public ProjectService() throws Exception {
        projectDAO = new ProjectDAO();
    }

    public Project getProject(UUID projectId) throws Exception {
        return projectDAO.getProject(projectId);
    }

    public List<Project> getProjects() throws Exception {
        return projectDAO.getProjects();
    }

    public Project createProject(Project project) throws SQLException {
        // Calculate day rate and gross margin, if project members are present
        if (project.getProjectMembers() != null) {
            project.setProjectDayRate(calculateDayRate(project.getProjectMembers()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }
        project.setProjectTotalDays(calculateWorkingDays(project.getProjectStartDate(), project.getProjectEndDate()));

        var newProject = projectDAO.createProject(project);
        if (!newProject.getProjectMembers().isEmpty()) {
            projectDAO.assignProfilesToProject(newProject.getProjectId(), project.getProjectMembers());
        }

        return newProject;
    }

    public boolean deleteProject(UUID projectId) throws SQLException {
        return projectDAO.deleteProject(projectId);
    }

    public boolean archiveProject(UUID projectId) throws SQLException {
        return projectDAO.archiveProject(projectId);
    }

    public Project updateProject(Project project) throws SQLException {
        try {
            System.out.println("dayrate service før if : " + project.getProjectDayRate());
            if (project.getProjectMembers() != null && project.getProjectDayRate() == null) {
                project.setProjectDayRate(calculateDayRate(project.getProjectMembers()));
                project.setProjectGrossMargin(calculateGrossMargin(project));
            }
            System.out.println("dayrate service efter if : " + project.getProjectDayRate());
            project.setProjectTotalDays(calculateWorkingDays(project.getProjectStartDate(), project.getProjectEndDate()));

            validateProject(project);

            var updateSuccess = projectDAO.updateProject(project);

            if (updateSuccess && !project.getProjectMembers().isEmpty()) {
                projectDAO.updateAssignedProfiles(project.getProjectId(), project.getProjectMembers()
                );
            }
            System.out.println("dayrate service efter update : " + project.getProjectDayRate());
            return project;
        }catch(SQLException e){
            throw new SQLException("Failed to update project", e);
        }
    }

    private void validateProject(Project project) throws SQLException{
        BigDecimal grossMargin = project.getProjectGrossMargin();
        if (grossMargin.compareTo(BigDecimal.ZERO) < 0 || grossMargin.compareTo(new BigDecimal("999.99")) > 0) {
            throw new SQLException("Project gross margin cannot be more than 999.99, or less than 0");
        }
    }

    private BigDecimal calculateDayRate(List<ProjectMember> projectMembers) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        for (ProjectMember projectMember : projectMembers) {
            BigDecimal markup = projectMember.getMarkup();
            var dayRateWithMarkup = projectMember.getDayRate()
                    .multiply(markup.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE));
            totalDayRate = totalDayRate.add(dayRateWithMarkup);
        }
        return totalDayRate;
    }

    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                count++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return count;
    }

    private BigDecimal calculateGrossMargin(Project project) {
        BigDecimal grossMarginNumber = project.getProjectPrice().subtract(project.getProjectDayRate().multiply(BigDecimal.valueOf(project.getProjectTotalDays())));
        BigDecimal grossMarginPercentage = grossMarginNumber.divide(project.getProjectPrice(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return grossMarginPercentage;
    }
}
