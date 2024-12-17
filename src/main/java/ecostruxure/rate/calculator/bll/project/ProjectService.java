package ecostruxure.rate.calculator.bll.project;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectTeam;
import ecostruxure.rate.calculator.be.dto.ProjectDTO;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.IProjectRepository;
import ecostruxure.rate.calculator.dal.IProjectTeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @PersistenceContext
    private final EntityManager entityManager;

    private final IProjectRepository projectRepository;
    private final IProjectTeamRepository projectTeamRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public ProjectService(IProjectRepository projectRepository,
                          IProjectTeamRepository projectTeamRepository,
                          EntityManager entityManager) throws Exception {
        this.projectRepository = projectRepository;
        this.projectTeamRepository = projectTeamRepository;
        this.entityManager = entityManager;
    }

    public Project getProject(UUID projectId) throws Exception {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public List<ProjectDTO> getProjects() throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> cq = cb.createQuery(Project.class);
        Root<Project> project = cq.from(Project.class);
        project.fetch("projectTeams", JoinType.LEFT).fetch("team", JoinType.LEFT);

        cq.select(project).where(cb.equal(project.get("projectArchived"), false));

        List<Project> projects = entityManager.createQuery(cq).getResultList();

        List<ProjectDTO> projectDTOLists = projects.stream()
                .map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                        .toList();
        return projectDTOLists;
    }

    public Project createProject(Project project) throws Exception {
        Project calculatedProject = RateUtils.updateProjectRates(project);
        for (ProjectTeam projectTeam : calculatedProject.getProjectTeams()) {
            projectTeam.setProject(calculatedProject);
        }
        calculatedProject.setProjectArchived(false);
        return projectRepository.save(calculatedProject);
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

    public Project deleteProjectTeam(UUID projectTeamId) throws Exception {
        ProjectTeam projectTeam = projectTeamRepository.findById(projectTeamId).orElseThrow(() ->
                new EntityNotFoundException("Project Team not found"));
        projectTeamRepository.deleteById(projectTeamId);
        var updatedProject = RateUtils.updateProjectRates(projectTeam.getProject());
        projectRepository.save(updatedProject);
        return updatedProject;
    }

    @Transactional
    public Project updateProject(Project project) throws Exception {
        Project updatedProject = RateUtils.updateProjectRates(project);
        return projectRepository.save(updatedProject);
    }

    public Iterable<Project> getProjectsForExcel() {
        return projectRepository.findAll();
    }
}
