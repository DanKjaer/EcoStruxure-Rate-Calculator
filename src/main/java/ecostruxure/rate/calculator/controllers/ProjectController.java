package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.dto.ProjectDTO;
import ecostruxure.rate.calculator.bll.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController() throws Exception {
        projectService = new ProjectService();
    }

    @GetMapping()
    public Project getProject(@RequestParam UUID projectId) throws Exception {
        return projectService.getProject(projectId);
    }

    @GetMapping("/all")
    public List<Project> getProjects() throws Exception {
        return projectService.getProjects();
    }

    @PostMapping()
    public Project createProject(@RequestBody ProjectDTO projectDTO) throws Exception {
        return projectService.createProject(projectDTO.getProject());
    }

    @DeleteMapping("/{id}")
    public boolean deleteProfile(@PathVariable UUID id) throws Exception {
        return projectService.deleteProject(id);
    }

    /**
     * Using a put mapping to delete a project member, as it is a partial update of the project
     * @param project
     * @param teamId
     * @return
     * @throws Exception
     */
    @PutMapping("/delete_member")
    public Project deleteProjectMember(@RequestBody Project project, @RequestParam UUID teamId) throws Exception {
        return projectService.deleteProjectMember(project, teamId);
    }

    @PutMapping("/update")
    public Project updateProject(@RequestBody Project project) throws Exception {
        return projectService.updateProject(project);
    }
}
