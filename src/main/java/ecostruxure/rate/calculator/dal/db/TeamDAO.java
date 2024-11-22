package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;
import ecostruxure.rate.calculator.dal.dao.ITeamDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class TeamDAO implements ITeamDAO {
    private final DBConnector dbConnector;
    private static final Logger logger = Logger.getLogger(TeamDAO.class.getName());

    public TeamDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }

    @Override
    public List<Team> all() throws Exception {
        List<Team> teams = new ArrayList<>();

        String query = """
                SELECT * 
                FROM dbo.teams 
                WHERE is_archived = FALSE
                ORDER BY id DESC
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Team team = new Team.Builder()
                        .teamId((UUID) rs.getObject("id"))
                        .name(rs.getString("name"))
                        .markup(rs.getBigDecimal("markup"))
                        .grossMargin(rs.getBigDecimal("gross_margin"))
                        .archived(rs.getBoolean("is_archived"))
                        .updatedAt(rs.getTimestamp("updated_at"))
                        .dayRate(rs.getBigDecimal("day_rate"))
                        .hourlyRate(rs.getBigDecimal("hourly_rate"))
                        .totalAllocatedCost(rs.getBigDecimal("total_allocated_cost"))
                        .totalAllocatedHours(rs.getBigDecimal("total_allocated_hours"))
                        .totalMarkup(rs.getBigDecimal("total_markup"))
                        .totalGrossMargin(rs.getBigDecimal("total_gross_margin"))
                        .build();
                teams.add(team);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get all Teams from Database.\n" + e.getMessage());
        }
        return teams;
    }

    @Override
    public List<Team> getTeams(List<TeamProfile> teamProfiles) throws SQLException {
        String query = """
                SELECT * FROM dbo.teams WHERE id = ?
                """;
        List<Team> teams = new ArrayList<>();
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);) {
            for (TeamProfile teamProfile : teamProfiles) {
                stmt.setObject(1, teamProfile.getTeamId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Team team = new Team.Builder()
                                .teamId((UUID) rs.getObject("id"))
                                .name(rs.getString("name"))
                                .markup(rs.getBigDecimal("markup"))
                                .grossMargin(rs.getBigDecimal("gross_margin"))
                                .archived(rs.getBoolean("is_archived"))
                                .updatedAt(rs.getTimestamp("updated_at"))
                                .dayRate(rs.getBigDecimal("day_rate"))
                                .hourlyRate(rs.getBigDecimal("hourly_rate"))
                                .totalAllocatedCost(rs.getBigDecimal("total_allocated_cost"))
                                .totalAllocatedHours(rs.getBigDecimal("total_allocated_hours"))
                                .totalMarkup(rs.getBigDecimal("total_markup"))
                                .totalGrossMargin(rs.getBigDecimal("total_gross_margin"))
                                .build();
                        teams.add(team);
                    }
                }
            }
        }
        return teams;
    }

    @Override
    public Team get(UUID id) throws Exception {
        Team team = null;

        String query = """
                SELECT * FROM Teams WHERE id = ?
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    team = new Team.Builder()
                            .teamId((UUID) rs.getObject("id"))
                            .name(rs.getString("name"))
                            .markup(rs.getBigDecimal("markup"))
                            .grossMargin(rs.getBigDecimal("gross_margin"))
                            .archived(rs.getBoolean("is_archived"))
                            .updatedAt(rs.getTimestamp("updated_at"))
                            .dayRate(rs.getBigDecimal("day_rate"))
                            .hourlyRate(rs.getBigDecimal("hourly_rate"))
                            .totalAllocatedCost(rs.getBigDecimal("total_allocated_cost"))
                            .totalAllocatedHours(rs.getBigDecimal("total_allocated_hours"))
                            .totalMarkup(rs.getBigDecimal("total_markup"))
                            .totalGrossMargin(rs.getBigDecimal("total_gross_margin"))
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get Team from Database. Team ID: " + id + "\n" + e.getMessage());
        }

        if (team == null) throw new Exception("Team with ID " + id + " not found.");

        return team;
    }

    @Override
    public List<TeamProfile> getByProfileId(UUID id) throws Exception {
        List<TeamProfile> teams = new ArrayList<>();

        String query = """
                       SELECT tp.*, t.name, p.annual_cost, p.annual_hours, g.id, g.name, g.predefined
                       FROM dbo.teams_profiles tp
                       INNER JOIN dbo.teams t ON t.id = tp.teamid
                       INNER JOIN dbo.profiles p on p.profile_id = tp.profileid
                       INNER JOIN dbo.geography g on p.geography_id = g.id
                       WHERE profileid = ?
                """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID teamId = (UUID) rs.getObject("teamid");
                UUID profileId = (UUID) rs.getObject("profileId");
                String name = rs.getString("name");
                BigDecimal dayRate = rs.getBigDecimal("day_rate_on_team");
                BigDecimal costAllocation = rs.getBigDecimal("cost_allocation");
                BigDecimal hourAllocation = rs.getBigDecimal("hour_allocation");
                BigDecimal allocatedCostOnTeam = rs.getBigDecimal("allocated_cost_on_team");
                BigDecimal allocatedHoursOnTeam = rs.getBigDecimal("allocated_hours_on_team");
                BigDecimal annualCost = rs.getBigDecimal("annual_cost");
                BigDecimal annualHours = rs.getBigDecimal("annual_hours");
                Geography geography = new Geography(rs.getInt("id"),
                                                    rs.getString("geo_name"),
                                                    rs.getBoolean("predefined"));

                teams.add(new TeamProfile(teamId, profileId, name, dayRate, costAllocation, hourAllocation,
                        allocatedCostOnTeam, allocatedHoursOnTeam, annualCost, annualHours, geography));
            }
            return teams;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get TeamProfiles from Database. Team ID: " + id + "\n" + e.getMessage());
        }
    }

    @Override
    public Team create(Team team) throws Exception {
        String query = """
                INSERT INTO dbo.teams (name, markup, gross_margin, is_archived, updated_at) VALUES (?, 0, 0, FALSE, NOW())
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, team.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    team.setTeamId((UUID) rs.getObject("id"));
                    team.setUpdatedAt(rs.getTimestamp("updated_at"));
                    team.setMarkup(rs.getBigDecimal("markup"));
                    team.setGrossMargin(rs.getBigDecimal("gross_margin"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not create Team in Database.\n" + e.getMessage());
        }

        return team;
    }

    @Override
    public Team create(TransactionContext context, Team team) throws Exception {

        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                INSERT INTO Teams (name, markup, gross_margin) VALUES (?, ?, ?)
                """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, team.getName());
            stmt.setBigDecimal(2, team.getMarkup());
            stmt.setBigDecimal(3, team.getGrossMargin());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    team.setTeamId((UUID) rs.getObject(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not create Team in Database.\n" + e.getMessage());
        }
        return team;
    }

    @Override
    public Team update(UUID teamId, Team team) throws Exception {
        String query = """
                    UPDATE dbo.teams SET name = ?,
                                     markup = ?,
                                     gross_margin = ?,
                                     day_rate = ?,
                                     hourly_rate = ?,
                                     total_allocated_hours = ?,
                                     total_allocated_cost = ?,
                                     total_markup = ?,
                                     total_gross_margin = ?,
                                     updated_at = CURRENT_TIMESTAMP 
                    WHERE id = ?
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, team.getName());
            stmt.setBigDecimal(2, team.getMarkup());
            stmt.setBigDecimal(3, team.getGrossMargin());
            stmt.setBigDecimal(4, team.getDayRate());
            stmt.setBigDecimal(5, team.getHourlyRate());
            stmt.setBigDecimal(6, team.getTotalAllocatedHours());
            stmt.setBigDecimal(7, team.getTotalAllocatedCost());
            stmt.setBigDecimal(8, team.getTotalMarkup());
            stmt.setBigDecimal(9, team.getTotalGrossMargin());
            stmt.setObject(10, team.getTeamId());
            stmt.executeUpdate();
            return team;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update name for Team in Database.\n" + e.getMessage());
        }
    }


    @Override
    public void updateMultipliers(Team team) throws Exception {
        String query = """
                UPDATE Teams SET markup = ?, gross_margin = ? WHERE id = ?
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, team.getMarkup());
            stmt.setBigDecimal(2, team.getGrossMargin());
            stmt.setObject(3, team.getTeamId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update markup for Team in Database.\n" + e.getMessage());
        }
    }

    @Override
    public boolean assignProfiles(Team team, List<TeamProfile> teamProfiles) throws Exception {
        String query = """
                INSERT INTO Teams_profiles (teamId, profileId, cost_allocation, hour_allocation) VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            for (TeamProfile teamProfile : teamProfiles) {
                stmt.setObject(1, team.getTeamId());
                stmt.setObject(2, teamProfile.getProfileId());
                stmt.setBigDecimal(3, teamProfile.getCostAllocation());
                stmt.setBigDecimal(4, teamProfile.getHourAllocation());
                logger.info("assignProfiles in teamDAO:: Assigning profile with profileId: " + teamProfile.getProfileId() + " to team with teamId: " + team.getTeamId());

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not assign Profiles to Team in Database.\n" + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean assignProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                INSERT INTO Teams_profiles (teamId, profileId, cost_allocation, hour_allocation) VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (Profile profile : profiles) {
                BigDecimal costAllocation = getCostAllocation(team.getTeamId(), profile.getProfileId());
                BigDecimal hourAllocation = getHourAllocation(team.getTeamId(), profile.getProfileId());
                stmt.setObject(1, team.getTeamId());
                stmt.setObject(2, profile.getProfileId());
                stmt.setBigDecimal(3, costAllocation);
                stmt.setBigDecimal(4, hourAllocation);
                logger.info("Assigning profile with profileId: " + profile.getProfileId() + " to team with teamId: " + team.getTeamId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not assign Profiles to Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateProfiles(Team team, List<Profile> profiles) throws Exception {
        String query = """
                UPDATE Teams_profiles SET cost_allocation = ?, hour_allocation = ? WHERE teamId = ? AND profileId = ?
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            TeamProfile teamProfile = new TeamProfile();
            for (Profile profile : profiles) {
                stmt.setBigDecimal(1, teamProfile.getCostAllocation());
                stmt.setBigDecimal(2, teamProfile.getHourAllocation());
                stmt.setObject(3, team.getTeamId());
                stmt.setObject(4, profile.getProfileId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update update profiles for Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                UPDATE Teams_profiles SET cost_allocation = ?, hour_allocation = ? WHERE teamId = ? AND profileId = ?
                """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            TeamProfile teamProfile = new TeamProfile();
            for (Profile profile : profiles) {
                stmt.setBigDecimal(1, teamProfile.getCostAllocation());
                stmt.setBigDecimal(2, teamProfile.getHourAllocation());
                stmt.setObject(3, team.getTeamId());
                stmt.setObject(4, profile.getProfileId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update update profiles for Team in Database.\n" + e.getMessage());
        }
        return true;
    }


    @Override
    public boolean updateProfile(UUID teamId, Profile profile) throws Exception {
        String query = """
                UPDATE Teams_profiles SET cost_allocation = ?, hour_allocation = ? WHERE teamId = ? AND profileId = ?
                """;
        TeamProfile teamProfile = new TeamProfile();
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBigDecimal(1, teamProfile.getCostAllocation());
            stmt.setBigDecimal(2, teamProfile.getHourAllocation());
            stmt.setObject(3, teamId);
            stmt.setObject(4, profile.getProfileId());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update update profile for Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateProfile(TransactionContext context, UUID teamId, TeamProfile teamProfile) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                UPDATE Teams_profiles SET cost_allocation = ?, hour_allocation = ? WHERE teamId = ? AND profileId = ?
                """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {

            stmt.setBigDecimal(1, teamProfile.getCostAllocation());
            stmt.setBigDecimal(2, teamProfile.getHourAllocation());
            stmt.setObject(3, teamProfile.getTeamId());
            stmt.setObject(4, teamProfile.getProfileId());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update update profile for Team in Database.\n" + e.getMessage());
        }
        return true;
    }


    @Override
    public boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception {
        String query = """
                 DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ? AND is_archived = FALSE;
                """; //false

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            for (Profile profile : profiles) {
                stmt.setObject(1, team.getTeamId());
                stmt.setObject(2, profile.getProfileId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not remove assigned profiles in Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean removeAssignedProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String query = """
                 DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ?;
                """; //false

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            for (Profile profile : profiles) {
                stmt.setObject(1, team.getTeamId());
                stmt.setObject(2, profile.getProfileId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not remove assigned profiles in Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public List<TeamProfile> _getTeamProfiles(UUID teamId) throws Exception {
        List<TeamProfile> teamProfiles = new ArrayList<>();
        String query = """
                SELECT tp.*, p.name, p.annual_cost, p.annual_hours, g.id, g.name as geo_name, g.predefined
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.profile_id = tp.profileId AND tp.teamId = ?
                INNER JOIN dbo.geography g on p.geography_id = g.id;
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID profileId = (UUID) rs.getObject("profileId");
                    String name = rs.getString("name");
                    BigDecimal dayRate = rs.getBigDecimal("day_rate_on_team");
                    BigDecimal costAllocation = rs.getBigDecimal("cost_allocation");
                    BigDecimal hourAllocation = rs.getBigDecimal("hour_allocation");
                    BigDecimal allocatedCostOnTeam = rs.getBigDecimal("allocated_cost_on_team");
                    BigDecimal allocatedHoursOnTeam = rs.getBigDecimal("allocated_hours_on_team");
                    BigDecimal annualCost = rs.getBigDecimal("annual_cost");
                    BigDecimal annualHours = rs.getBigDecimal("annual_hours");
                    Geography geography = new Geography(rs.getInt("id"),
                                                        rs.getString("geo_name"),
                                                        rs.getBoolean("predefined"));

                    teamProfiles.add(new TeamProfile(teamId, profileId, name, dayRate, costAllocation, hourAllocation,
                            allocatedCostOnTeam, allocatedHoursOnTeam, annualCost, annualHours, geography));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Could not get Team Profiles from Database.\n" + e.getMessage());
            }
            return teamProfiles;
        }
    }

    @Override
    public List<Profile> getTeamProfiles(UUID teamId) throws Exception {
        List<Profile> profiles = new ArrayList<>();
        String query = """
                SELECT p.*, tp.cost_allocation, tp.hour_allocation
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.profile_id = tp.profileID AND tp.teamID = ?;
                """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                Geography geography = new Geography();
                while (rs.next()) {
                    UUID id = (UUID) rs.getObject("profile_id");
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    geography.setId(rs.getInt("geography_id"));
                    geography.setName(rs.getString("geography.name"));
                    boolean resourceType = rs.getBoolean("resource_type");

                    BigDecimal annualCost = rs.getBigDecimal("annual_cost");
                    BigDecimal annualHours = rs.getBigDecimal("annual_hours");
                    BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");
                    BigDecimal effectivenessPercentage = rs.getBigDecimal("effectiveness");
                    BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
                    boolean is_archived = rs.getBoolean("is_archived");

                    BigDecimal costAllocation = rs.getBigDecimal("cost_allocation");
                    BigDecimal hourAllocation = rs.getBigDecimal("hour_allocation");

                    Profile profile = new Profile.Builder()
                            .setProfileId(id)
                            .setName(name)
                            .setCurrency(currency)
                            .setGeography(geography)
                            .setResourceType(resourceType)
                            .setAnnualCost(annualCost)
                            .setAnnualHours(annualHours)
                            .setHoursPerDay(hoursPerDay)
                            .setEffectivenessPercentage(effectivenessPercentage)
                            .setEffectiveWorkHours(effectiveWorkHours)
                            .setArchived(is_archived)
                            .build();

                    TeamProfile teamProfile = new TeamProfile();
                    teamProfile.setProfileId(id);
                    teamProfile.setTeamId(teamId);
                    teamProfile.setCostAllocation(costAllocation);
                    teamProfile.setHourAllocation(hourAllocation);

                    profile.setTeamProfile(teamProfile);
                    profiles.add(profile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Could not get Profiles for Team from Database.\n" + e.getMessage());
            }
            return profiles;
        }
    }

    @Override
    public List<TeamProfile> getTeamProfiles(List<Team> teams) throws Exception {
        String query = """
                SELECT tp.*, p.name, p.annual_cost, p.annual_hours, g.id, g.name, g.predefined
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.profile_id = tp.profileId
                INNER JOIN dbo.geography g on p.geography_id = g.id
                WHERE tp.teamId = ?;
                """;
        List<TeamProfile> teamProfiles = new ArrayList<>();
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Team team : teams) {
                stmt.setObject(1, team.getTeamId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        UUID profileId = (UUID) rs.getObject("profileId");
                        String name = rs.getString("name");
                        BigDecimal dayRate = rs.getBigDecimal("day_rate_on_team");
                        BigDecimal costAllocation = rs.getBigDecimal("cost_allocation");
                        BigDecimal hourAllocation = rs.getBigDecimal("hour_allocation");
                        BigDecimal allocatedCostOnTeam = rs.getBigDecimal("allocated_cost_on_team");
                        BigDecimal allocatedHoursOnTeam = rs.getBigDecimal("allocated_hours_on_team");
                        BigDecimal annualCost = rs.getBigDecimal("annual_cost");
                        BigDecimal annualHours = rs.getBigDecimal("annual_hours");
                        Geography geography = new Geography(rs.getInt("id"),
                                                            rs.getString("geo_name"),
                                                            rs.getBoolean("predefined"));

                        teamProfiles.add(new TeamProfile(team.getTeamId(), profileId, name, dayRate, costAllocation, hourAllocation,
                                allocatedCostOnTeam, allocatedHoursOnTeam, annualCost, annualHours, geography));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Could not get Team Profiles from Database.\n" + e.getMessage());
                }
            }
        }
        return teamProfiles;
    }

    @Override
    public Profile getTeamProfile(UUID teamId, UUID profileId) throws Exception {
        Profile profile = null;
        String query = """
                SELECT p.*, tp.cost_allocation, tp.hour_allocation
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.profile_id = tp.profileid WHERE tp.teamid = ? AND tp.profileid = ? AND p.is_archived = FALSE;
                """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                Geography geography = new Geography();
                if (rs.next()) {
                    UUID id = (UUID) rs.getObject("profile_id");
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    geography.setId(rs.getInt("geography_id"));
                    geography.setName(rs.getString("geography.name"));
                    boolean resourceType = rs.getBoolean("resource_type");

                    BigDecimal annualCost = rs.getBigDecimal("annual_cost");
                    BigDecimal effectivenessPercentage = rs.getBigDecimal("effectiveness");
                    BigDecimal annualHours = rs.getBigDecimal("annual_hours");
                    BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
                    BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");
                    boolean is_archived = rs.getBoolean("is_archived");

                    BigDecimal costAllocation = rs.getBigDecimal("cost_allocation");
                    BigDecimal hourAllocation = rs.getBigDecimal("hour_allocation");

                    profile = new Profile.Builder()
                            .setProfileId(id)
                            .setName(name)
                            .setCurrency(currency)
                            .setGeography(geography)
                            .setResourceType(resourceType)
                            .setAnnualCost(annualCost)
                            .setAnnualHours(annualHours)
                            .setHoursPerDay(hoursPerDay)
                            .setEffectivenessPercentage(effectivenessPercentage)
                            .setEffectiveWorkHours(effectiveWorkHours)
                            .setArchived(is_archived)
                            .build();

                    TeamProfile teamProfile = new TeamProfile();
                    teamProfile.setProfileId(id);
                    teamProfile.setTeamId(teamId);
                    teamProfile.setCostAllocation(costAllocation);
                    teamProfile.setHourAllocation(hourAllocation);

                    profile.setTeamProfile(teamProfile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Could not get Profile for Team from Database.\n" + e.getMessage());
            }

            if (profile == null)
                throw new Exception("Profile with ID " + profileId + " not found in team ID " + teamId + ".");
            return profile;
        }
    }

    @Override
    public boolean archive(List<Team> teams) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                for (Team team : teams)
                    archiveTeamWithTransaction(conn, team.getTeamId(), true);

                conn.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                conn.rollback();
                throw new Exception("Error occurred, rolling back..\n" + e.getMessage());
            }
        }
    }

    private void archiveTeamWithTransaction(Connection conn, UUID teamId, boolean archive) throws Exception {
        try {
            conn.setAutoCommit(false);
            archiveTeam(conn, teamId, archive);
            archiveTeamMembers(conn, teamId, archive);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean archive(UUID teamId, boolean archive) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                archiveTeam(conn, teamId, archive);
                List<Profile> profiles = getTeamProfiles(teamId);
                archiveTeamMembers(conn, teamId, archive);
                conn.commit();
                updateTotalAllocationOfProfilesOnDelete(profiles);
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                throw new Exception("Error occurred, rolling back..\n" + e.getMessage());
            }
        }
    }

    private void archiveTeamMembers(Connection conn, UUID teamId, boolean archive) throws Exception {
        String query = """
                DELETE FROM Teams_profiles WHERE teamId = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not archive Team Profiles in Database.\n" + e.getMessage());
        }
    }

    private void archiveTeam(Connection conn, UUID teamId, boolean archive) throws Exception {
        String query = """
                UPDATE Teams SET is_archived = ? WHERE id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, archive);
            stmt.setObject(2, teamId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not archive Team in Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Profile> canUnarchive(UUID teamId) throws Exception {
        List<Profile> profiles = new ArrayList<>();
        String query = """
                SELECT
                    p.*,
                    tp.cost_allocation AS cost_allocation,
                    tp.hour_allocation,
                    total_sum.total_cost_allocation,
                    total_sum.total_hour_allocation
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.id = tp.profileID AND tp.teamID = ?
                INNER JOIN
                    (SELECT
                        profileID,
                        SUM(cost_allocation) AS total_cost_allocation,
                        SUM(hour_allocation) AS total_hour_allocation
                     FROM dbo.Teams_profiles
                     WHERE teamID != ?
                       AND (SELECT is_archived FROM dbo.Profiles_data WHERE id = profileID) = 0
                       AND is_archived = FALSE
                     GROUP BY profileID
                    ) AS total_sum ON p.id = total_sum.profileID
                WHERE tp.is_archived = TRUE;
                    """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                Geography geography = new Geography();
                while (rs.next()) {
                    BigDecimal addedRate = rs.getBigDecimal("cost_allocation");
                    BigDecimal costAllocationTotal = rs.getBigDecimal("total_cost_allocation"); // den her ignorer det team man giver, så man skal plus sammen for at se om det er over 100

                    BigDecimal addedHour = rs.getBigDecimal("hourAllocation");
                    BigDecimal hourAllocationTotal = rs.getBigDecimal("total_hour_allocation");

                    boolean rateIsOK = costAllocationTotal.add(addedRate).compareTo(new BigDecimal("100")) <= 0;
                    boolean hoursIsOK = hourAllocationTotal.add(addedHour).compareTo(new BigDecimal("100")) <= 0;
                    if (rateIsOK && hoursIsOK)
                        continue;

                    UUID id = (UUID) rs.getObject("id");
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    geography.setId(rs.getInt("geography_id"));
                    geography.setName(rs.getString("geography.name"));
                    boolean resourceType = rs.getBoolean("resource_type");

                    // Profile
                    BigDecimal annualCost = rs.getBigDecimal("annual_salary");
                    BigDecimal effectivenessPercentage = rs.getBigDecimal("effectiveness");
                    BigDecimal annualHours = rs.getBigDecimal("annual_hours");
                    BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
                    BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");
                    boolean archived = rs.getBoolean("is_archived");

                    BigDecimal costAllocation = costAllocationTotal.add(addedRate);
                    BigDecimal hourAllocation = hourAllocationTotal.add(addedHour);

                    Profile profile = new Profile.Builder()
                            .setProfileId(id)
                            .setName(name)
                            .setCurrency(currency)
                            .setGeography(geography)
                            .setResourceType(resourceType)
                            .setAnnualCost(annualCost)
                            .setAnnualHours(annualHours)
                            .setHoursPerDay(hoursPerDay)
                            .setEffectivenessPercentage(effectivenessPercentage)
                            .setEffectiveWorkHours(effectiveWorkHours)
                            .setArchived(archived)
                            .build();

                    TeamProfile teamProfile = new TeamProfile();
                    teamProfile.setProfileId(id);
                    teamProfile.setTeamId(teamId);
                    teamProfile.setCostAllocation(costAllocation);
                    teamProfile.setHourAllocation(hourAllocation);

                    profile.setTeamProfile(teamProfile);
                    profiles.add(profile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Could not get Profiles to verify for unarchiving Team from Database.\n" + e.getMessage());
            }
            return profiles;
        }
    }

    @Override
    public boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws SQLException {
        String query = """
                DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ?
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, profileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not remove Profile from Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean removeProfileFromTeam(TransactionContext context, UUID teamId, UUID profileId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ?
                """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, profileId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not remove Profile from Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    public List<Profile> getTeamProfiles(TransactionContext context, UUID teamId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                SELECT *, g.name
                FROM dbo.Profiles
                INNER JOIN dbo.Teams_profiles ON dbo.Profiles.profile_id = dbo.Teams_profiles.profileId
                JOIN dbo.geography g ON g.id = dbo.profiles.geography_id
                WHERE dbo.Teams_profiles.teamId = ?;
                """;
        List<Profile> profiles = new ArrayList<>();
        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setObject(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(ResultSetMappers.profileResultSet(rs));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return profiles;
        }
    }

    public LocalDateTime getLastUpdated(UUID teamId) throws Exception {
        String query = """
                SELECT * FROM Teams_profiles_history WHERE team_id = ? ORDER BY updated_at DESC LIMIT 1;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("updated_at").toLocalDateTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get last updated for Team from Database.\n" + e.getMessage());
        }

        return null;
    }

    @Override
    public BigDecimal getCostAllocation(UUID teamId, UUID profileId) throws Exception {
        String sql = "SELECT cost_allocation FROM teams_profiles WHERE teamid = ? AND profileid = ?";
        try (Connection connection = dbConnector.connection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, teamId);
            pstmt.setObject(2, profileId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("cost_allocation");
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }

    }

    @Override
    public BigDecimal getHourAllocation(UUID teamId, UUID profileId) throws Exception {
        String sql = "SELECT hour_allocation FROM teams_profiles WHERE teamid = ? AND profileid = ?";
        try (Connection connection = dbConnector.connection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, teamId);
            pstmt.setObject(2, profileId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("hour_allocation");
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    @Override
    public BigDecimal getAllocatedCostOnTeam(UUID teamId, UUID profileId) throws Exception {
        String sql = "SELECT allocated_cost_on_team FROM teams_profiles WHERE teamid = ? AND profileid = ?";
        try (Connection conn = dbConnector.connection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setObject(1, teamId);
            preparedStatement.setObject(2, profileId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("allocated_cost_on_team");
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }
    }

     @Override
    public BigDecimal getAllocatedHoursOnTeam(UUID teamId, UUID profileId) throws Exception {
        String sql = "SELECT allocated_Hours_on_team FROM teams_profiles WHERE teamid = ? AND profileid = ?";
        try (Connection conn = dbConnector.connection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setObject(1, teamId);
            preparedStatement.setObject(2, profileId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("allocated_Hours_on_team");
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    @Override
    public void updateAllocatedCostAndHour(UUID teamId, List<TeamProfile> teamProfiles) throws SQLException{
        String sql = """
                UPDATE dbo.teams_profiles SET allocated_cost_on_team = ?, allocated_hours_on_team = ?, day_rate_on_team = ? 
                WHERE teamid = ? AND profileid = ?
                """;

        try (Connection conn = dbConnector.connection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for(TeamProfile teamProfile : teamProfiles) {
                preparedStatement.setBigDecimal(1, teamProfile.getAllocatedCostOnTeam());
                preparedStatement.setBigDecimal(2, teamProfile.getAllocatedHoursOnTeam());
                preparedStatement.setBigDecimal(3, teamProfile.getDayRateOnTeam());
                preparedStatement.setObject(4, teamId);
                preparedStatement.setObject(5, teamProfile.getProfileId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
    }

    @Override
    public void updateDayRateOnTeam(UUID teamid, UUID profileId, BigDecimal dayRate) throws SQLException {
        String sql = "UPDATE teams_profiles SET day_rate_on_team = ? WHERE teamid = ? AND profileid = ?";
        try (Connection conn = dbConnector.connection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, dayRate);
            pstmt.setObject(2, teamid);
            pstmt.setObject(3, profileId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateTeamsDayRate(UUID teamId, BigDecimal dayRate) throws SQLException {
        String sql = "UPDATE teams SET day_rate = ? WHERE id = ?";
        try (Connection conn = dbConnector.connection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, dayRate);
            pstmt.setObject(2, teamId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateTeamRateCostHours(Team team) throws SQLException {
        String sql = "UPDATE dbo.teams SET hourly_rate = ?, day_rate = ?, total_allocated_cost = ?, total_allocated_hours = ?  WHERE id = ?";
        try (Connection conn = dbConnector.connection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, team.getHourlyRate());
            pstmt.setBigDecimal(2, team.getDayRate());
            pstmt.setBigDecimal(3, team.getTotalAllocatedCost());
            pstmt.setBigDecimal(4, team.getTotalAllocatedHours());
            pstmt.setObject(5, team.getTeamId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void assignProfilesToTeam(UUID teamId, List<TeamProfile> teamProfiles) throws SQLException {
        String query = """
                INSERT INTO dbo.Teams_profiles (teamId, profileId, cost_allocation, allocated_cost_on_team, hour_allocation, allocated_hours_on_team, day_rate_on_team) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            for (TeamProfile teamProfile : teamProfiles) {
                stmt.setObject(1, teamId);
                stmt.setObject(2, teamProfile.getProfileId());
                stmt.setBigDecimal(3, teamProfile.getCostAllocation());
                stmt.setBigDecimal(4, teamProfile.getAllocatedCostOnTeam());
                stmt.setBigDecimal(5, teamProfile.getHourAllocation());
                stmt.setBigDecimal(6, teamProfile.getAllocatedHoursOnTeam());
                stmt.setBigDecimal(7, teamProfile.getDayRateOnTeam());

                logger.info("assignProfiles in teamDAO:: Assigning profile with profileId: " + teamProfile.getProfileId() + " to team with teamId: " + teamId);

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not assign Profiles to Team in Database.\n" + e.getMessage());
        }
    }

    @Override
    public void updateDayRateOnTeam(List<TeamProfile> teamProfiles) throws SQLException {
        String sql = "UPDATE dbo.teams_profiles SET day_rate_on_team = ? WHERE teamid = ? AND profileid = ?";
        try (Connection conn = dbConnector.connection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (TeamProfile teamProfile : teamProfiles) {
                preparedStatement.setBigDecimal(1, teamProfile.getDayRateOnTeam());
                preparedStatement.setObject(2, teamProfile.getTeamId());
                preparedStatement.setObject(3, teamProfile.getProfileId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    @Override
    public boolean removeProfilesFromTeam(UUID teamId, List<UUID> profileIds) throws SQLException {
        String query = """
               DELETE FROM dbo.Teams_profiles 
               WHERE teamId = ? AND profileId = ?
                """;
        try (Connection conn = dbConnector.connection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            for (UUID profileId : profileIds) {
                stmt.setObject(1, teamId);
                stmt.setObject(2, profileId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not remove Profiles from Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public TeamProfile updateTeamProfile(UUID teamId, TeamProfile teamProfile) throws SQLException {
        String query = """
                UPDATE dbo.Teams_profiles 
                SET cost_allocation = ?, 
                    hour_allocation = ?, 
                    allocated_cost_on_team = ?, 
                    allocated_hours_on_team = ?, 
                    day_rate_on_team = ?
                WHERE teamId = ? AND profileId = ?
                """;
        try (Connection conn = dbConnector.connection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, teamProfile.getCostAllocation());
            stmt.setBigDecimal(2, teamProfile.getHourAllocation());
            stmt.setBigDecimal(3, teamProfile.getAllocatedCostOnTeam());
            stmt.setBigDecimal(4, teamProfile.getAllocatedHoursOnTeam());
            stmt.setBigDecimal(5, teamProfile.getDayRateOnTeam());
            stmt.setObject(6, teamId);
            stmt.setObject(7, teamProfile.getProfileId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not update Team Profile in Database.\n" + e.getMessage());
        }
        return teamProfile;
    }

    @Override
    public boolean updateTeamProfile(List<TeamProfile> teamProfiles) throws SQLException {
        String query = """
                UPDATE dbo.Teams_profiles 
                SET cost_allocation = ?, 
                    hour_allocation = ?, 
                    allocated_cost_on_team = ?, 
                    allocated_hours_on_team = ?, 
                    day_rate_on_team = ?
                WHERE teamId = ? AND profileId = ?
                """;
        try (Connection conn = dbConnector.connection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            for (TeamProfile teamProfile : teamProfiles) {
                stmt.setBigDecimal(1, teamProfile.getCostAllocation());
                stmt.setBigDecimal(2, teamProfile.getHourAllocation());
                stmt.setBigDecimal(3, teamProfile.getAllocatedCostOnTeam());
                stmt.setBigDecimal(4, teamProfile.getAllocatedHoursOnTeam());
                stmt.setBigDecimal(5, teamProfile.getDayRateOnTeam());
                stmt.setObject(6, teamProfile.getTeamId());
                stmt.setObject(7, teamProfile.getProfileId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not update Team Profile in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public void updateTotalAllocationOfProfiles(List<TeamProfile> teamProfiles) throws SQLException {
        String query = """
                        SELECT SUM(cost_allocation) AS total_cost_allocation, SUM(hour_allocation) AS total_hour_allocation
                        FROM dbo.Teams_profiles
                        WHERE profileId = ?;
                        """;
        String command = """
                        UPDATE dbo.Profiles SET total_cost_allocation = ?, total_hour_allocation = ?
                        WHERE profile_id = ?;
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmtCost = conn.prepareStatement(query);
             PreparedStatement stmtUpdate = conn.prepareStatement(command)) {
            for (TeamProfile teamProfile : teamProfiles) {
                stmtCost.setObject(1, teamProfile.getProfileId());

                try (ResultSet rs = stmtCost.executeQuery()) {
                    if (rs.next()) {
                        stmtUpdate.setBigDecimal(1, rs.getBigDecimal("total_cost_allocation"));
                        stmtUpdate.setBigDecimal(2, rs.getBigDecimal("total_hour_allocation"));
                        stmtUpdate.setObject(3, teamProfile.getProfileId());
                        stmtUpdate.addBatch();
                    }
                }
            }
            stmtUpdate.executeBatch();
        }
    }

    @Override
    public void updateTotalAllocationOfProfilesOnDelete(List<Profile> profiles) throws SQLException {
        String query = """
                        SELECT SUM(cost_allocation) AS total_cost_allocation, SUM(hour_allocation) AS total_hour_allocation
                        FROM dbo.Teams_profiles
                        WHERE profileId = ?;
                        """;
        String command = """
                        UPDATE dbo.Profiles SET total_cost_allocation = ?, total_hour_allocation = ?
                        WHERE profile_id = ?;
                """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmtCost = conn.prepareStatement(query);
             PreparedStatement stmtUpdate = conn.prepareStatement(command)) {
            for (Profile profile : profiles) {
                stmtCost.setObject(1, profile.getProfileId());

                try (ResultSet rs = stmtCost.executeQuery()) {
                    if (rs.next()) {
                        stmtUpdate.setBigDecimal(1, rs.getBigDecimal("total_cost_allocation"));
                        stmtUpdate.setBigDecimal(2, rs.getBigDecimal("total_hour_allocation"));
                        stmtUpdate.setObject(3, profile.getProfileId());
                        stmtUpdate.addBatch();
                    }
                }
            }
            stmtUpdate.executeBatch();
        }
    }

    @Override
    public List<TeamProfile> saveTeamProfiles(UUID teamId, List<TeamProfile> teamProfiles) throws SQLException {
        String insertSql = """
            INSERT INTO teams_profiles (teamid, profileid, cost_allocation, hour_allocation, allocated_cost_on_team, allocated_hours_on_team)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        String selectSql = """
                SELECT tp.*, p.annual_cost, p.annual_hours
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.profile_id = tp.profileId AND tp.teamId = ?;
            """;
        List<TeamProfile> insertedProfiles = new ArrayList<>();

        try (Connection conn = dbConnector.connection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            for (TeamProfile teamProfile : teamProfiles) {
                insertStmt.setObject(1, teamId);
                insertStmt.setObject(2, teamProfile.getProfileId());
                insertStmt.setBigDecimal(3, teamProfile.getCostAllocation());
                insertStmt.setBigDecimal(4, teamProfile.getHourAllocation());
                insertStmt.setBigDecimal(5, teamProfile.getAllocatedCostOnTeam());
                insertStmt.setBigDecimal(6, teamProfile.getAllocatedHoursOnTeam());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            selectStmt.setObject(1, teamId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    TeamProfile insertedProfile = new TeamProfile();
                    insertedProfile.setTeamId((UUID) rs.getObject("teamid"));
                    insertedProfile.setProfileId((UUID) rs.getObject("profileid"));
                    insertedProfile.setCostAllocation(rs.getBigDecimal("cost_allocation"));
                    insertedProfile.setHourAllocation(rs.getBigDecimal("hour_allocation"));
                    insertedProfile.setAllocatedCostOnTeam(rs.getBigDecimal("allocated_cost_on_team"));
                    insertedProfile.setAllocatedHoursOnTeam(rs.getBigDecimal("allocated_hours_on_team"));
                    insertedProfile.setAnnualCost(rs.getBigDecimal("annual_cost"));
                    insertedProfile.setAnnualHours(rs.getBigDecimal("annual_hours"));
                    insertedProfiles.add(insertedProfile);
                }
            }
        }
        return insertedProfiles;
    }
}