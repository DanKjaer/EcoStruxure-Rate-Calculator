package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.ProfileHistory;
import ecostruxure.rate.calculator.be.TeamHistory;
import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.be.data.ProfileMetrics;
import ecostruxure.rate.calculator.be.data.TeamMetrics;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IHistoryDAO {
    List<ProfileHistory> getProfileHistory(UUID profileId) throws Exception;

    List<TeamHistory> getTeamHistory(int teamId) throws Exception;

    int insertProfileHistory(TransactionContext context, Profile profile) throws Exception;

    void insertEmptyTeamProfileHistory(TransactionContext context, int teamId, TeamMetrics metrics, Reason reason) throws Exception;

    void insertEmptyTeamProfileHistory(TransactionContext context, int teamId, TeamMetrics metrics, Reason reason, LocalDateTime now) throws Exception;

    void insertTeamProfileHistory(TransactionContext context, int teamId, UUID profileId, Integer profileHistoryId, TeamMetrics teamMetrics, Reason reason, ProfileMetrics profileMetrics) throws Exception;

    void insertTeamProfileHistory(TransactionContext context, int teamId, UUID profileId, Integer profileHistoryId, TeamMetrics teamMetrics, Reason reason, ProfileMetrics profileMetrics, LocalDateTime now) throws Exception;

    Integer getLatestProfileHistoryId(TransactionContext context, UUID profileId) throws Exception;
}
