package ecostruxure.rate.calculator.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.dal.dao.IProjectDAO;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
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
                    project = createProjectFromResultset(project, rsProject);
                }
            }
            project.setProjectMembers(getProfilesBasedOnProject(connection, projectId));

            return project;
            }
        }

    private Project createProjectFromResultset(Project project, ResultSet rsProject) throws SQLException {
        project.setProjectId(UUID.fromString(rsProject.getString("project_id")));
        project.setProjectName(rsProject.getString("project_name"));
        project.setProjectDescription(rsProject.getString("project_description"));
        project.setProjectCost(rsProject.getBigDecimal("project_cost"));
        project.setProjectMargin(rsProject.getBigDecimal("project_margin"));
        project.setProjectPrice(rsProject.getBigDecimal("project_price"));
        project.setStartDate(rsProject.getDate("start_date").toLocalDate());
        project.setEndDate(rsProject.getDate("end_date").toLocalDate());

        return project;
    }

    private List<Profile> getProfilesBasedOnProject(Connection conn,UUID projectId) throws SQLException {
        String queryProjectMembers = """
                SELECT     pr.profile_id,
                           pr.name,
                           pr.currency,
                           pr.geography_id,
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
                    Geography geography = new Geography();
                    profile.setProfileId(UUID.fromString(rsMembers.getString("profile_id")));
                    profile.setName(rsMembers.getString("name"));
                    profile.setCurrency(rsMembers.getString("currency"));
                    geography.setId(rsMembers.getInt("geography_id"));
                    geography.setName(rsMembers.getString("geography.name"));
                    profile.setGeography(geography);
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
                    project = createProjectFromResultset(project, rsProjects);
                    project.setProjectMembers(getProfilesBasedOnProject(connection, project.getProjectId()));
                    projects.add(project);
                }
                return projects;
            }
        }
    }

    @Override
    public Project createProject(Project project) throws SQLException {
        String query = """
                INSERT INTO dbo.project (
                         project_id,
                         project_name,
                         project_description,
                         project_cost,
                         project_markup,
                         project_gross_margin,
                         project_margin,
                         project_price,
                         start_date,
                         end_date)
                VALUES (?, ?, ?, 1000000, ?, ?, 1000000, 1000000, ?, ?);
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, project.getProjectName());
            stmt.setString(3, project.getProjectDescription());
            stmt.setBigDecimal(4, project.getProjectMarkup());
            stmt.setBigDecimal(5, project.getProjectGrossMargin());
            stmt.setDate(6, Date.valueOf(project.getStartDate()));
            stmt.setDate(7, Date.valueOf(project.getEndDate()));
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                project.setProjectId(UUID.fromString(rs.getString("project_id")));
            }
            return project;
        }
    }

    @Override
    public List<Profile> assignProfilesToProject(UUID projectId, List<Profile> projectMembers) throws SQLException {
        String query = """
                INSERT INTO dbo.project_members (project_id, profile_id)
                VALUES (?, ?);
                """;
        try (Connection connection = dbConnector.connection();
                PreparedStatement stmt = connection.prepareStatement(query);) {
                for (Profile profile : projectMembers) {
                    stmt.setObject(1, projectId);
                    stmt.setObject(2, profile.getProfileId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                return projectMembers;
            }
    }

    @Override
    public Project updateProject(Project project) throws SQLException {
        return null;
    }

    @Override
    public boolean deleteProject(UUID projectId) throws SQLException {
        String deleteProjectMembersQuery = """
                DELETE FROM dbo.project_members WHERE project_id = ?;
                """;
        String query ="""
                DELETE FROM dbo.project WHERE project_id = ?;
                """;

        try(Connection conn = dbConnector.connection();
            PreparedStatement deleteProjectMembersStmt = conn.prepareStatement(deleteProjectMembersQuery);
            PreparedStatement deleteProject = conn.prepareStatement(query)) {

            deleteProjectMembersStmt.setObject(1, projectId);
            deleteProjectMembersStmt.executeUpdate();

            deleteProject.setObject(1, projectId);
            deleteProject.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
