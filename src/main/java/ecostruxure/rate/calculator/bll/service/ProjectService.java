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
        project.setProjectDayRate(calculateDayRate(project.getProjectMembers()));
        project.setProjectTotalDays(calculateWorkingDays(project.getProjectStartDate(), project.getProjectEndDate()));
        project.setProjectGrossMargin(calculateGrossMargin(project));
        var newProject = projectDAO.createProject(project);
        if (!newProject.getProjectMembers().isEmpty()) {
            projectDAO.assignProfilesToProject(newProject.getProjectId(), project.getProjectMembers());
        }

        return newProject;
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
            System.out.println();
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

    public boolean deleteProject(UUID projectId) throws SQLException {
        return projectDAO.deleteProject(projectId);
    }

    public boolean archiveProject(UUID projectId) throws SQLException {
        return projectDAO.archiveProject(projectId);
    }
}
