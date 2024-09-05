package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITeamDAO {
    List<Team> all() throws Exception;
    Team get(UUID id) throws Exception;
    Team create(Team team) throws Exception;

    Team create(TransactionContext context, Team team) throws Exception;

    boolean update(Team team) throws Exception;

    void updateMultipliers(Team team) throws Exception;

    boolean assignProfiles(Team team, List<Profile> profiles) throws Exception;

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

    boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws Exception;

    boolean removeProfileFromTeam(TransactionContext context, UUID teamId, UUID profileId) throws Exception;

    List<Profile> getTeamProfiles(TransactionContext context, UUID teamId) throws Exception;

    LocalDateTime getLastUpdated(UUID teamId) throws Exception;
}
