package ecostruxure.rate.calculator.bll.dashboard;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectTeam;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.dto.DashboardDTO;
import ecostruxure.rate.calculator.be.dto.DashboardProjectDTO;
import ecostruxure.rate.calculator.be.dto.DashboardTeamDTO;
import ecostruxure.rate.calculator.dal.IProjectRepository;
import ecostruxure.rate.calculator.dal.ITeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    public DashboardService() {
    }

    public List<DashboardDTO> getDashboard() {
        Map<String, List<Project>> mappedProjects = getProjects();

        List<DashboardDTO> dashboardDTOs = mappedProjects.entrySet().stream()
                .map(entry -> {
                    String location = entry.getKey();

                    List<Project> projects = entry.getValue();

                    //Aggregate project data
                    BigDecimal totalDayRate = getTotalDayRate(projects);
                    BigDecimal totalGrossMargin = getTotalGrossMargin(projects);
                    BigDecimal totalPrice = getTotalPrice(projects);

                    //Convert Project to DashboardProjectDTO
                    DashboardProjectDTO[] dashboardProjectDTOs = getDashboardProjectDTOS(projects);

                    //Get teams in projects and convert to DashboardTeamDTO and take out duplicates
                    DashboardTeamDTO[] dashboardTeamDTOs = getDashboardTeamDTOS(projects);

                    return new DashboardDTO(
                            location,
                            totalDayRate,
                            totalGrossMargin,
                            totalPrice,
                            dashboardProjectDTOs,
                            dashboardTeamDTOs
                    );
                })
                .toList();
        return dashboardDTOs;
    }

    private DashboardTeamDTO[] getDashboardTeamDTOS(List<Project> projects) {
        DashboardTeamDTO[] dashboardTeamDTOs = projects.stream()
                .flatMap(project -> project.getProjectTeams().stream())
                .map(ProjectTeam::getTeam)
                .map(team -> new DashboardTeamDTO(team.getName()))
                .collect(Collectors.toMap(
                        DashboardTeamDTO::getName,
                        dto -> dto,
                        (existing, replacement) -> existing))
                .values()
                .toArray(DashboardTeamDTO[]::new);
        return dashboardTeamDTOs;
    }

    private DashboardProjectDTO[] getDashboardProjectDTOS(List<Project> projects) {
        DashboardProjectDTO[] dashboardProjectDTOs = projects.stream()
                .map(project -> new DashboardProjectDTO(
                        project.getProjectName(),
                        project.getProjectPrice(),
                        project.getProjectGrossMargin(),
                        project.getProjectDayRate()
                ))
                .toArray(DashboardProjectDTO[]::new);
        return dashboardProjectDTOs;
    }

    private BigDecimal getTotalPrice(List<Project> projects) {
        BigDecimal totalPrice = projects.stream()
                .map(Project::getProjectPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalPrice;
    }

    private BigDecimal getTotalGrossMargin(List<Project> projects) {
        BigDecimal totalGrossMargin = projects.stream()
                .map(Project::getProjectGrossMargin)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalGrossMargin;
    }

    private BigDecimal getTotalDayRate(List<Project> projects) {
        BigDecimal totalDayRate = projects.stream()
                .map(Project::getProjectDayRate)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalDayRate;
    }

    private Map<String, List<Project>> getProjects() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> cq = cb.createQuery(Project.class);
        Root<Project> project = cq.from(Project.class);

        cq.select(project).where(cb.equal(project.get("projectArchived"), false));

        List<Project> projects = entityManager.createQuery(cq).getResultList();

        Map<String, List<Project>> countryProjects = projects.stream()
                .collect(Collectors.groupingBy(proj -> proj.getProjectLocation().getName()));

        return countryProjects;
    }
}
