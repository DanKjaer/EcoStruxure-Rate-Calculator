package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;
import ecostruxure.rate.calculator.dal.dao.IProfileDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ProfileDAO implements IProfileDAO {
    private final DBConnector dbConnector;
    private static final Logger logger = Logger.getLogger(ProfileDAO.class.getName());

    public ProfileDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }

    private Profile profileResultSet(ResultSet rs) throws SQLException {
        Geography geography = new Geography();
        UUID profileId = (UUID) (rs.getObject("profile_id"));
        String profileName = rs.getString("name");
        String currency = rs.getString("currency");
        geography.setId(rs.getInt("geography_id"));
        // Check if the geography_name column exists in the ResultSet
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int columnCount = rsMetaData.getColumnCount();
        boolean hasGeographyName = false;
        for (int i = 1; i <= columnCount; i++) {
            if ("geography_name".equals(rsMetaData.getColumnName(i))) {
                hasGeographyName = true;
                break;
            }
        }
        if (hasGeographyName) {
            geography.setName(rs.getString("geography_name"));
        } else {
            geography.setName(null); // or set a default value
        }
        boolean resourceType = rs.getBoolean("resource_type");

        BigDecimal annualCost = rs.getBigDecimal("annual_cost");
        BigDecimal annualHours = rs.getBigDecimal("annual_hours");
        BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");
        BigDecimal effectivenessPercentage = rs.getBigDecimal("effectiveness");
        BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
        BigDecimal totalCostAllocation = rs.getBigDecimal("total_cost_allocation");
        BigDecimal totalHoursAllocation = rs.getBigDecimal("total_hour_allocation");
        //boolean archived = rs.getBoolean("is_archived");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new Profile.Builder()
                .setProfileId(profileId)
                .setName(profileName)
                .setCurrency(currency)
                .setGeography(geography)
                .setResourceType(resourceType)
                .setAnnualCost(annualCost)
                .setAnnualHours(annualHours)
                .setHoursPerDay(hoursPerDay)
                .setEffectivenessPercentage(effectivenessPercentage)
                .setEffectiveWorkHours(effectiveWorkHours)
                .setTotalCostAllocation(totalCostAllocation)
                .setTotalHoursAllocation(totalHoursAllocation)
                //.setArchived(archived)
                .setUpdatedAt(updatedAt)
                .build();
    }

    // Bemærk: Kan nok laves smartere, men indtil videre er dette funktionelt og opdelt.
    @Override
    public Profile create(Profile profile) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                Profile createdProfile = createProfile(conn, profile);
                conn.commit();
                return createdProfile;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                throw new Exception("Error occured, rolling back..\n" + ex.getMessage());
            }
        }

    }

    @Override
    public List<Profile> all() throws Exception {
        List<Profile> profiles = new ArrayList<>();

        String query = """

                       SELECT p.*, g.name AS geography_name 
                       FROM dbo.Profiles p
                       INNER JOIN dbo.Geography g ON p.geography_id = g.id
                       ORDER BY p.profile_id DESC
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                profiles.add(profileResultSet(rs));
            }

            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get all Profiles from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Profile> allWithUtilization() throws Exception {
        List<Profile> profiles = new ArrayList<>();

        String query = """
                        SELECT p.*,
                           COALESCE(tp.cost_allocation_total, 0) AS cost_allocation_total,
                           COALESCE(tp.hour_allocation_total, 0) AS hour_allocation_total
                        FROM dbo.Profiles p
                        LEFT JOIN LATERAL (
                            SELECT profileId,
                                   SUM(cost_allocation) AS cost_allocation_total,
                                   SUM(hour_allocation) AS hour_allocation_total
                            FROM dbo.Teams_profiles tp
                            WHERE p.is_archived = FALSE
                            GROUP BY profileId
                        ) tp ON p.profile_id = tp.profileId
                        WHERE p.is_archived = FALSE
                        ORDER BY p.profile_id DESC;                     
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Profile profile = profileResultSet(rs);

                TeamProfile teamProfile = new TeamProfile();
                teamProfile.setProfile(profile);
                teamProfile.setCostAllocation(rs.getBigDecimal("cost_allocation_total"));
                teamProfile.setHourAllocation(rs.getBigDecimal("hour_allocation_total"));

                profiles.add(profile);
            }

            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get all Profiles from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Profile> allWithUtilizationByTeam(UUID teamId) throws Exception {
        List<Profile> profiles = new ArrayList<>();

        String query = """
                        SELECT p.*, pd.*,
                               COALESCE(tp.cost_allocation_total, 0) AS cost_allocation_total,
                               COALESCE(tp.hour_allocation_total, 0) AS hour_allocation_total
                        FROM dbo.Profiles p
                        LEFT JOIN (
                            SELECT profileID,
                                   SUM(cost_allocation) AS cost_allocation_total,
                                   SUM(hour_allocation) AS uhour_allocation_total
                            FROM dbo.Teams_profiles
                            WHERE teamId = ?
                            GROUP BY profileID
                        ) tp ON p.profile_id = tp.profileID
                        LEFT JOIN dbo.Teams_profiles tp2 ON p.profile_id = tp2.profileId
                        WHERE tp2.teamId = ?
                        ORDER BY p.profile_id DESC;
                    """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Profile profile = profileResultSet(rs);

                    TeamProfile teamProfile = new TeamProfile();
                    teamProfile.setCostAllocation(rs.getBigDecimal("cost_allocation_total"));
                    teamProfile.setHourAllocation(rs.getBigDecimal("hour_allocation_total"));

                    profile.setTeamProfile(teamProfile);
                    profiles.add(profile);
                }
            }

            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get all Profiles from Database.\n" + e.getMessage());
        }
    }

    @Override
    public Profile get(UUID profileId) throws Exception {
        Profile profile = null;
        String query = """
                       SELECT * FROM dbo.Profiles

                       WHERE dbo.Profiles.profile_id = ?
                       """;
        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile = profileResultSet(rs);
                    profile.setUpdatedAt(rs.getTimestamp("updated_at"));
                    profile.setArchived(rs.getBoolean("is_archived"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get Profile from Database. Profile ID: " + profileId + "\n" + e.getMessage());
        }
        if (profile == null) throw new Exception("Profile with ID " + profileId + " not found.");

        return profile;
    }

    @Override
    public Profile get(TransactionContext context, UUID id) throws Exception {
        System.out.println("Entering get profileDAO");
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        if (id == null){
            throw new Exception("Profile id is null.");
        }
        Profile profile = null;

        String query = """
                       SELECT * FROM dbo.Profiles
                       WHERE dbo.Profiles.profile_id = CAST(? AS UUID)
                       """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile = profileResultSet(rs);
                    logger.info("ProfileDAO fetched profile: " +profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get Profile from Database. Profile ID: " + id + "\n" + e.getMessage());
        }

        if (profile == null) throw new Exception("Profile with ID " + id + " not found.");
        System.out.println("Exiting get profileDAO");
        return profile;
    }

    private Profile createProfile(Connection connection, Profile profile) throws Exception {
        String sql = """
                     INSERT INTO dbo.Profiles (profile_id, name, currency, geography_id, resource_type, annual_cost, 
                     effectiveness, annual_hours, effective_work_hours, hours_per_day, is_archived) 
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            UUID profileId = UUID.randomUUID();
            stmt.setObject(1, profileId);
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getCurrency());
            stmt.setInt(4, profile.getGeography().getId());
            stmt.setBoolean(5, profile.isResourceType());
            stmt.setBigDecimal(6, profile.getAnnualCost());
            stmt.setBigDecimal(7, profile.getEffectivenessPercentage());
            stmt.setBigDecimal(8, profile.getAnnualHours());
            stmt.setBigDecimal(9, profile.getEffectiveWorkHours());
            stmt.setBigDecimal(10, profile.getHoursPerDay());
            stmt.setBoolean(11, profile.isArchived());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
                throw new SQLException("Creating profile failed, no rows affected.");

            // Få ID fra oprettet profil
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    profile.setProfileId((UUID) generatedKeys.getObject(1));
                    return profile;
                } else {
                    throw new SQLException("Creating profile failed, no ID obtained.");
                }
            }
        }
    }

    public List<Profile> allByCountry(Country country) throws Exception {
        return allByCountry(country.code());
    }

    /**
     * Profiles data har ikke været en ting i LANG tid, skal måske slettes?
     */
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
            e.printStackTrace();
            throw new Exception("Getting profileByCountry failed\n." + e.getMessage());
        }

        return profilesBycountry;
    }

    @Override
    public List<Profile> allByTeam(Team team) throws Exception {
        return allByTeam(team.getTeamId());
    }

    /**
     * Profiles data har ikke været en ting i LANG tid, skal måske slettes?
     */
    @Override
    public List<Profile> allByTeam(UUID teamId) throws Exception {
        ArrayList<Profile> profilesByTeam = new ArrayList<>();
        String query = """
                        SELECT * FROM dbo.Profiles
                        INNER JOIN dbo.Profiles_data ON dbo.Profiles.id = dbo.Profiles_data.id
                        INNER JOIN dbo.Teams_profiles ON dbo.Profiles.id = dbo.Teams_profiles.profileId
                        WHERE dbo.Teams_profiles.teamId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profilesByTeam.add(profileResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting profileByTeam failed\n." + e.getMessage());
        }

        return profilesByTeam;
    }

    @Override
    public List<Profile> allByGeography(Geography geography) throws Exception {
        return allByGeography(geography.id());
    }

    /**
     * Profiles data har ikke været en ting i LANG tid, skal måske slettes?
     */
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
            e.printStackTrace();
            throw new Exception("Getting profileByGeography failed\n." + e.getMessage());
        }

        return profilesByGeography;
    }

    @Override
    public BigDecimal getTotalCostAllocation(UUID profileId) throws Exception {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        String query = """
                        SELECT SUM(cost_allocation) AS total_allocation
                        FROM Teams_profiles
                        WHERE profileId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_allocation");
                    if (column != null)
                        totalUtilization = column;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting total utilization rate for profile failed\n." + e.getMessage());
        }

        return totalUtilization;
    }

    @Override
    public BigDecimal getTotalHourAllocation(UUID profileId) throws Exception {
        BigDecimal totalAllocation = BigDecimal.ZERO;
        String query = """
                        SELECT SUM(hour_allocation) AS total_allocation
                        FROM Teams_profiles
                        WHERE profileId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_allocation");
                    if (column != null)
                        totalAllocation = column;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting total hour allocation for profile failed\n." + e.getMessage());
        }

        return totalAllocation;
    }

    @Override
    public BigDecimal getProfileCostAllocationForTeam(UUID profileId, UUID teamId) throws Exception {
        BigDecimal allocation = BigDecimal.ZERO;
        String query = """
                        SELECT cost_allocation AS total_allocation
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, profileId);
            stmt.setObject(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_allocation");
                    if (column != null)
                        allocation = rs.getBigDecimal("total_allocation");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting profile allocation for team failed\n." + e.getMessage());
        }

        return allocation;
    }

    @Override
    public BigDecimal getProfileHourAllocationForTeam(UUID profileId, UUID teamId) throws Exception {
        BigDecimal allocation = BigDecimal.ZERO;
        String query = """
                        SELECT hour_allocation AS total_allocation
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, profileId);
            stmt.setObject(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_allocation");
                    if (column != null)
                        allocation = rs.getBigDecimal("total_allocation");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting profile allocation for team failed\n." + e.getMessage());
        }

        return allocation;
    }

    @Override
    public BigDecimal getProfileCostAllocationForTeam(TransactionContext context, UUID profileId, UUID teamId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        BigDecimal allocation = BigDecimal.ZERO;
        String query = """
                        SELECT cost_allocation AS total_allocation
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setObject(1, profileId);
            stmt.setObject(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_allocation");
                    if (column != null)
                        allocation = rs.getBigDecimal("total_allocation");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting profile allocation for team failed\n." + e.getMessage());
        }

        return allocation;
    }

    @Override
    public BigDecimal getProfileHourAllocationForTeam(TransactionContext context, UUID profileId, UUID teamId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        BigDecimal allocation = BigDecimal.ZERO;
        String query = """
                        SELECT hour_allocation AS total_allocation
                        FROM Teams_profiles
                        WHERE profileId = ? AND teamId = ?;
                        """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(query)) {
            stmt.setObject(1, profileId);
            stmt.setObject(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal column = rs.getBigDecimal("total_allocation");
                    if (column != null)
                        allocation = rs.getBigDecimal("total_allocation");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting profile allocation for team failed\n." + e.getMessage());
        }

        return allocation;
    }

    @Override
    public List<Team> getTeams(Profile profile) throws Exception {
        List<Team> teams = new ArrayList<>();

        String query = """
                       SELECT *
                       FROM Teams_profiles
                       INNER JOIN Teams ON Teams_profiles.teamId = Teams.id
                       WHERE Teams_profiles.profileId = ? AND Teams.is_archived = FALSE;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, profile.getProfileId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team.Builder()
                            .teamId((UUID) rs.getObject("teamId"))
                            .name(rs.getString("name"))
                            .markup(rs.getBigDecimal("markup"))
                            .grossMargin(rs.getBigDecimal("gross_margin"))
                            .archived(rs.getBoolean("is_archived"))
                            .build();
                    teams.add(team);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Getting teams for profile failed\n." + e.getMessage());
        }

        return teams;
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
            stmt.setObject(1, profile.getProfileId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team.Builder()
                            .teamId((UUID) rs.getObject("teamId"))
                            .name(rs.getString("name"))
                            .markup(rs.getBigDecimal("markup"))
                            .grossMargin(rs.getBigDecimal("gross_margin"))
                            .archived(rs.getBoolean("is_archived"))
                            .build();
                    teams.add(team);
                }
            }
        }

        return teams;
    }

    @Override
    public boolean update(UUID profileId, Profile profile) throws Exception {
        String updateProfileSQL = """
                                  UPDATE dbo.Profiles
                                  SET annual_cost = ?, effectiveness = ?, annual_hours = ?, effective_work_hours = ?, hours_per_day = ?,
                                  name = ?, currency = ?, geography_id
    = ?, resource_type = ?, is_archived = ?, updated_at = CURRENT_TIMESTAMP
                                  WHERE profile_id = ?;
                                  """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement updateProfileStmt = conn.prepareStatement(updateProfileSQL)) {
            conn.setAutoCommit(false);

            updateProfileStmt.setBigDecimal(1, profile.getAnnualCost());
            updateProfileStmt.setBigDecimal(2, profile.getEffectivenessPercentage());
            updateProfileStmt.setBigDecimal(3, profile.getAnnualHours());
            updateProfileStmt.setBigDecimal(4, profile.getEffectiveWorkHours());
            updateProfileStmt.setBigDecimal(5, profile.getHoursPerDay());
            updateProfileStmt.setString(6, profile.getName());
            updateProfileStmt.setString(7, profile.getCurrency());
            updateProfileStmt.setInt(8, profile.getGeography().getId());
            updateProfileStmt.setBoolean(9, profile.isResourceType());
            updateProfileStmt.setBoolean(10, profile.isArchived());
            updateProfileStmt.setObject(11, profileId);
            updateProfileStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            try (Connection conn = dbConnector.connection()) {
                conn.rollback();
                return false;
            } catch (Exception ex) {
                e.printStackTrace();
                throw new Exception("Could not update Profile in Database.\n" + ex.getMessage());
            }
        }
    }

    @Override
    public boolean update(TransactionContext context, Profile profile) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;

        String updateProfileSQL = """
                                  
UPDATE dbo.Profiles
                                  SET annual_cost = ?, effectiveness = ?, annual_hours = ?, effective_work_hours = ?, hours_per_day = ?,
                                  name = ?, currency = ?, geography_id
                                      = ?, resource_type = ?, is_archived = ?, updated_at = CURRENT_TIMESTAMP
                                  WHERE profile_id = ?;
                                  """;
        try (PreparedStatement updateProfileStmt = sqlContext.connection().prepareStatement(updateProfileSQL)) {
            updateProfileStmt.setBigDecimal(1, profile.getAnnualCost());
            updateProfileStmt.setBigDecimal(2, profile.getEffectivenessPercentage());
            updateProfileStmt.setBigDecimal(3, profile.getAnnualHours());
            updateProfileStmt.setBigDecimal(4, profile.getEffectiveWorkHours());
            updateProfileStmt.setBigDecimal(5, profile.getHoursPerDay());
            updateProfileStmt.setString(6, profile.getName());
            updateProfileStmt.setString(7, profile.getCurrency());
            updateProfileStmt.setInt(8, profile.getGeography().getId());
            updateProfileStmt.setBoolean(9, profile.isResourceType());
            updateProfileStmt.setBoolean(10, profile.isArchived());
            updateProfileStmt.setObject(11, profile.getProfileId());
            updateProfileStmt.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not update Profile in Database.\n" + e.getMessage());
        }
    }

    @Override
    public boolean archive(UUID profileId, boolean shouldArchive) throws Exception {
        String query = """
                       UPDATE dbo.Profiles SET is_archived = ?, updated_at = CURRENT_TIMESTAMP WHERE profile_id = ?;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, shouldArchive);
            stmt.setObject(2, profileId);

            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not archive Profile in Database.\n" + e.getMessage());
        }
    }

    // Updatere flere profiler på en gang, er det nødvendigt?
    @Override
    public boolean archive(List<Profile> profiles) throws Exception {
        String query = """
                       UPDATE dbo.Profiles SET is_archived = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            for (Profile profile : profiles) {
                stmt.setBoolean(1, true);
                stmt.setObject(2, profile.getProfileId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not archive Profiles in Database.\n" + e.getMessage());
        }
    }

    @Override
    public void updateAllocation(UUID profileId, BigDecimal costAllocation, BigDecimal hourAllocation) throws SQLException {
        String query = """
                       UPDATE dbo.Teams_profiles
                       SET cost_allocation = ?, hour_allocation = ?
                       WHERE profile_id = ?;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, costAllocation);
            stmt.setBigDecimal(2, hourAllocation);
            stmt.setObject(3, profileId);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Could not update Profile allocation in Database.\n" + e.getMessage());
        }
    }
}
