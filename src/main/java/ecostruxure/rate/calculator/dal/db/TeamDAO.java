package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;
import ecostruxure.rate.calculator.dal.dao.ITeamDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO implements ITeamDAO {
    private final DBConnector dbConnector;

    public TeamDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }

    @Override
    public List<Team> all() throws Exception {
        List<Team> teams = new ArrayList<>();

        String query = """
                       SELECT * FROM Teams ORDER BY id DESC
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                BigDecimal markup = rs.getBigDecimal("markup");
                BigDecimal grossMargin = rs.getBigDecimal("gross_margin");
                boolean archived = rs.getBoolean("archived");

                teams.add(new Team(id, name, markup, grossMargin, archived));
            }

            return teams;
        } catch (Exception e) {
            throw new Exception("Could not get all Teams from Database.\n" + e.getMessage());
        }
    }

    @Override
    public Team get(int id) throws Exception {
        Team team = null;

        String query = """
                       SELECT * FROM Teams WHERE id = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    BigDecimal markup = rs.getBigDecimal("markup");
                    BigDecimal grossMargin = rs.getBigDecimal("gross_margin");
                    boolean archived = rs.getBoolean("archived");

                    team = new Team(id, name, markup, grossMargin, archived);
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not get Team from Database. Team ID: " + id + "\n" + e.getMessage());
        }

        if (team == null) throw new Exception("Team with ID " + id + " not found.");

        return team;
    }

    @Override
    public Team create(Team team) throws Exception {
        String query = """
                       INSERT INTO Teams (name, markup, gross_margin) VALUES (?, ?, ?)
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, team.name());
            stmt.setBigDecimal(2, team.markup());
            stmt.setBigDecimal(3, team.grossMargin());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    team.id(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
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
            stmt.setString(1, team.name());
            stmt.setBigDecimal(2, team.markup());
            stmt.setBigDecimal(3, team.grossMargin());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    team.id(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not create Team in Database.\n" + e.getMessage());
        }

        return team;
    }

    @Override
    public boolean update(Team team) throws Exception {
        String query = """
                       UPDATE Teams SET name = ? WHERE id = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, team.name());
            stmt.setInt(2, team.id());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
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
            stmt.setBigDecimal(1, team.markup());
            stmt.setBigDecimal(2, team.grossMargin());
            stmt.setInt(3, team.id());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not update markup for Team in Database.\n" + e.getMessage());
        }
    }

    @Override
    public boolean assignProfiles(Team team, List<Profile> profiles) throws Exception {
        String query = """
                       INSERT INTO Teams_profiles (teamId, profileId, utilization_rate, utilization_hours) VALUES (?, ?, ?, ?)
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            for (Profile profile : profiles) {
                stmt.setInt(1, team.id());
                stmt.setInt(2, profile.id());
                stmt.setBigDecimal(3, profile.utilizationRate());
                stmt.setBigDecimal(4, profile.utilizationHours());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Could not assign Profiles to Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean assignProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                       INSERT INTO Teams_profiles (teamId, profileId, utilization_rate, utilization_hours) VALUES (?, ?, ?, ?)
                       """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (Profile profile : profiles) {
                stmt.setInt(1, team.id());
                stmt.setInt(2, profile.id());
                stmt.setBigDecimal(3, profile.utilizationRate());
                stmt.setBigDecimal(4, profile.utilizationHours());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            throw new Exception("Could not assign Profiles to Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateProfiles(Team team, List<Profile> profiles) throws Exception {
        String query = """
                       UPDATE Teams_profiles SET utilization_rate = ?, utilization_hours = ? WHERE teamId = ? AND profileId = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            for (Profile profile : profiles) {
                stmt.setBigDecimal(1, profile.utilizationRate());
                stmt.setBigDecimal(2, profile.utilizationHours());
                stmt.setInt(3, team.id());
                stmt.setInt(4, profile.id());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Could not update update profiles for Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                       UPDATE Teams_profiles SET utilization_rate = ?, utilization_hours = ? WHERE teamId = ? AND profileId = ?
                       """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {

            for (Profile profile : profiles) {
                stmt.setBigDecimal(1, profile.utilizationRate());
                stmt.setBigDecimal(2, profile.utilizationHours());
                stmt.setInt(3, team.id());
                stmt.setInt(4, profile.id());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            throw new Exception("Could not update update profiles for Team in Database.\n" + e.getMessage());
        }
        return true;
    }


    @Override
    public boolean updateProfile(int teamId, Profile profile) throws Exception {
        String query = """
                       UPDATE Teams_profiles SET utilization_rate = ?, utilization_hours = ? WHERE teamId = ? AND profileId = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBigDecimal(1, profile.utilizationRate());
            stmt.setBigDecimal(2, profile.utilizationHours());
            stmt.setInt(3, teamId);
            stmt.setInt(4, profile.id());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not update update profile for Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateProfile(TransactionContext context, int teamId, Profile profile) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                       UPDATE Teams_profiles SET utilization_rate = ?, utilization_hours = ? WHERE teamId = ? AND profileId = ?
                       """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {

            stmt.setBigDecimal(1, profile.utilizationRate());
            stmt.setBigDecimal(2, profile.utilizationHours());
            stmt.setInt(3, teamId);
            stmt.setInt(4, profile.id());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not update update profile for Team in Database.\n" + e.getMessage());
        }
        return true;
    }


    @Override
    public boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception {
        String query = """
                        DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ? AND archived = FALSE;
                       """; //false

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            for (Profile profile : profiles) {
                stmt.setInt(1, team.id());
                stmt.setInt(2, profile.id());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Could not remove assigned profiles in Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean removeAssignedProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String query = """
                        DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ? AND archived = FALSE;
                       """; //false

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            for (Profile profile : profiles) {
                stmt.setInt(1, team.id());
                stmt.setInt(2, profile.id());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            throw new Exception("Could not remove assigned profiles in Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public List<Profile> getTeamProfiles(int teamId) throws Exception {
        List<Profile> profiles = new ArrayList<>();
        String query = """
                        SELECT p.*, pd.*, tp.utilization_rate, tp.utilization_hours
                        FROM dbo.Profiles p
                        INNER JOIN dbo.Teams_profiles tp ON p.id = tp.profileID AND tp.teamID = ?
                        INNER JOIN dbo.Profiles_data pd ON p.id = pd.id AND pd.archived = FALSE;                    
                        """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    int geography = rs.getInt("geography");
                    boolean overhead = rs.getBoolean("overhead");
                    boolean archived = rs.getBoolean("archived");

                    // Profile
                    BigDecimal annualSalary = rs.getBigDecimal("annual_salary");
                    BigDecimal fixedAnnualAmount = rs.getBigDecimal("fixed_annual_amount");
                    BigDecimal overheadMultiplier = rs.getBigDecimal("overhead_multiplier");
                    BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
                    BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");

                    BigDecimal utilizationRate = rs.getBigDecimal("utilization_rate");
                    BigDecimal utilizationHours = rs.getBigDecimal("utilization_hours");

                    Profile profile = new Profile(
                            id,
                            name,
                            currency,
                            annualSalary,
                            fixedAnnualAmount,
                            overheadMultiplier,
                            geography,
                            effectiveWorkHours,
                            utilizationRate,
                            utilizationHours,
                            overhead,
                            hoursPerDay,
                            archived
                    );

                    profiles.add(profile);
                }
            } catch (Exception e) {
                throw new Exception("Could not get Profiles for Team from Database.\n" + e.getMessage());
            }
            return profiles;
        }
    }

    @Override
    public Profile getTeamProfile(int teamId, int profileId) throws Exception {
        Profile profile = null;
        String query = """
                        SELECT p.*, pd.*, tp.utilization_rate, tp.utilization_hours
                        FROM dbo.Profiles p
                        INNER JOIN dbo.Teams_profiles tp ON p.id = tp.profileID AND tp.teamID = ? AND tp.profileID = ?
                        INNER JOIN dbo.Profiles_data pd ON p.id = pd.id AND pd.archived = FALSE;                    
                        """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    int geography = rs.getInt("geography");
                    boolean overhead = rs.getBoolean("overhead");
                    boolean archived = rs.getBoolean("archived");

                    // Profile
                    BigDecimal annualSalary = rs.getBigDecimal("annual_salary");
                    BigDecimal fixedAnnualAmount = rs.getBigDecimal("fixed_annual_amount");
                    BigDecimal overheadMultiplier = rs.getBigDecimal("overhead_multiplier");
                    BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
                    BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");

                    BigDecimal utilizationRate = rs.getBigDecimal("utilization_rate");
                    BigDecimal utilizationHours = rs.getBigDecimal("utilization_hours");

                    return new Profile(
                            id,
                            name,
                            currency,
                            annualSalary,
                            fixedAnnualAmount,
                            overheadMultiplier,
                            geography,
                            effectiveWorkHours,
                            utilizationRate,
                            utilizationHours,
                            overhead,
                            hoursPerDay,
                            archived
                    );
                }
            } catch (Exception e) {
                throw new Exception("Could not get Profile for Team from Database.\n" + e.getMessage());
            }

            if (profile == null) throw new Exception("Profile with ID " + profileId + " not found in team ID " + teamId + ".");
            return profile;
        }
    }

    @Override
    public boolean archive(List<Team> teams) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                for (Team team : teams)
                    archiveTeamWithTransaction(conn, team.id(), true);

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw new Exception("Error occurred, rolling back..\n" + e.getMessage());
            }
        }
    }

    private void archiveTeamWithTransaction(Connection conn, int teamId, boolean archive) throws Exception {
        try {
            conn.setAutoCommit(false);
            archiveTeam(conn, teamId, archive);
            archiveTeamMembers(conn, teamId, archive);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean archive(int teamId, boolean archive) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                 archiveTeam(conn, teamId, archive);
                 archiveTeamMembers(conn, teamId, archive);
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw new Exception("Error occured, rolling back..\n" + e.getMessage());
            }
        }
    }

    private void archiveTeamMembers(Connection conn, int teamId, boolean archive) throws Exception {
        String query = """
                       UPDATE Teams_profiles SET archived = ? WHERE teamId = ?
                       """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, archive);
            stmt.setInt(2, teamId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not archive Team Profiles in Database.\n" + e.getMessage());
        }
    }

    private void archiveTeam(Connection conn, int teamId, boolean archive) throws Exception {
        String query = """
                       UPDATE Teams SET archived = ? WHERE id = ?
                       """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, archive);
            stmt.setInt(2, teamId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not archive Team in Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Profile> canUnarchive(int teamId) throws Exception {
        List<Profile> profiles = new ArrayList<>();
        String query = """
                SELECT
                    p.*,
                    pd.*,
                    tp.utilization_rate AS utilization_rate,
                    tp.utilization_hours,
                    total_sum.total_utilization_rate,
                    total_sum.total_utilization_hours
                FROM dbo.Profiles p
                INNER JOIN dbo.Teams_profiles tp ON p.id = tp.profileID AND tp.teamID = ?
                INNER JOIN dbo.Profiles_data pd ON p.id = pd.id
                INNER JOIN
                    (SELECT
                        profileID,
                        SUM(utilization_rate) AS total_utilization_rate,
                        SUM(utilization_hours) AS total_utilization_hours
                     FROM dbo.Teams_profiles
                     WHERE teamID != ?
                       AND (SELECT archived FROM dbo.Profiles_data WHERE id = profileID) = 0
                       AND archived = FALSE
                     GROUP BY profileID
                    ) AS total_sum ON p.id = total_sum.profileID
                WHERE tp.archived = TRUE;
                    """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BigDecimal utilizationRate = rs.getBigDecimal("utilization_rate");
                    BigDecimal utilizationRateTotal = rs.getBigDecimal("total_utilization_rate"); // den her ignorer det team man giver, så man skal plus sammen for at se om det er over 100

                    BigDecimal utilizationHours = rs.getBigDecimal("utilization_hours");
                    BigDecimal utilizationHoursTotal = rs.getBigDecimal("total_utilization_hours");

                    boolean rateIsOK = utilizationRateTotal.add(utilizationRate).compareTo(new BigDecimal("100")) <= 0;
                    boolean hoursIsOK = utilizationHoursTotal.add(utilizationHours).compareTo(new BigDecimal("100")) <= 0;
                    if (rateIsOK && hoursIsOK)
                        continue;

                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    int geography = rs.getInt("geography");
                    boolean overhead = rs.getBoolean("overhead");
                    boolean archived = rs.getBoolean("archived");

                    // Profile
                    BigDecimal annualSalary = rs.getBigDecimal("annual_salary");
                    BigDecimal fixedAnnualAmount = rs.getBigDecimal("fixed_annual_amount");
                    BigDecimal overheadMultiplier = rs.getBigDecimal("overhead_multiplier");
                    BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
                    BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");

                    BigDecimal addedRate = utilizationRateTotal.add(utilizationRate);
                    BigDecimal addedHours = utilizationHoursTotal.add(utilizationHours);

                    Profile profile = new Profile(
                            id,
                            name,
                            currency,
                            annualSalary,
                            fixedAnnualAmount,
                            overheadMultiplier,
                            geography,
                            effectiveWorkHours,
                            addedRate,
                            addedHours,
                            overhead,
                            hoursPerDay,
                            archived
                    );

                    profiles.add(profile);
                }
            } catch (Exception e) {
                throw new Exception("Could not get Profiles to verify for unarchiving Team from Database.\n" + e.getMessage());
            }
            return profiles;
        }
    }

    @Override
    public boolean removeProfileFromTeam(int teamId, int profileId) throws Exception {
        String query = """
                       DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, profileId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not remove Profile from Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean removeProfileFromTeam(TransactionContext context, int teamId, int profileId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                       DELETE FROM Teams_profiles WHERE teamId = ? AND profileId = ?
                       """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, profileId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Could not remove Profile from Team in Database.\n" + e.getMessage());
        }
        return true;
    }

    public List<Profile> getTeamProfiles(TransactionContext context, int teamId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                        SELECT * FROM dbo.Profiles
                        INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                        INNER JOIN dbo.Teams_profiles ON dbo.Profiles.id = dbo.Teams_profiles.profileId
                        WHERE dbo.Teams_profiles.teamId = ?;
                        """;
        List<Profile> profiles = new ArrayList<>();
        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setInt(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(ResultSetMappers.profileResultSet(rs));
                }
            }

            return profiles;
        }
    }

    public LocalDateTime getLastUpdated(int teamId) throws Exception {
        String query = """
                SELECT * FROM Teams_profiles_history WHERE team_id = ? ORDER BY updated_at DESC LIMIT 1;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("updated_at").toLocalDateTime();
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not get last updated for Team from Database.\n" + e.getMessage());
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        TeamDAO teamDAO = new TeamDAO();
        ProfileDAO profileDAO = new ProfileDAO();
        System.out.println(teamDAO.canUnarchive(20));
        System.out.println(teamDAO.canUnarchive(19));

        System.out.println(profileDAO.getProfileHourUtilizationForTeam(20, 20));
        System.out.println(profileDAO.getProfileRateUtilizationForTeam(20, 19));
    }
}
