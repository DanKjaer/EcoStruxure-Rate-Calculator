package ecostruxure.rate.calculator.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.dal.dao.IProjectDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectDAO implements IProjectDAO {
    private final DBConnector dbConnector;

    public ProjectDAO() throws Exception {
        dbConnector = new DBConnector();
    }

    public Project getProject(UUID projectId) throws SQLException {
        String queryProject = """
                SELECT * FROM dbo.project WHERE project_id = ?
                """;

        try (Connection connection = dbConnector.connection();
             PreparedStatement stmtProject = connection.prepareStatement(queryProject);) {
            Project project = new Project();
            stmtProject.setObject(1, projectId);
            try (ResultSet rsProject = stmtProject.executeQuery()) {
                if (rsProject.next()) {
                    project.setProjectId(UUID.fromString(rsProject.getString("project_id")));
                    project.setProjectName(rsProject.getString("project_name"));
                    project.setProjectDescription(rsProject.getString("project_description"));
                    project.setProjectCost(rsProject.getBigDecimal("project_cost"));
                    project.setProjectMargin(rsProject.getBigDecimal("project_margin"));
                    project.setProjectPrice(rsProject.getBigDecimal("project_price"));
                    project.setStartDate(rsProject.getTimestamp("start_date"));
                    project.setEndDate(rsProject.getTimestamp("end_date"));
                }
            }
            project.setProjectMembers(getProfilesBasedOnProject(connection, projectId));

            return project;
            }
        }

    private List<Profile> getProfilesBasedOnProject(Connection conn,UUID projectId) throws SQLException {
        String queryProjectMembers = """
                SELECT     pr.profile_id,
                           pr.name,
                           pr.currency,
                           pr.country_id,
                           pr.resource_type,
                           pr.annual_cost,
                           pr.effectiveness,
                           pr.annual_hours,
                           pr.effective_work_hours,
                           pr.hours_per_day,
                           pr.total_cost_allocation,
                           pr.total_hour_allocation,
                           pr.is_archived,
                           pr.updated_at
                FROM dbo.project_members pm
                JOIN dbo.profiles pr ON pm.profile_id = pr.profile_id
                WHERE pm.project_id = ?;
                """;

        List<Profile> profiles = new ArrayList<>();
        try (PreparedStatement stmtMembers = conn.prepareStatement(queryProjectMembers)) {
            stmtMembers.setObject(1, projectId);
            try (ResultSet rsMembers = stmtMembers.executeQuery()) {
                while (rsMembers.next()) {
                    Profile profile = new Profile();
                    profile.setProfileId(UUID.fromString(rsMembers.getString("profile_id")));
                    profile.setName(rsMembers.getString("name"));
                    profile.setCurrency(rsMembers.getString("currency"));
                    profile.setCountryId(rsMembers.getInt("country_id"));
                    profile.setResourceType(rsMembers.getBoolean("resource_type"));
                    profile.setAnnualCost(rsMembers.getBigDecimal("annual_cost"));
                    profile.setEffectivenessPercentage(rsMembers.getBigDecimal("effectiveness"));
                    profile.setAnnualHours(rsMembers.getBigDecimal("annual_hours"));
                    profile.setEffectiveWorkHours(rsMembers.getBigDecimal("effective_work_hours"));
                    profile.setHoursPerDay(rsMembers.getBigDecimal("hours_per_day"));
                    profile.setTotalCostAllocation(rsMembers.getBigDecimal("total_cost_allocation"));
                    profile.setTotalHourAllocation(rsMembers.getBigDecimal("total_hour_allocation"));
                    profile.setArchived(rsMembers.getBoolean("is_archived"));
                    profile.setUpdatedAt(rsMembers.getTimestamp("updated_at"));
                    profiles.add(profile);
                }
            }
            return profiles;
        }
    }

    public List<Project> getProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String queryProjects = """
                SELECT * 
                FROM dbo.project
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmtProjects = connection.prepareStatement(queryProjects);) {
            try (ResultSet rsProjects = stmtProjects.executeQuery()) {
                while (rsProjects.next()) {
                    Project project = new Project();
                    project.setProjectId(UUID.fromString(rsProjects.getString("project_id")));
                    project.setProjectName(rsProjects.getString("project_name"));
                    project.setProjectDescription(rsProjects.getString("project_description"));
                    project.setProjectCost(rsProjects.getBigDecimal("project_cost"));
                    project.setProjectMargin(rsProjects.getBigDecimal("project_margin"));
                    project.setProjectPrice(rsProjects.getBigDecimal("project_price"));
                    project.setStartDate(rsProjects.getTimestamp("start_date"));
                    project.setEndDate(rsProjects.getTimestamp("end_date"));
                    project.setProjectMembers(getProfilesBasedOnProject(connection, project.getProjectId()));
                    projects.add(project);
                }
                return projects;
            }
        }
    }

    @Override
    public Project createProject(Project project) throws SQLException {
        return null;
    }

    @Override
    public Project updateProject(Project project) throws SQLException {
        return null;
    }

    @Override
    public boolean deleteProject(UUID projectId) throws SQLException {
        return false;
    }
}
