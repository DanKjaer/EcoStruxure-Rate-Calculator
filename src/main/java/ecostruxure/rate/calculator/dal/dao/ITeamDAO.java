package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITeamDAO {
    List<Team> all() throws Exception;

    Team get(UUID id) throws Exception;

    List<TeamProfile> getByProfileId(UUID id) throws Exception;

    Team create(Team team) throws Exception;

    Team create(TransactionContext context, Team team) throws Exception;

    Team update(UUID teamId, Team team) throws Exception;

    void updateMultipliers(Team team) throws Exception;

    boolean assignProfiles(Team team, List<TeamProfile> teamProfiles) throws Exception;

    boolean assignProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception;

    boolean updateProfiles(Team team, List<Profile> profiles) throws Exception;

    boolean updateProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception;

    boolean updateProfile(UUID teamId, Profile profile) throws Exception;

    boolean updateProfile(TransactionContext context, UUID teamId, TeamProfile teamProfile) throws Exception;

    boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception;

    boolean removeAssignedProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception;

    List<TeamProfile> _getTeamProfiles(UUID teamId) throws Exception;

    List<Profile> getTeamProfiles(UUID teamId) throws Exception;

    Profile getTeamProfile(UUID teamId, UUID profileId) throws Exception;

    boolean archive(UUID teamId, boolean archive) throws Exception;

    boolean archive(List<Team> teams) throws Exception;

    List<Profile> canUnarchive(UUID teamId) throws Exception;

    boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws SQLException;

    boolean removeProfileFromTeam(TransactionContext context, UUID teamId, UUID profileId) throws Exception;

    List<Profile> getTeamProfiles(TransactionContext context, UUID teamId) throws Exception;

    LocalDateTime getLastUpdated(UUID teamId) throws Exception;

    BigDecimal getCostAllocation(UUID teamId, UUID profileId) throws Exception;

    BigDecimal getHourAllocation(UUID teamId, UUID profileId) throws Exception;

    BigDecimal getAllocatedCostOnTeam(UUID teamId, UUID profileId) throws Exception;

    BigDecimal getAllocatedHoursOnTeam(UUID teamId, UUID profileId) throws Exception;

    void updateAllocatedCostAndHour(UUID teamId, List<TeamProfile> teamProfiles) throws SQLException;

    void updateDayRateOnTeam(UUID teamid, UUID profileId, BigDecimal dayRate) throws SQLException;

    void updateTeamsDayRate(UUID teamId, BigDecimal dayRate) throws SQLException;

    boolean storeTeamProfiles(TransactionContext context, List<TeamProfile> teamProfiles) throws Exception;

    List<TeamProfile> saveTeamProfiles(UUID teamId, List<TeamProfile> teamProfiles) throws SQLException;

    void updateTeamRateCostHours(Team team) throws SQLException;

    void assignProfilesToTeam(UUID teamId, List<TeamProfile> profiles) throws SQLException;

    void updateDayRateOnTeam(List<TeamProfile> teamProfiles) throws SQLException;

    boolean removeProfilesFromTeam(UUID teamId, List<UUID> profileIds) throws SQLException;

    TeamProfile updateTeamProfile(UUID teamId, TeamProfile teamProfile) throws SQLException;

    void updateTotalAllocationOfProfiles(List<TeamProfile> teamProfiles) throws SQLException;

    void updateTotalAllocationOfProfilesOnDelete(List<Profile> profiles) throws SQLException;

}
