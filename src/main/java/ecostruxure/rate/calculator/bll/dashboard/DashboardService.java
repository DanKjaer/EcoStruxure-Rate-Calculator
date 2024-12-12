package ecostruxure.rate.calculator.bll.dashboard;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.dto.DashboardDTO;
import ecostruxure.rate.calculator.be.dto.DashboardProjectDTO;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    private final IProjectRepository projectRepository;
    private final ITeamRepository teamRepository;


    @Autowired
    public DashboardService(IProjectRepository projectRepository, ITeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    public List<DashboardDTO> getDashboard() {
        Map<String, List<Project>> mappedProjects = getProjects();

        List<DashboardDTO> dashboardDTOs = mappedProjects.entrySet().stream()
                .map(entry -> {
                    String location = entry.getKey();

                    List<Project> projects = entry.getValue();

                    //Aggregate project data
                    BigDecimal totalDayRate = projects.stream()
                            .map(Project::getProjectDayRate)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalGrossMargin = projects.stream()
                            .map(Project::getProjectGrossMargin)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalPrice = projects.stream()
                            .map(Project::getProjectPrice)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    //Convert Project to DashboardProjectDTO
                    DashboardProjectDTO[] dashboardProjectDTOs = projects.stream()
                            .map(project -> new DashboardProjectDTO(
                                    project.getProjectName(),
                                    project.getProjectPrice(),
                                    project.getProjectGrossMargin()
                            ))
                            .toArray(DashboardProjectDTO[]::new);

                    return new DashboardDTO(
                            location,
                            totalDayRate,
                            totalGrossMargin,
                            totalPrice,
                            dashboardProjectDTOs
                    );
                })
                .toList();
        return dashboardDTOs;
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
