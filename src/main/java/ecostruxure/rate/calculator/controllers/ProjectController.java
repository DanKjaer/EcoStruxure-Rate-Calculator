package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.dto.ProjectDTO;
import ecostruxure.rate.calculator.bll.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    public ProjectController() throws Exception {
    }

    @GetMapping()
    public Project getProject(@RequestParam UUID projectId) throws Exception {
        return projectService.getProject(projectId);
    }

    @GetMapping("/all")
    public Iterable<Project> getProjects() throws Exception {
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

    @DeleteMapping()
    public boolean deleteProjectMember(@RequestParam UUID projectId, @RequestParam UUID teamId) throws Exception {
        return projectService.deleteProjectTeam(projectId, teamId);
    }

    @PutMapping("/update")
    public Project updateProject(@RequestBody Project project) throws Exception {
        return projectService.updateProject(project);
    }
}
