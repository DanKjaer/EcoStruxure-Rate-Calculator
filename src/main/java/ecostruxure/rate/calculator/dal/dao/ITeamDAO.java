package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ITeamDAO {
    List<Team> all() throws Exception;
    Team get(int id) throws Exception;
    Team create(Team team) throws Exception;

    Team create(TransactionContext context, Team team) throws Exception;

    boolean update(Team team) throws Exception;

    void updateMultipliers(Team team) throws Exception;

    boolean assignProfiles(Team team, List<Profile> profiles) throws Exception;

    boolean assignProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception;

    boolean updateProfiles(Team team, List<Profile> profiles) throws Exception;

    boolean updateProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception;

    boolean updateProfile(int teamId, Profile profile) throws Exception;

    boolean updateProfile(TransactionContext context, int teamId, Profile profile) throws Exception;

    boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception;

    boolean removeAssignedProfiles(TransactionContext context, Team team, List<Profile> profiles) throws Exception;

    List<Profile> getTeamProfiles(int teamId) throws Exception;

    Profile getTeamProfile(int teamId, int profileId) throws Exception;

    boolean archive(int teamId, boolean archive) throws Exception;

    boolean archive(List<Team> teams) throws Exception;

    List<Profile> canUnarchive(int teamId) throws Exception;

    boolean removeProfileFromTeam(int teamId, int profileId) throws Exception;

    boolean removeProfileFromTeam(TransactionContext context, int teamId, int profileId) throws Exception;

    List<Profile> getTeamProfiles(TransactionContext context, int teamId) throws Exception;

    LocalDateTime getLastUpdated(int teamId) throws Exception;
}
