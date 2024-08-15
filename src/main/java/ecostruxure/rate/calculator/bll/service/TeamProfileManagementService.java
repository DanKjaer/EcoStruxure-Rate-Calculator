package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.be.data.ProfileMetrics;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.be.data.TeamMetrics;
import ecostruxure.rate.calculator.dal.db.DbTransactionManager;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;
import ecostruxure.rate.calculator.dal.transaction.TransactionManager;
import ecostruxure.rate.calculator.dal.db.HistoryDAO;
import ecostruxure.rate.calculator.dal.db.ProfileDAO;
import ecostruxure.rate.calculator.dal.db.TeamDAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TeamProfileManagementService {
    private final HistoryDAO historyDAO;
    private final ProfileDAO profileDAO;
    private final TeamDAO teamDAO;
    private final TransactionManager transactionManager;

    public TeamProfileManagementService() throws Exception {
        this.historyDAO = new HistoryDAO();
        this.profileDAO = new ProfileDAO();
        this.teamDAO = new TeamDAO();
        this.transactionManager = new DbTransactionManager();
    }

    public Team createTeam(Team team) throws Exception {
        return transactionManager.executeTransaction(context -> {

            Team createdTeam = teamDAO.create(context, team);

            TeamMetrics metrics = calculateMetrics(createdTeam.id(), teamDAO.getTeamProfiles(context, createdTeam.id()), context);
            historyDAO.insertEmptyTeamProfileHistory(context, createdTeam.id(), metrics, Reason.TEAM_CREATED);
            return createdTeam;
        });
    }

    public Team createTeam(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            Team createdTeam = teamDAO.create(context, team);
            teamDAO.assignProfiles(context, createdTeam, profiles);
            List<Profile> assignedProfiles = teamDAO.getTeamProfiles(context, createdTeam.id());
            TeamMetrics metrics = calculateMetrics(createdTeam.id(), assignedProfiles, context);
            for (Profile profile : assignedProfiles) {
                Integer profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profile.id());
                ProfileMetrics profileMetrics = calculateMetrics(profile, team, context);

                historyDAO.insertTeamProfileHistory(context, createdTeam.id(), profile.id(), profileHistoryId, metrics, Reason.TEAM_CREATED, profileMetrics, now);

            }

            historyDAO.insertEmptyTeamProfileHistory(context, createdTeam.id(), metrics, Reason.TEAM_CREATED, now);
            return createdTeam;
        });
    }

    public boolean assignProfilesToTeam(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            boolean assigned = teamDAO.assignProfiles(context, team, profiles);

            if (!assigned) return false;
            List<Profile> assignedProfiles = teamDAO.getTeamProfiles(context, team.id());
            TeamMetrics metrics = calculateMetrics(team.id(), assignedProfiles, context);
            for (Profile profile : assignedProfiles) {
                Integer profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profile.id());
                ProfileMetrics profileMetrics = calculateMetrics(profile, team, context);
                historyDAO.insertTeamProfileHistory(context, team.id(), profile.id(), profileHistoryId, metrics, Reason.ASSIGNED_PROFILE, profileMetrics, now);
            }
            return true;
        });
    }

    public boolean removeProfilesFromTeam(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            boolean removed = teamDAO.removeAssignedProfiles(context, team, profiles);

            if (!removed) return false;
            List<Profile> assignedProfiles = teamDAO.getTeamProfiles(context, team.id());
            TeamMetrics metrics = calculateMetrics(team.id(), assignedProfiles, context);
            for (Profile profile : assignedProfiles) {
                Integer profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profile.id());
                ProfileMetrics profileMetrics = calculateMetrics(profile, team, context);
                historyDAO.insertTeamProfileHistory(context, team.id(), profile.id(), profileHistoryId, metrics, Reason.REMOVED_PROFILE, profileMetrics, now);
            }

            if (assignedProfiles.isEmpty()) {
                historyDAO.insertEmptyTeamProfileHistory(context, team.id(), metrics, Reason.REMOVED_PROFILE, now);
            }

            return true;
        });
    }

    public boolean removeProfileFromTeam(int teamId, int profileId) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();

            boolean removed = teamDAO.removeProfileFromTeam(context, teamId, profileId);

            if (!removed) return false;
            Profile removedProfile = profileDAO.get(context, profileId);
            ProfileMetrics removedProfileMetrics = calculateMetrics(removedProfile, teamId, context);
            List<Profile> remainingProfiles = teamDAO.getTeamProfiles(context, teamId);
            TeamMetrics updatedTeamMetrics = calculateMetrics(teamId, remainingProfiles, context);
            Integer profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profileId);
            historyDAO.insertTeamProfileHistory(context, teamId, profileId, profileHistoryId, updatedTeamMetrics, Reason.REMOVED_PROFILE, removedProfileMetrics, now);

            return true;
        });
    }

    public boolean updateTeamProfile(int teamId, Profile profile) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            int profileHistoryId = historyDAO.insertProfileHistory(context, profile);
            boolean updated = teamDAO.updateProfile(context, teamId, profile);
            if (!updated) return false;

            List<Profile> teamProfiles = teamDAO.getTeamProfiles(context, teamId);
            TeamMetrics teamMetrics = calculateMetrics(teamId, teamProfiles, context);
            ProfileMetrics profileMetrics = calculateMetrics(profile, teamId, context);

            historyDAO.insertTeamProfileHistory(context, teamId, profile.id(), profileHistoryId, teamMetrics, Reason.UTILIZATION_CHANGE, profileMetrics, now);
            return true;
        });
    }

    public boolean updateTeamProfiles(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();

            boolean updated = teamDAO.updateProfiles(context, team, profiles);
            if (!updated) return false;

            List<Profile> updatedProfiles = teamDAO.getTeamProfiles(context, team.id());
            TeamMetrics updatedTeamMetrics = calculateMetrics(team.id(), updatedProfiles, context);

            for (Profile profile : updatedProfiles) {
                Integer profileHistoryId = historyDAO.insertProfileHistory(context, profile);
                ProfileMetrics profileMetrics = calculateMetrics(profile, team.id(), context);
                historyDAO.insertTeamProfileHistory(context, team.id(), profile.id(), profileHistoryId, updatedTeamMetrics, Reason.UTILIZATION_CHANGE, profileMetrics, now);
            }
            return true;
        });
    }

    public boolean updateProfile(Profile toUpdate) throws Exception {
        return transactionManager.executeTransaction(context -> {
            int profileHistoryId = historyDAO.insertProfileHistory(context, toUpdate);
            profileDAO.update(context, toUpdate);
            List<Team> teams = profileDAO.getTeams(context, toUpdate);
            for (Team team : teams) {
                TeamMetrics metrics = calculateMetrics(team.id(), teamDAO.getTeamProfiles(context, team.id()), context);
                ProfileMetrics profileMetrics = calculateMetrics(toUpdate, team, context);
                historyDAO.insertTeamProfileHistory(context, team.id(), toUpdate.id(), profileHistoryId, metrics, Reason.UPDATED_PROFILE, profileMetrics);
            }
            return true;
        });
    }

    private ProfileMetrics calculateMetrics(Profile profile, Team team, TransactionContext context) throws Exception {
        return calculateMetrics(profile, team.id(), context);
    }

    private ProfileMetrics calculateMetrics(Profile profile, int teamid, TransactionContext context) throws Exception {
        BigDecimal costAllocation = profileDAO.getProfileCostAllocationForTeam(context, profile.id(), teamid);
        BigDecimal hourAllocation = profileDAO.getProfileHourAllocationForTeam(context, profile.id(), teamid);

        profileDAO.get(context, profile.id());

        return new ProfileMetrics(
                RateUtils.hourlyRate(profile, costAllocation),
                RateUtils.dayRate(profile, costAllocation),
                RateUtils.annualCost(profile, costAllocation),
                RateUtils.utilizedHours(profile, hourAllocation),
                costAllocation,
                hourAllocation
        );
    }

    private TeamMetrics calculateMetrics(int teamId, List<Profile> profiles, TransactionContext context) {
        BigDecimal annualCost = RateUtils.teamAnnualCost(profiles);
        BigDecimal totalHours = RateUtils.teamHourlyRate(profiles);

        if (totalHours.compareTo(BigDecimal.ZERO) == 0) {
            return new TeamMetrics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        } else {
            BigDecimal hourlyRate = annualCost.divide(totalHours, 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal dayRate = RateUtils.teamDayRate(profiles);

            return new TeamMetrics(hourlyRate, dayRate, annualCost, totalHours);
        }
    }
}
