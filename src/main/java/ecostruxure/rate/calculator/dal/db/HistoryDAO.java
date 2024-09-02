package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.ProfileHistory;
import ecostruxure.rate.calculator.be.TeamHistory;
import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.be.TeamProfileHistory;
import ecostruxure.rate.calculator.be.data.ProfileMetrics;
import ecostruxure.rate.calculator.be.data.TeamMetrics;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;
import ecostruxure.rate.calculator.dal.dao.IHistoryDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class HistoryDAO implements IHistoryDAO {
    private final DBConnector dbConnector;
    private static final Logger logger = Logger.getLogger(HistoryDAO.class.getName());

    public HistoryDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }

    @Override
    public List<ProfileHistory> getProfileHistory(UUID profileId) throws Exception {
        List<ProfileHistory> profiles = new ArrayList<>();

        String query = """
                       SELECT * FROM dbo.Profiles_history
                       WHERE profile_id = ?
                       ORDER BY updated_at DESC
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProfileHistory profile = new ProfileHistory();
                    profile.profileId((UUID) rs.getObject("profile_id"));
                    profile.resourceType(rs.getBoolean("resource_type"));
                    profile.annualCost(rs.getBigDecimal("annual_cost"));
                    profile.effectiveness(rs.getBigDecimal("effectiveness"));
                    profile.annualHours(rs.getBigDecimal("annual_hours"));
                    profile.hoursPerDay(rs.getBigDecimal("hours_per_day"));
                    profile.updatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    profiles.add(profile);
                }
            }

            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get Profile History from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<TeamHistory> getTeamHistory(UUID teamId) throws Exception {
        Map<LocalDateTime, TeamHistory> historyMap = new LinkedHashMap<>();

        String query = """
                        SELECT tph.team_id, tph.profile_id, tph.profile_history_id, tph.reason,
                               tph.hourly_rate, tph.day_rate, tph.annual_cost, tph.annual_hours, tph.cost_allocation, tph.hour_allocation,
                               tph.profile_hourly_rate, tph.profile_day_rate, tph.profile_annual_cost, tph.profile_annual_hours, tph.updated_at
                        FROM dbo.Teams_profiles_history tph
                        WHERE tph.team_id = ?
                        ORDER BY tph.updated_at DESC;
                        """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                    TeamHistory teamHistory = historyMap.get(updatedAt);

                    if (teamHistory == null) {
                        teamHistory = new TeamHistory();
                        teamHistory.teamId((UUID) rs.getObject("team_id"));
                        teamHistory.reason(TeamHistory.Reason.valueOf(rs.getString("reason")));
                        teamHistory.hourlyRate(rs.getBigDecimal("hourly_rate"));
                        teamHistory.dayRate(rs.getBigDecimal("day_rate"));
                        teamHistory.annualCost(rs.getBigDecimal("annual_cost"));
                        teamHistory.annualHours(rs.getBigDecimal("annual_hours"));
                        teamHistory.updatedAt(updatedAt);
                        historyMap.put(updatedAt, teamHistory);
                    }

                    TeamProfileHistory teamProfileHistory = new TeamProfileHistory();
                    teamProfileHistory.profileId((UUID) rs.getObject("profile_id"));
                    teamProfileHistory.profileHistoryId((UUID) rs.getObject("profile_history_id"));
                    teamProfileHistory.hourlyRate(rs.getBigDecimal("profile_hourly_rate"));
                    teamProfileHistory.dayRate(rs.getBigDecimal("profile_day_rate"));
                    teamProfileHistory.annualCost(rs.getBigDecimal("profile_annual_cost"));
                    teamProfileHistory.annualHours(rs.getBigDecimal("profile_annual_hours"));
                    teamProfileHistory.costAllocation(rs.getBigDecimal("cost_allocation"));
                    teamProfileHistory.hourAllocation(rs.getBigDecimal("hour_allocation"));
                    teamProfileHistory.updatedAt(updatedAt);

                    teamHistory.teamProfileHistories().add(teamProfileHistory);
                }
            }

            return new ArrayList<>(historyMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not retrieve team history from the database.\n" + e.getMessage(), e);
        }
    }

    @Override
    public UUID insertProfileHistory(TransactionContext context, Profile profile) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String insertProfileHistorySQL = """
                                         INSERT INTO dbo.Profiles_history (
                                             profile_id, resource_type, annual_cost, effectiveness,
                                             annual_hours, hours_per_day, updated_at
                                         )
                                         SELECT p.profile_id, p.resource_type, p.annual_cost, p.effectiveness,
                                                p.annual_hours, p.hours_per_day, CURRENT_TIMESTAMP
                                         FROM dbo.Profiles p
                                         WHERE p.profile_id = ?
                                         """;
        logger.info("InsertProfileHistory HistoryDAO::Inserting profile history for profileId: " + profile.getProfileId());

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(insertProfileHistorySQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, profile.getProfileId());
            stmt.executeUpdate();

            UUID profileHistoryId = null;
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next())  profileHistoryId = (UUID) generatedKeys.getObject(1);
            }
            return profileHistoryId;
        } catch(SQLException e) {
            e.printStackTrace();
        } throw new Exception("Could not insert profile history into the database");
    }

    @Override
    public void insertEmptyTeamProfileHistory(TransactionContext context, UUID teamId, TeamMetrics metrics, Reason reason) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String sql = """
                    INSERT INTO dbo.Teams_profiles_history (team_id, profile_id, profile_history_id, reason, hourly_rate, day_rate, annual_cost, annual_hours, updated_at)
                    VALUES (?, NULL, NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);
                    """;

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(sql)) {
            stmt.setObject(1, teamId);
            stmt.setString(2, reason.name());
            stmt.setBigDecimal(3, metrics.hourlyRate());
            stmt.setBigDecimal(4, metrics.dayRate());
            stmt.setBigDecimal(5, metrics.annualCost());
            stmt.setBigDecimal(6, metrics.annualHours());
            stmt.executeUpdate();
        }
    }

    @Override
    public void insertEmptyTeamProfileHistory(TransactionContext context, UUID teamId, TeamMetrics metrics, Reason reason, LocalDateTime now) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String sql = """
                    INSERT INTO dbo.Teams_profiles_history (team_id, profile_id, profile_history_id, reason, hourly_rate, day_rate, annual_cost, annual_hours, updated_at)
                    VALUES (?, NULL, NULL, ?, ?, ?, ?, ?, ?);
                    """;

        logger.info("Inserting empty team profile history for teamId: " + teamId + " with reason: " + reason);

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(sql)) {
            stmt.setObject(1, teamId);
            stmt.setString(2, reason.name());
            stmt.setBigDecimal(3, metrics.hourlyRate());
            stmt.setBigDecimal(4, metrics.dayRate());
            stmt.setBigDecimal(5, metrics.annualCost());
            stmt.setBigDecimal(6, metrics.annualHours());
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.executeUpdate();
        }
    }

    @Override
    public void insertTeamProfileHistory(TransactionContext context, UUID teamId, UUID profileId, UUID profileHistoryId, TeamMetrics teamMetrics, Reason reason, ProfileMetrics profileMetrics) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String sql = """
                    INSERT INTO dbo.Teams_profiles_history (team_id, profile_id, profile_history_id, reason, hourly_rate, day_rate, annual_cost, annual_hours, cost_allocation, hour_allocation, profile_hourly_rate, profile_day_rate, profile_annual_cost, profile_annual_hours, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);
                    """;

        logger.info("Inserting team profile history with profileid: " + profileId);

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(sql)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, profileId);

            if (profileHistoryId == null) stmt.setNull(3, Types.OTHER);
            else stmt.setObject(3, profileHistoryId, Types.OTHER);

            stmt.setString(4, reason.name());
            stmt.setBigDecimal(5, teamMetrics.hourlyRate());
            stmt.setBigDecimal(6, teamMetrics.dayRate());
            stmt.setBigDecimal(7, teamMetrics.annualCost());
            stmt.setBigDecimal(8, teamMetrics.annualHours());
            stmt.setBigDecimal(9, profileMetrics.costAllocation());
            stmt.setBigDecimal(10, profileMetrics.hourAllocation());
            stmt.setBigDecimal(11, profileMetrics.hourlyRate());
            stmt.setBigDecimal(12, profileMetrics.dayRate());
            stmt.setBigDecimal(13, profileMetrics.annualCost());
            stmt.setBigDecimal(14, profileMetrics.annualHours());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not insert team profile history into the database.\n" + e.getMessage(), e);
        }
    }

    @Override
    public void insertTeamProfileHistory(TransactionContext context, UUID teamId, UUID profileId, UUID profileHistoryId, TeamMetrics teamMetrics, Reason reason, ProfileMetrics profileMetrics, LocalDateTime now) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String sql = """
                    INSERT INTO dbo.Teams_profiles_history (team_id, profile_id, profile_history_id, reason, hourly_rate, day_rate, annual_cost, annual_hours, cost_allocation, hour_Allocation, profile_hourly_rate, profile_day_rate, profile_annual_cost, profile_annual_hours, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                    """;
        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(sql)) {
            stmt.setObject(1, teamId);
            stmt.setObject(2, profileId);

            if (profileHistoryId == null) stmt.setNull(3, Types.OTHER);
            else stmt.setObject(3, profileHistoryId, Types.OTHER);

            stmt.setString(4, reason.name());
            stmt.setBigDecimal(5, teamMetrics.hourlyRate());
            stmt.setBigDecimal(6, teamMetrics.dayRate());
            stmt.setBigDecimal(7, teamMetrics.annualCost());
            stmt.setBigDecimal(8, teamMetrics.annualHours());
            stmt.setBigDecimal(9, profileMetrics.costAllocation());
            stmt.setBigDecimal(10, profileMetrics.hourAllocation());
            stmt.setBigDecimal(11, profileMetrics.hourlyRate());
            stmt.setBigDecimal(12, profileMetrics.dayRate());
            stmt.setBigDecimal(13, profileMetrics.annualCost());
            stmt.setBigDecimal(14, profileMetrics.annualHours());
            stmt.setTimestamp(15, Timestamp.valueOf(now));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not insert team profile history into the database.\n" + e.getMessage(), e);
        }
    }

    @Override
    public UUID getLatestProfileHistoryId(TransactionContext context, UUID profileId) throws Exception {
        SqlTransactionContext sqlContext = (SqlTransactionContext) context;
        String sql = "SELECT history_id FROM dbo.Profiles_history WHERE profile_id = ? ORDER BY updated_at DESC LIMIT 1";

        try (PreparedStatement stmt = sqlContext.connection().prepareStatement(sql)) {
            stmt.setObject(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID historyId = (UUID) rs.getObject("history_id");
                return historyId;
            } else {
                return null;
            }
        } catch (SQLException e){
            logger.severe("SQL Exception: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Could not retrieve latest profile history id from the database");
        }
    }
}
