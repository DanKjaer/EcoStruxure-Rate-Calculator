package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.TeamProfileManagementService;
import ecostruxure.rate.calculator.dal.dao.ITeamDAO;
import ecostruxure.rate.calculator.dal.db.TeamDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TeamService {
    private final ITeamDAO teamDAO;
    private final TeamProfileManagementService teamProfileManagementService;

    public TeamService() throws Exception {
        this.teamDAO = new TeamDAO();
        this.teamProfileManagementService = new TeamProfileManagementService();
    }

    public List<Team> all() throws Exception {
        return teamDAO.all();
    }

    public Team get(int id) throws Exception {
        return teamDAO.get(id);
    }

    public Team get(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return this.get(team.id());
    }

    public boolean update(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return teamDAO.update(team);
    }

    /**
     * Create a new team.
     *
     * @param team the team to create. Must not be null.
     * @return the created team that has same data as the input team, but with ID set.
     * @throws Exception if an error occurred in creating the team.
     * @throws NullPointerException if the input team or team name is null.
     * @throws IllegalArgumentException if the team name is empty.
     */
    public Team create(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(team.name(), "Team name cannot be null");
        if (team.name().isEmpty()) throw new IllegalArgumentException("Team name cannot be empty");

        return teamProfileManagementService.createTeam(team);
    }

    public Team create(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.createTeam(team, profiles);
    }


    /**
     * Set the markup for the given team.
     *
     * @param team Must not be null.
     * @param markup between 0 - 100%. Must not be null.
     * @throws Exception if an error occurred in updating the markup.
     * @throws NullPointerException if the team or markup is null.
     * @throws IllegalArgumentException if the markup is not between 0 and 100%.
     */
    public void setMarkup(Team team, BigDecimal markup) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(markup, "Markup cannot be null");

        if (markup.compareTo(BigDecimal.ZERO) < 0 || markup.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Markup must be between 0 and 100%");

        var oldMarkup = team.markup();
        team.markup(markup);

        // for at rollback i BLL hvis der sker in fejl i DAL.
        try {
            teamDAO.updateMultipliers(team);
        } catch (Exception e) {
            team.markup(oldMarkup);
            throw e;
        }
    }

    public void setMarkup(int teamId, BigDecimal markup) throws Exception {
        Objects.requireNonNull(markup, "Markup cannot be null");

        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");

        this.setMarkup(this.get(teamId), markup);
    }

    public void setMultipliers(Team team, BigDecimal markup, BigDecimal grossMargin) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(markup, "Markup cannot be null");
        Objects.requireNonNull(grossMargin, "Gross margin cannot be null");

        if (markup.compareTo(BigDecimal.ZERO) < 0 || markup.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Markup must be between 0 and 100%");

        if (grossMargin.compareTo(BigDecimal.ZERO) < 0 || grossMargin.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Gross margin must be between 0 and 100%");

        var oldMarkup = team.markup();
        var oldGrossMargin = team.grossMargin();
        team.markup(markup);
        team.grossMargin(grossMargin);

        // for at rollback i BLL hvis der sker in fejl i DAL.
        try {
            teamDAO.updateMultipliers(team);
        } catch (Exception e) {
            team.markup(oldMarkup);
            team.grossMargin(oldGrossMargin);
            throw e;
        }
    }

    public void setMultipliers(int teamId, BigDecimal markup, BigDecimal grossMargin) throws Exception {
        Objects.requireNonNull(markup, "Markup cannot be null");
        Objects.requireNonNull(grossMargin, "Gross margin cannot be null");

        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");

        this.setMultipliers(this.get(teamId), markup, grossMargin);
    }

    public boolean assignProfiles(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.assignProfilesToTeam(team, profiles);
    }

    public boolean updateProfiles(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.updateTeamProfiles(team, profiles);
    }

    public boolean updateProfile(int teamId, Profile profile) throws Exception {
        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");
        Objects.requireNonNull(profile, "Profile cannot be null");

        return teamProfileManagementService.updateTeamProfile(teamId, profile);
    }

    public boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.removeProfilesFromTeam(team, profiles);
    }

    public List<Profile> getTeamProfiles(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return getTeamProfiles(team.id());
    }

    public List<Profile> getTeamProfiles(int teamId) throws Exception {
        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.getTeamProfiles(teamId);
    }

    public Profile getTeamProfile(int teamId, int profileId) throws Exception {
        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");
        if (profileId <= 0) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return teamDAO.getTeamProfile(teamId, profileId);
    }

    public boolean archive(int teamId, boolean archive) throws Exception {
        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.archive(teamId, archive);
    }

    public boolean archive(Team team, boolean archive) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return archive(team.id(), archive);
    }

    public boolean archive(List<Team> teams) throws Exception {
        Objects.requireNonNull(teams, "Teams cannot be null");

        return teamDAO.archive(teams);
    }

    public List<Profile> canUnarchive(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return teamDAO.canUnarchive(team.id());
    }

    public boolean removeProfileFromTeam(int teamId, int profileId) throws Exception {
        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");
        if (profileId <= 0) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return teamProfileManagementService.removeProfileFromTeam(teamId, profileId);
    }

    public LocalDateTime getLastUpdated(int teamId) throws Exception {
        if (teamId <= 0) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.getLastUpdated(teamId);
    }
}
