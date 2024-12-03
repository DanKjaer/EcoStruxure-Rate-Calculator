package ecostruxure.rate.calculator.bll.project;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectTeam;
import ecostruxure.rate.calculator.dal.IProjectRepository;
import ecostruxure.rate.calculator.dal.IProjectTeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    @Autowired
    private IProjectRepository projectRepository;
    @Autowired
    private IProjectTeamRepository projectTeamRepository;

    public ProjectService() throws Exception {
    }

    public Project getProject(UUID projectId) throws Exception {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public Iterable<Project> getProjects() throws Exception {
        return projectRepository.findAllByProjectArchived(false);
    }

    public Project createProject(Project project) throws Exception {
        return projectRepository.save(project);
    }

    public boolean deleteProject(UUID projectId) throws Exception {
        projectRepository.deleteById(projectId);
        return !projectRepository.existsById(projectId);
    }

    public boolean archiveProject(UUID projectId) throws Exception {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setProjectArchived(true);
        projectRepository.save(project);
        return true;
    }

    public boolean deleteProjectTeam(UUID projectId, UUID teamId) throws Exception {
        projectTeamRepository.findProjectTeamByProject_ProjectId(projectId).deleteById(teamId);
        return !projectTeamRepository.existsById(projectId);
    }

//    public Project createProject(Project project) throws SQLException {
//        // Calculate day rate and gross margin, if project members are present
//        if (project.getProjectMembers() != null) {
//            project.setProjectDayRate(calculateDayRate(project.getProjectMembers()));
//            project.setProjectGrossMargin(calculateGrossMargin(project));
//        }
//        project.setProjectTotalDays(calculateWorkingDays(project.getProjectStartDate(), project.getProjectEndDate()));
//        var newProject = projectDAO.createProject(project);
//        if (!newProject.getProjectMembers().isEmpty()) {
//            projectDAO.assignProfilesToProject(newProject.getProjectId(), project.getProjectMembers());
//        }
//
//        return newProject;
//    }
//
//    public boolean deleteProjectMember(UUID projectId, UUID teamId) throws SQLException {
//        return projectDAO.deleteProjectMember(projectId, teamId);
//    }
//
//    public boolean archiveProject(UUID projectId) throws SQLException {
//        return projectDAO.archiveProject(projectId);
//    }
//
    @Transactional
    public Project updateProject(Project project) throws Exception {
        var projectContainsMembers = project.getProjectTeams() != null;
        var projectContainsDayRate = project.getProjectDayRate() != null;
        var projectIsStarted = LocalDate.now().isAfter(project.getProjectStartDate());

        // Calculate, if rest cost have been calc. before
        if (projectContainsMembers && projectContainsDayRate && project.getProjectRestCostDate() == null) {
            project.setProjectTotalCostAtChange(calculateTotalCostAtChangeFirstTime(project));
            project.setProjectRestCostDate(LocalDate.now());
            project.setProjectDayRate(calculateDayRate(project.getProjectTeams()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }
        // Calculate, if project members are present and project is started
        else if (projectContainsMembers && projectContainsDayRate && projectIsStarted) {
            project.setProjectTotalCostAtChange(calculateTotalCostAtChange(project));
            project.setProjectRestCostDate(LocalDate.now());
            project.setProjectDayRate(calculateDayRate(project.getProjectTeams()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }
        // Calculate, if project members are present and project is not started
        else if (projectContainsMembers && projectContainsDayRate) {
            project.setProjectDayRate(calculateDayRate(project.getProjectTeams()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }

        project.setProjectTotalDays(calculateWorkingDays(project.getProjectStartDate(), project.getProjectEndDate()));

        return projectRepository.save(project);
    }

    private BigDecimal calculateTotalCostAtChangeFirstTime(Project project) {
        BigDecimal totalCostAtChange;
        var daysPassed = LocalDate.now().toEpochDay() - project.getProjectStartDate().toEpochDay();
        totalCostAtChange = project.getProjectDayRate().multiply(BigDecimal.valueOf(daysPassed));
        return totalCostAtChange;
    }

    private BigDecimal calculateTotalCostAtChange(Project project) {
        BigDecimal totalCostAtChange = project.getProjectTotalCostAtChange();
        var daysPassed = LocalDate.now().toEpochDay() - project.getProjectRestCostDate().toEpochDay();
        totalCostAtChange = totalCostAtChange.add(project.getProjectDayRate().multiply(BigDecimal.valueOf(daysPassed)));
        return totalCostAtChange;
    }

    private BigDecimal calculateDayRate(List<ProjectTeam> projectTeams) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        for (ProjectTeam projectTeam : projectTeams) {
            BigDecimal markup = projectTeam.getTeam().getMarkupPercentage();
            var dayRateWithMarkup = projectTeam.getTeam().getDayRate()
                    .multiply(markup.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE));
            var allocatedDayRate = dayRateWithMarkup.multiply(projectTeam.getAllocationPercentage()
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            projectTeam.getTeam().setDayRate(allocatedDayRate);
            totalDayRate = totalDayRate.add(allocatedDayRate);
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
