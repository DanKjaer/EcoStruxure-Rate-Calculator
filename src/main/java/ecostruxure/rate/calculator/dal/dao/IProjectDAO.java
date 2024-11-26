package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectMember;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface IProjectDAO {
    Project getProject(UUID projectId) throws SQLException;
    List<Project> getProjects() throws SQLException;
    Project createProject(Project project) throws SQLException;
    boolean updateProject(Project project) throws SQLException;
    boolean deleteProject(UUID projectId) throws SQLException;
    boolean deleteProjectMember(UUID projectId, UUID teamId) throws SQLException;
    List<ProjectMember> assignProfilesToProject(UUID projectId, List<ProjectMember> projectMembers) throws SQLException;
    boolean archiveProject(UUID projectId) throws SQLException;
    void updateAssignedProfiles(UUID projectId, List<ProjectMember> projectMembers);
    List<Project> getProjectsBasedOnTeam(UUID teamId) throws SQLException;
}
