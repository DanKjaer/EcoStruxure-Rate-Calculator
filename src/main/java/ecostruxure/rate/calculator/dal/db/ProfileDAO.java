package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;
import ecostruxure.rate.calculator.dal.dao.IProfileDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAO implements IProfileDAO {
    private final DBConnector dbConnector;

    public ProfileDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }

    private Profile profileResultSet(ResultSet rs) throws SQLException {
        // ProfileData
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

        return new Profile(
                id,
                name,
                currency,
                annualSalary,
                fixedAnnualAmount,
                overheadMultiplier,
                geography,
                effectiveWorkHours,
                overhead,
                hoursPerDay,
                archived
        );
    }

    // Bemærk: Kan nok laves smartere, men indtil videre er dette funktionelt og opdelt.
    @Override
    public Profile create(Profile profile) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                Profile createdProfile = createProfile(conn, profile);
                ProfileData createdProfileData = createProfileData(conn, createdProfile, profile.profileData());
                createdProfile.profileData(createdProfileData);
                conn.commit();
                return createdProfile;
            } catch (Exception ex) {
                conn.rollback();
                throw new Exception("Error occured, rolling back..\n" + ex.getMessage());
            }
        }

    }

    @Override
    public List<Profile> all() throws Exception {
        List<Profile> profiles = new ArrayList<>();

        String query = """
                       SELECT * FROM dbo.Profiles
                       INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                       ORDER BY dbo.Profiles.id DESC
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                profiles.add(profileResultSet(rs));
            }

            return profiles;
        } catch (Exception e) {
            throw new Exception("Could not get all Profiles from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Profile> allWithUtilization() throws Exception {
        List<Profile> profiles = new ArrayList<>();

        String query = """
                        SELECT p.*, pd.*,
                           COALESCE(tp.utilization_rate_total, 0) AS utilization_rate_total,
                           COALESCE(tp.utilization_hours_total, 0) AS utilization_hours_total
                        FROM dbo.Profiles p
                        INNER JOIN dbo.Profiles_data pd ON p.id = pd.id
                        LEFT JOIN (
                            SELECT profileID,
                                   SUM(utilization_rate) AS utilization_rate_total,
                                   SUM(utilization_hours) AS utilization_hours_total
                            FROM dbo.Teams_profiles
                            WHERE archived = 0
                            GROUP BY profileID
                        ) tp ON p.id = tp.profileID
                        WHERE pd.archived = 0
                        ORDER BY p.id DESC;                     
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Profile profile = profileResultSet(rs);
                profile.utilizationRate(rs.getBigDecimal("utilization_rate_total"));
                profile.utilizationHours(rs.getBigDecimal("utilization_hours_total"));
                profiles.add(profile);
            }

            return profiles;
        } catch (Exception e) {
            throw new Exception("Could not get all Profiles from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Profile> allWithUtilizationByTeam(int teamId) throws Exception {
        List<Profile> profiles = new ArrayList<>();

        String query = """
                        SELECT p.*, pd.*,
                               COALESCE(tp.utilization_rate_total, 0) AS utilization_rate_total,
                               COALESCE(tp.utilization_hours_total, 0) AS utilization_hours_total
                        FROM dbo.Profiles p
                        INNER JOIN dbo.Profiles_data pd ON p.id = pd.id
                        LEFT JOIN (
                            SELECT profileID,
                                   SUM(utilization_rate) AS utilization_rate_total,
                                   SUM(utilization_hours) AS utilization_hours_total
                            FROM dbo.Teams_profiles
                            WHERE teamId = ?
                            GROUP BY profileID
                        ) tp ON p.id = tp.profileID
                        LEFT JOIN dbo.Teams_profiles tp2 ON p.id = tp2.profileId
                        WHERE tp2.teamId = ?
                        ORDER BY p.id DESC;
                    """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Profile profile = profileResultSet(rs);
                    profile.utilizationRate(rs.getBigDecimal("utilization_rate_total"));
                    profile.utilizationHours(rs.getBigDecimal("utilization_hours_total"));
                    profiles.add(profile);
                }
            }

            return profiles;
        } catch (Exception e) {
            throw new Exception("Could not get all Profiles from Database.\n" + e.getMessage());
        }
    }

    @Override
    public Profile get(int id) throws Exception {
        Profile profile = null;

        String query = """
                       SELECT * FROM dbo.Profiles
                       INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                       WHERE dbo.Profiles.id = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile = profileResultSet(rs);
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not get Profile from Database. Profile ID: " + id + "\n" + e.getMessage());
        }

        if (profile == null) throw new Exception("Profile with ID " + id + " not found.");

        return profile;
    }

    @Override
    public Profile get(TransactionContext context, int id) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        Profile profile = null;

        String query = """
                       SELECT * FROM dbo.Profiles
                       INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                       WHERE dbo.Profiles.id = ?
                       """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile = profileResultSet(rs);
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not get Profile from Database. Profile ID: " + id + "\n" + e.getMessage());
        }

        if (profile == null) throw new Exception("Profile with ID " + id + " not found.");

        return profile;
    }

    private Profile createProfile(Connection connection, Profile profile) throws Exception {

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO dbo.Profiles (annual_salary, fixed_annual_amount, overhead_multiplier, effective_work_hours, hours_per_day) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, profile.annualSalary());
            stmt.setBigDecimal(2, profile.fixedAnnualAmount());
            stmt.setBigDecimal(3, profile.overheadMultiplier());
            stmt.setBigDecimal(4, profile.effectiveWorkHours());
            stmt.setBigDecimal(5, profile.hoursPerDay());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
                throw new SQLException("Creating profile failed, no rows affected.");

            // Få ID fra oprettet profil
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    profile.id(generatedKeys.getInt(1));
                    return profile;
                } else {
                    throw new SQLException("Creating profile failed, no ID obtained.");
                }
            }
        }
    }

    private ProfileData createProfileData(Connection connection, Profile profile, ProfileData profileData) throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO dbo.Profiles_data (id, name, currency, geography, overhead, archived) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, profile.id());
            stmt.setString(2, profileData.name());
            stmt.setString(3, profileData.currency());
            stmt.setInt(4, profileData.geography());
            stmt.setBoolean(5, profileData.overhead());
            stmt.setBoolean(6, profileData.archived()); // Er dog false fra default, måske fjernes...

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
                throw new SQLException("Creating profile data failed, no rows affected.");

            return profileData;
        }
    }

    public List<Profile> allByCountry(Country country) throws Exception {
        return allByCountry(country.code());
    }

    public List<Profile> allByCountry(String countryCode) throws Exception {
        ArrayList<Profile> profilesBycountry = new ArrayList<>();
        String query = """
                            SELECT * FROM dbo.Profiles
                            INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                            INNER JOIN dbo.Geography_countries ON dbo.Profiles_data.geography = dbo.Geography_countries.geography
                            WHERE dbo.Geography_countries.code = ?;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, countryCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profilesBycountry.add(profileResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profileByCountry failed\n." + e.getMessage());
        }

        return profilesBycountry;
    }

    @Override
    public List<Profile> allByTeam(Team team) throws Exception {
        return allByTeam(team.id());
    }

    @Override
    public List<Profile> allByTeam(int teamId) throws Exception {
        ArrayList<Profile> profilesByTeam = new ArrayList<>();
        String query = """
                        SELECT * FROM dbo.Profiles
                        INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                        INNER JOIN dbo.Teams_profiles ON dbo.Profiles.id = dbo.Teams_profiles.profileId
                        WHERE dbo.Teams_profiles.teamId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profilesByTeam.add(profileResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profileByTeam failed\n." + e.getMessage());
        }

        return profilesByTeam;
    }

    @Override
    public List<Profile> allByGeography(Geography geography) throws Exception {
        return allByGeography(geography.id());
    }

    @Override
    public List<Profile> allByGeography(int geographyId) throws Exception {
        ArrayList<Profile> profilesByGeography = new ArrayList<>();
        String query = """
                        SELECT * FROM dbo.Profiles
                        INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                        WHERE dbo.Profiles_data.geography = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, geographyId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profilesByGeography.add(profileResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profileByGeography failed\n." + e.getMessage());
        }

        return profilesByGeography;
    }

    @Override
    public BigDecimal getTotalRateUtilization(int profileId) throws Exception {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        String query = """
                        SELECT SUM(utilization_rate) AS total_utilization
                        FROM Teams_profiles
                        WHERE profileId = ? AND Teams_profiles.archived = 0;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_utilization");
                    if (column != null)
                        totalUtilization = rs.getBigDecimal("total_utilization");
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting total utilization rate for profile failed\n." + e.getMessage());
        }

        return totalUtilization;
    }

    @Override
    public BigDecimal getTotalHourUtilization(int profileId) throws Exception {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        String query = """
                        SELECT SUM(utilization_hours) AS total_utilization
                        FROM Teams_profiles
                        WHERE profileId = ? AND Teams_profiles.archived = 0;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_utilization");
                    if (column != null)
                        totalUtilization = rs.getBigDecimal("total_utilization");
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting total utilization hour for profile failed\n." + e.getMessage());
        }

        return totalUtilization;
    }

    @Override
    public BigDecimal getProfileRateUtilizationForTeam(int profileId, int teamId) throws Exception {
        BigDecimal utilization = BigDecimal.ZERO;
        String query = """
                        SELECT utilization_rate AS total_utilization
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.setInt(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_utilization");
                    if (column != null)
                        utilization = rs.getBigDecimal("total_utilization");
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profile utilization for team failed\n." + e.getMessage());
        }

        return utilization;
    }

    @Override
    public BigDecimal getProfileHourUtilizationForTeam(int profileId, int teamId) throws Exception {
        BigDecimal utilization = BigDecimal.ZERO;
        String query = """
                        SELECT utilization_hours AS total_utilization
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.setInt(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_utilization");
                    if (column != null)
                        utilization = rs.getBigDecimal("total_utilization");
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profile utilization for team failed\n." + e.getMessage());
        }

        return utilization;
    }

    @Override
    public BigDecimal getProfileRateUtilizationForTeam(TransactionContext context, int profileId, int teamId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        BigDecimal utilization = BigDecimal.ZERO;
        String query = """
                        SELECT utilization_rate AS total_utilization
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.setInt(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_utilization");
                    if (column != null)
                        utilization = rs.getBigDecimal("total_utilization");
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profile utilization for team failed\n." + e.getMessage());
        }

        return utilization;
    }

    @Override
    public BigDecimal getProfileHourUtilizationForTeam(TransactionContext context, int profileId, int teamId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        BigDecimal utilization = BigDecimal.ZERO;
        String query = """
                        SELECT utilization_hours AS total_utilization
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.setInt(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_utilization");
                    if (column != null)
                        utilization = rs.getBigDecimal("total_utilization");
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting profile utilization for team failed\n." + e.getMessage());
        }

        return utilization;
    }

    @Override
    public List<Team> getTeams(Profile profile) throws Exception {
        List<Team> teams = new ArrayList<>();

        String query = """
                       SELECT *
                       FROM Teams_profiles
                       INNER JOIN Teams ON Teams_profiles.teamId = Teams.id
                       WHERE Teams_profiles.profileId = ? AND Teams.archived = 0;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profile.id());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team(
                            rs.getInt("teamId"),
                            rs.getString("name"),
                            rs.getBigDecimal("markup"),
                            rs.getBigDecimal("gross_margin"),
                            rs.getBoolean("archived")
                    );
                    teams.add(team);
                }
            }
        } catch (Exception e) {
            throw new Exception("Getting teams for profile failed\n." + e.getMessage());
        }

        return teams;
    }

    @Override
    public boolean update(Profile profile) throws Exception {
        String updateProfileSQL = """
                                  UPDATE dbo.Profiles
                                  SET annual_salary = ?, fixed_annual_amount = ?, overhead_multiplier = ?, effective_work_hours = ?, hours_per_day = ?
                                  WHERE id = ?;
                                  """;

        String updateProfileDataSQL = """
                                      UPDATE dbo.Profiles_data
                                      SET name = ?, currency = ?, geography = ?, overhead = ?, archived = ?, updated_at = GETDATE()
                                      WHERE id = ?;
                                      """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement updateProfileStmt = conn.prepareStatement(updateProfileSQL);
             PreparedStatement updateProfileDataStmt = conn.prepareStatement(updateProfileDataSQL)) {
            conn.setAutoCommit(false);

            updateProfileStmt.setBigDecimal(1, profile.annualSalary());
            updateProfileStmt.setBigDecimal(2, profile.fixedAnnualAmount());
            updateProfileStmt.setBigDecimal(3, profile.overheadMultiplier());
            updateProfileStmt.setBigDecimal(4, profile.effectiveWorkHours());
            updateProfileStmt.setBigDecimal(5, profile.hoursPerDay());
            updateProfileStmt.setInt(6, profile.id());
            updateProfileStmt.executeUpdate();

            updateProfileDataStmt.setString(1, profile.profileData().name());
            updateProfileDataStmt.setString(2, profile.profileData().currency());
            updateProfileDataStmt.setInt(3, profile.profileData().geography());
            updateProfileDataStmt.setBoolean(4, profile.profileData().overhead());
            updateProfileDataStmt.setBoolean(5, profile.profileData().archived());
            updateProfileDataStmt.setInt(6, profile.id());
            updateProfileDataStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            try (Connection conn = dbConnector.connection()) {
                conn.rollback();
                return false;
            } catch (Exception ex) {
                throw new Exception("Could not update Profile in Database.\n" + ex.getMessage());
            }
        }
    }

    @Override
    public boolean update(TransactionContext context, Profile profile) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String updateProfileSQL = """
                                  UPDATE dbo.Profiles
                                  SET annual_salary = ?, fixed_annual_amount = ?, overhead_multiplier = ?, effective_work_hours = ?, hours_per_day = ?
                                  WHERE id = ?;
                                  """;

        String updateProfileDataSQL = """
                                      UPDATE dbo.Profiles_data
                                      SET name = ?, currency = ?, geography = ?, overhead = ?, archived = ?, updated_at = GETDATE()
                                      WHERE id = ?;
                                      """;

        try (PreparedStatement updateProfileStmt = sqlContext.connection().prepareStatement(updateProfileSQL);
             PreparedStatement updateProfileDataStmt = sqlContext.connection().prepareStatement(updateProfileDataSQL)) {
            updateProfileStmt.setBigDecimal(1, profile.annualSalary());
            updateProfileStmt.setBigDecimal(2, profile.fixedAnnualAmount());
            updateProfileStmt.setBigDecimal(3, profile.overheadMultiplier());
            updateProfileStmt.setBigDecimal(4, profile.effectiveWorkHours());
            updateProfileStmt.setBigDecimal(5, profile.hoursPerDay());
            updateProfileStmt.setInt(6, profile.id());
            updateProfileStmt.executeUpdate();

            updateProfileDataStmt.setString(1, profile.profileData().name());
            updateProfileDataStmt.setString(2, profile.profileData().currency());
            updateProfileDataStmt.setInt(3, profile.profileData().geography());
            updateProfileDataStmt.setBoolean(4, profile.profileData().overhead());
            updateProfileDataStmt.setBoolean(5, profile.profileData().archived());
            updateProfileDataStmt.setInt(6, profile.id());
            updateProfileDataStmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new Exception("Could not update Profile in Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Team> getTeams(TransactionContext context, Profile profile) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String query = """
                       SELECT *
                       FROM Teams_profiles
                       INNER JOIN Teams ON Teams_profiles.teamId = Teams.id
                       WHERE Teams_profiles.profileId = ?;
                       """;

        List<Team> teams = new ArrayList<>();

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setInt(1, profile.id());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team(
                            rs.getInt("teamId"),
                            rs.getString("name"),
                            rs.getBigDecimal("markup"),
                            rs.getBigDecimal("gross_margin"),
                            rs.getBoolean("archived")
                    );
                    teams.add(team);
                }
            }
        }

        return teams;
    }

    @Override
    public boolean archive(Profile profile, boolean shouldArchive) throws Exception {
        String query = """
                       UPDATE dbo.Profiles_data SET archived = ?, updated_at = GETDATE() WHERE id = ?;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, shouldArchive);
            stmt.setInt(2, profile.id());

            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new Exception("Could not archive Profile in Database.\n" + e.getMessage());
        }
    }

    @Override
    public boolean archive(List<Profile> profiles) throws Exception {
        String query = """
                       UPDATE dbo.Profiles_data SET archived = ?, updated_at = GETDATE() WHERE id = ?;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            for (Profile profile : profiles) {
                stmt.setBoolean(1, true);
                stmt.setInt(2, profile.id());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            throw new Exception("Could not archive Profiles in Database.\n" + e.getMessage());
        }
    }
}
