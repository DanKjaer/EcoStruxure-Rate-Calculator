package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.dal.dao.IProjectDAO;

import java.sql.*;
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
                SELECT *
                FROM dbo.project
                INNER JOIN dbo.geography g on g.id = project.project_location
                WHERE project_id = ?
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
            project.setProjectMembers(getMembersBasedOnProject(connection, projectId));

            return project;
        }
    }

    private Project createProjectFromResultset(Project project, ResultSet rsProject) throws SQLException {
        Geography geography = new Geography(rsProject.getString("name"));
        geography.setId(rsProject.getInt("project_location"));

        project.setProjectId(UUID.fromString(rsProject.getString("project_id")));
        project.setProjectName(rsProject.getString("project_name"));
        project.setProjectSalesNumber(rsProject.getString("project_sales_number"));
        project.setProjectDescription(rsProject.getString("project_description"));
        project.setProjectDayRate(rsProject.getBigDecimal("project_day_rate"));
        project.setProjectGrossMargin(rsProject.getBigDecimal("project_gross_margin"));
        project.setProjectPrice(rsProject.getBigDecimal("project_price"));
        project.setProjectStartDate(rsProject.getDate("project_start_date").toLocalDate());
        project.setProjectEndDate(rsProject.getDate("project_end_date").toLocalDate());
        project.setProjectTotalDays(rsProject.getInt("project_total_days"));
        project.setProjectLocation(geography);

        return project;
    }

    private List<ProjectMember> getMembersBasedOnProject(Connection conn, UUID projectId) throws SQLException {
        String queryProjectMembers = """
                SELECT  *,
                        t.id,
                        t.name,
                        t.markup,
                        t.day_rate
                FROM dbo.project_members pm
                JOIN dbo.teams t ON pm.teams_id = t.id
                WHERE pm.project_id = ?;
                """;

        List<ProjectMember> projectMembers = new ArrayList<>();
        try (PreparedStatement stmtMembers = conn.prepareStatement(queryProjectMembers)) {
            stmtMembers.setObject(1, projectId);
            try (ResultSet rsMembers = stmtMembers.executeQuery()) {
                while (rsMembers.next()) {
                    ProjectMember projectMember = new ProjectMember();
                    projectMember.setTeamId(UUID.fromString(rsMembers.getString("teams_id")));
                    projectMember.setProjectId(UUID.fromString(rsMembers.getString("project_id")));
                    projectMember.setName(rsMembers.getString("name"));
                    projectMember.setProjectAllocation(rsMembers.getBigDecimal("allocation_on_project"));
                    projectMember.setMarkup(rsMembers.getBigDecimal("markup"));
                    projectMember.setDayRate(rsMembers.getBigDecimal("day_rate"));
                    projectMember.setDayRateWithMarkup(rsMembers.getBigDecimal("day_rate_on_project"));
                    projectMembers.add(projectMember);
                }
            }
            return projectMembers;
        }
    }

    public List<Project> getProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String queryProjects = """
                SELECT *
                FROM dbo.project
                INNER JOIN dbo.geography g on g.id = project.project_location
                WHERE project_archived = FALSE;
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmtProjects = connection.prepareStatement(queryProjects);) {
            try (ResultSet rsProjects = stmtProjects.executeQuery()) {
                while (rsProjects.next()) {
                    Project project = new Project();
                    project = createProjectFromResultset(project, rsProjects);
                    project.setProjectMembers(getMembersBasedOnProject(connection, project.getProjectId()));
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
                         project_sales_number,
                         project_description,
                         project_day_rate,
                         project_gross_margin,
                         project_price,
                         project_start_date,
                         project_end_date,
                         project_total_days,
                         project_location,
                         project_archived)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE);
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, project.getProjectName());
            stmt.setString(3, project.getProjectSalesNumber());
            stmt.setString(4, project.getProjectDescription());
            stmt.setBigDecimal(5, project.getProjectDayRate());
            stmt.setBigDecimal(6, project.getProjectGrossMargin());
            stmt.setBigDecimal(7, project.getProjectPrice());
            stmt.setDate(8, Date.valueOf(project.getProjectStartDate()));
            stmt.setDate(9, Date.valueOf(project.getProjectEndDate()));
            stmt.setInt(10, project.getProjectTotalDays());
            stmt.setInt(11, project.getProjectLocation().getId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                project.setProjectId(UUID.fromString(rs.getString("project_id")));
            }
            return project;
        }
    }

    @Override
    public List<ProjectMember> assignProfilesToProject(UUID projectId, List<ProjectMember> projectMembers) throws SQLException {
        String query = """
                INSERT INTO dbo.project_members (project_id, teams_id, allocation_on_project, day_rate_on_project)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query);) {
            for (ProjectMember projectMember : projectMembers) {
                projectMember.setProjectId(projectId);
                stmt.setObject(1, projectMember.getProjectId());
                stmt.setObject(2, projectMember.getTeamId());
                stmt.setBigDecimal(3, projectMember.getProjectAllocation());
                stmt.setBigDecimal(4, projectMember.getDayRateWithMarkup());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return projectMembers;
        }
    }

    @Override
    public boolean updateProject(Project project) throws SQLException {
        String query = """
                UPDATE dbo.project
                SET project_name = ?,
                    project_sales_number = ?,
                    project_description = ?,
                    project_day_rate = ?,
                    project_gross_margin = ?,
                    project_price = ?,
                    project_end_date = ?,
                    project_total_days = ?,
                    project_location = ?
                WHERE project_id = ?;
                """;

        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query);) {
            stmt.setString(1, project.getProjectName());
            stmt.setString(2, project.getProjectSalesNumber());
            stmt.setString(3, project.getProjectDescription());
            stmt.setBigDecimal(4, project.getProjectDayRate());
            stmt.setBigDecimal(5, project.getProjectGrossMargin());
            stmt.setBigDecimal(6, project.getProjectPrice());
            stmt.setDate(7, Date.valueOf(project.getProjectEndDate()));
            stmt.setInt(8, project.getProjectTotalDays());
            stmt.setInt(9, project.getProjectLocation().getId());
            stmt.setObject(10, project.getProjectId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteProjectMember(UUID projectId, UUID teamId) throws SQLException {
        String query = """
                DELETE FROM dbo.project_members WHERE project_id = ? AND teams_id = ?;
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query);) {
            stmt.setObject(1, projectId);
            stmt.setObject(2, teamId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    @Override
    public boolean archiveProject(UUID projectId) throws SQLException{
        String sql = """ 
                UPDATE dbo.project SET project_archived = TRUE WHERE project_id = ?;
                """;

        try(Connection conn = dbConnector.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, projectId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateAssignedProfiles(UUID projectId, List<ProjectMember> projectMembers) {
        String query = """
                INSERT INTO dbo.project_members (project_id, teams_id, allocation_on_project, day_rate_on_project)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (project_id, teams_id) DO UPDATE SET allocation_on_project = ?, day_rate_on_project = ?;
                """;
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            for (ProjectMember projectMember : projectMembers) {
                stmt.setObject(1, projectId);
                stmt.setObject(2, projectMember.getTeamId());
                stmt.setBigDecimal(3, projectMember.getProjectAllocation());
                stmt.setBigDecimal(4, projectMember.getDayRateWithMarkup());
                stmt.setBigDecimal(5, projectMember.getProjectAllocation());
                stmt.setBigDecimal(6, projectMember.getDayRateWithMarkup());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Project> getProjectsBasedOnTeam(UUID teamId) throws SQLException {
        String query = """
                SELECT *
                FROM dbo.project
                INNER JOIN dbo.project_members pm on project.project_id = pm.project_id
                INNER JOIN dbo.geography g on g.id = project.project_location
                WHERE pm.teams_id = ? AND project_archived = FALSE;
                """;
        List<Project> projects = new ArrayList<>();
        try (Connection connection = dbConnector.connection();
             PreparedStatement stmt = connection.prepareStatement(query);) {
            stmt.setObject(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project();
                    project = createProjectFromResultset(project, rs);
                    project.setProjectMembers(getMembersBasedOnProject(connection, project.getProjectId()));
                    projects.add(project);
                }
                return projects;
            }
        }
    }
}
