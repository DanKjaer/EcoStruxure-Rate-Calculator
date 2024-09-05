package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.be.TeamProfile;
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
import java.util.UUID;

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

            TeamMetrics metrics = calculateMetrics(createdTeam.getTeamId(), teamDAO.getTeamProfiles(context, createdTeam.getTeamId()), context);
            historyDAO.insertEmptyTeamProfileHistory(context, createdTeam.getTeamId(), metrics, Reason.TEAM_CREATED);
            return createdTeam;
        });
    }

    public Team createTeam(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            Team createdTeam = teamDAO.create(context, team);
            teamDAO.assignProfiles(context, createdTeam, profiles);
            List<Profile> assignedProfiles = teamDAO.getTeamProfiles(context, createdTeam.getTeamId());
            TeamMetrics metrics = calculateMetrics(createdTeam.getTeamId(), assignedProfiles, context);
            for (Profile profile : assignedProfiles) {
                UUID profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profile.getProfileId());
                ProfileMetrics profileMetrics = calculateMetrics(profile, team, context);

                historyDAO.insertTeamProfileHistory(context, createdTeam.getTeamId(), profile.getProfileId(), profileHistoryId, metrics, Reason.TEAM_CREATED, profileMetrics, now);

            }

            historyDAO.insertEmptyTeamProfileHistory(context, createdTeam.getTeamId(), metrics, Reason.TEAM_CREATED, now);
            return createdTeam;
        });
    }

    public boolean assignProfilesToTeam(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            boolean assigned = teamDAO.assignProfiles(context, team, profiles);

            if (!assigned) return false;
            List<Profile> assignedProfiles = teamDAO.getTeamProfiles(context, team.getTeamId());
            TeamMetrics metrics = calculateMetrics(team.getTeamId(), assignedProfiles, context);
            for (Profile profile : assignedProfiles) {
                UUID profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profile.getProfileId());
                ProfileMetrics profileMetrics = calculateMetrics(profile, team, context);
                historyDAO.insertTeamProfileHistory(context, team.getTeamId(), profile.getProfileId(), profileHistoryId, metrics, Reason.ASSIGNED_PROFILE, profileMetrics, now);
            }
            return true;
        });
    }

    public boolean removeProfilesFromTeam(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            boolean removed = teamDAO.removeAssignedProfiles(context, team, profiles);

            if (!removed) return false;
            List<Profile> assignedProfiles = teamDAO.getTeamProfiles(context, team.getTeamId());
            TeamMetrics metrics = calculateMetrics(team.getTeamId(), assignedProfiles, context);
            for (Profile profile : assignedProfiles) {
                UUID profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profile.getProfileId());
                ProfileMetrics profileMetrics = calculateMetrics(profile, team, context);
                historyDAO.insertTeamProfileHistory(context, team.getTeamId(), profile.getProfileId(), profileHistoryId, metrics, Reason.REMOVED_PROFILE, profileMetrics, now);
            }

            if (assignedProfiles.isEmpty()) {
                historyDAO.insertEmptyTeamProfileHistory(context, team.getTeamId(), metrics, Reason.REMOVED_PROFILE, now);
            }

            return true;
        });
    }

    public boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();

            boolean removed = teamDAO.removeProfileFromTeam(context, teamId, profileId);

            if (!removed) return false;
            Profile removedProfile = profileDAO.get(context, profileId);
            ProfileMetrics removedProfileMetrics = calculateMetrics(removedProfile, teamId, context);
            List<Profile> remainingProfiles = teamDAO.getTeamProfiles(context, teamId);
            TeamMetrics updatedTeamMetrics = calculateMetrics(teamId, remainingProfiles, context);
            UUID profileHistoryId = historyDAO.getLatestProfileHistoryId(context, profileId);
            historyDAO.insertTeamProfileHistory(context, teamId, profileId, profileHistoryId, updatedTeamMetrics, Reason.REMOVED_PROFILE, removedProfileMetrics, now);

            return true;
        });
    }

    public boolean updateTeamProfile(UUID teamId, TeamProfile teamProfile) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();
            UUID profileHistoryId = historyDAO.insertProfileHistory(context, teamProfile.getProfile());

            boolean updated = teamDAO.updateProfile(context, teamId, teamProfile);

            if (!updated) return false;

            List<Profile> teamProfiles = teamDAO.getTeamProfiles(context, teamId);
            TeamMetrics teamMetrics = calculateMetrics(teamId, teamProfiles, context);
            ProfileMetrics profileMetrics = calculateMetrics(teamProfile.getProfile(), teamId, context);

            historyDAO.insertTeamProfileHistory(context, teamId, teamProfile.getProfileId(), profileHistoryId, teamMetrics, Reason.UTILIZATION_CHANGE, profileMetrics, now);
            return true;
        });
    }

    public boolean updateTeamProfiles(Team team, List<Profile> profiles) throws Exception {
        return transactionManager.executeTransaction(context -> {
            LocalDateTime now = LocalDateTime.now();

            boolean updated = teamDAO.updateProfiles(context, team, profiles);
            if (!updated) return false;

            List<Profile> updatedProfiles = teamDAO.getTeamProfiles(context, team.getTeamId());
            TeamMetrics updatedTeamMetrics = calculateMetrics(team.getTeamId(), updatedProfiles, context);

            for (Profile profile : updatedProfiles) {
                UUID profileHistoryId = historyDAO.insertProfileHistory(context, profile);
                ProfileMetrics profileMetrics = calculateMetrics(profile, team.getTeamId(), context);
                historyDAO.insertTeamProfileHistory(context, team.getTeamId(), profile.getProfileId(), profileHistoryId, updatedTeamMetrics, Reason.UTILIZATION_CHANGE, profileMetrics, now);
            }
            return true;
        });
    }

    public boolean updateProfile(Profile toUpdate) throws Exception {
        return transactionManager.executeTransaction(context -> {
            UUID profileHistoryId = historyDAO.insertProfileHistory(context, toUpdate);
            profileDAO.update(context, toUpdate);
            List<Team> teams = profileDAO.getTeams(context, toUpdate);
            for (Team team : teams) {
                TeamMetrics metrics = calculateMetrics(team.getTeamId(), teamDAO.getTeamProfiles(context, team.getTeamId()), context);
                ProfileMetrics profileMetrics = calculateMetrics(toUpdate, team, context);
                historyDAO.insertTeamProfileHistory(context, team.getTeamId(), toUpdate.getProfileId(), profileHistoryId, metrics, Reason.UPDATED_PROFILE, profileMetrics);
            }
            return true;
        });
    }

    private ProfileMetrics calculateMetrics(Profile profile, Team team, TransactionContext context) throws Exception {
        return calculateMetrics(profile, team.getTeamId(), context);
    }

    private ProfileMetrics calculateMetrics(Profile profile, UUID teamid, TransactionContext context) throws Exception {
        BigDecimal costAllocation = profileDAO.getProfileCostAllocationForTeam(context, profile.getProfileId(), teamid);
        BigDecimal hourAllocation = profileDAO.getProfileHourAllocationForTeam(context, profile.getProfileId(), teamid);

        profileDAO.get(context, profile.getProfileId());

        return new ProfileMetrics(
                RateUtils.hourlyRate(profile, costAllocation),
                RateUtils.dayRate(profile, costAllocation),
                RateUtils.annualCost(profile, costAllocation),
                RateUtils.utilizedHours(profile, hourAllocation),
                costAllocation,
                hourAllocation
        );
    }

    private TeamMetrics calculateMetrics(UUID teamId, List<Profile> profiles, TransactionContext context) {
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

    public List<TeamProfile> getTeamProfiles(UUID teamId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team id must be greater than 0");

        return teamDAO._getTeamProfiles(teamId);
    }
}
