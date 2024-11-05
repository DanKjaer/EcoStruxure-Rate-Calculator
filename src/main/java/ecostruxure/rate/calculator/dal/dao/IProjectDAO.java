package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Project;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface IProjectDAO {
    Project getProject(UUID projectId) throws SQLException;
    List<Project> getProjects() throws SQLException;
    Project createProject(Project project) throws SQLException;
    Project updateProject(Project project) throws SQLException;
    boolean deleteProject(UUID projectId) throws SQLException;
}
