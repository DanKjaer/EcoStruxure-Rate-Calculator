package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.bll.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
