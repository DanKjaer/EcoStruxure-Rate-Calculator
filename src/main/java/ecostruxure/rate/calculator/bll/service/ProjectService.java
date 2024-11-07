package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.dal.dao.IProjectDAO;
import ecostruxure.rate.calculator.dal.db.ProjectDAO;

import java.sql.SQLException;
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
        // lav udregninger her!!!!!
        var newProject = projectDAO.createProject(project);
        if (!newProject.getProjectMembers().isEmpty()) {
            projectDAO.assignProfilesToProject(newProject.getProjectId(), project.getProjectMembers());
        }

        return newProject;
    }
}
