package ecostruxure.rate.calculator.bll.project;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectTeam;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.IProjectRepository;
import ecostruxure.rate.calculator.dal.IProjectTeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {
    @PersistenceContext
    private EntityManager entityManager;
    private final IProjectRepository projectRepository;

    @Autowired
    public ProjectService(IProjectRepository projectRepository,
                          IProjectTeamRepository projectTeamRepository) throws Exception {
        this.projectRepository = projectRepository;
    }

    public Project getProject(UUID projectId) throws Exception {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public Iterable<Project> getProjects() throws Exception {
        return projectRepository.findAllByProjectArchived(false);
    }

    @Transactional
    public Project createProject(Project project) throws Exception {
        Project calculatedProject = RateUtils.updateProjectRates(project);
        for (ProjectTeam projectTeam : calculatedProject.getProjectTeams()) {
            projectTeam.setProject(calculatedProject);
        }
        calculatedProject.setProjectArchived(false);
        entityManager.persist(calculatedProject);
        return calculatedProject;
    }

    @Transactional
    public boolean deleteProject(UUID projectId) throws Exception {
        Optional<Project> project = projectRepository.findById(projectId);
        if((project.isPresent())) {
            entityManager.remove(project.get());
        }
        return !projectRepository.existsById(projectId);
    }

    public boolean archiveProject(UUID projectId) throws Exception {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setProjectArchived(true);
        projectRepository.save(project);
        return true;
    }

    @Transactional
    public Project deleteProjectTeam(UUID projectTeamId) throws Exception {
        ProjectTeam projectTeam = entityManager.find(ProjectTeam.class, projectTeamId);
        if (projectTeam == null) {
            throw new EntityNotFoundException("Project Team not found");
        }
        Project project = projectTeam.getProject();
        entityManager.remove(projectTeam);
        project.getProjectTeams().remove(projectTeam);
        var updatedProject = RateUtils.updateProjectRates(project);
        updatedProject = entityManager.merge(updatedProject);

        return updatedProject;
    }

    @Transactional
    public Project updateProject(Project project) throws Exception {
        Project updatedProject = RateUtils.updateProjectRates(project);
        return projectRepository.save(updatedProject);
    }
}
