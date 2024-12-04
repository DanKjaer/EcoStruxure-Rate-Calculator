package ecostruxure.rate.calculator.bll.project;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.IProjectRepository;
import ecostruxure.rate.calculator.dal.IProjectTeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectService {
    private final IProjectRepository projectRepository;
    private final IProjectTeamRepository projectTeamRepository;

    @Autowired
    public ProjectService(IProjectRepository projectRepository,
                          IProjectTeamRepository projectTeamRepository) throws Exception {
        this.projectRepository = projectRepository;
        this.projectTeamRepository = projectTeamRepository;
    }

    public Project getProject(UUID projectId) throws Exception {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public Iterable<Project> getProjects() throws Exception {
        return projectRepository.findAllByProjectArchived(false);
    }

    public Project createProject(Project project) throws Exception {
        Project calculatedProject = RateUtils.updateProjectRates(project);
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

    public boolean deleteProjectTeam(UUID projectId, UUID teamId) throws Exception {
        projectTeamRepository.findProjectTeamByProject_ProjectId(projectId).deleteById(teamId);
        return !projectTeamRepository.existsById(projectId);
    }

    @Transactional
    public Project updateProject(Project project) throws Exception {
        Project updatedProject = RateUtils.updateProjectRates(project);
        return projectRepository.save(updatedProject);
    }

}
