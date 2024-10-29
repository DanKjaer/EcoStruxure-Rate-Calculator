package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.dal.dao.ITeamDAO;
import ecostruxure.rate.calculator.dal.db.TeamDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

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

    public Team get(UUID id) throws Exception {
        return teamDAO.get(id);
    }

    public Team get(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return this.get(team.getTeamId());
    }

    public Map<String, Object> getTeamAndProfiles(UUID teamId) {
        Map<String, Object> teamAndProfiles = new HashMap<>();
        try {
            Team team = get(teamId);
            List<TeamProfile> profiles = teamProfileManagementService.getTeamProfiles(teamId);
            teamAndProfiles.put("team", team);
            teamAndProfiles.put("profiles", profiles);
            return teamAndProfiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean update(UUID teamId,Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return teamDAO.update(teamId, team);
    }

    /**
     * Create a new team.
     *
     * @param team the team to create. Must not be null.
     * @return the created team that has same data as the input team, but with ID set.
     * @throws Exception                if an error occurred in creating the team.
     * @throws NullPointerException     if the input team or team name is null.
     * @throws IllegalArgumentException if the team name is empty.
     */
    public Team create(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(team.getName(), "Team name cannot be null");
        if (team.getName().isEmpty()) throw new IllegalArgumentException("Team name cannot be empty");

        return teamProfileManagementService.createTeam(team);
    }

    public Team create(Team team, List<TeamProfile> teamProfiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Team createdTeam = teamDAO.create(team);
        if(teamProfiles == null) {
            return createdTeam;
        }
        List<TeamProfile> updatedTeamProfile = updateAllocatedCostAndHours(teamProfiles);
        teamDAO.assignProfilesToTeam(createdTeam.getTeamId(), updatedTeamProfile);
        createdTeam = calculateTotalAllocatedCostAndHoursFromProfiles(createdTeam, updatedTeamProfile);
        teamDAO.updateTeamRateCostHours(createdTeam.getTeamId(), createdTeam.getHourlyRate(), createdTeam.getDayRate(),
                createdTeam.getTotalAllocatedCost(), createdTeam.getTotalAllocatedHours());
        return createdTeam;
    }


    /**
     * Set the markup for the given team.
     *
     * @param team   Must not be null.
     * @param markup between 0 - 100%. Must not be null.
     * @throws Exception                if an error occurred in updating the markup.
     * @throws NullPointerException     if the team or markup is null.
     * @throws IllegalArgumentException if the markup is not between 0 and 100%.
     */
    public void setMarkup(Team team, BigDecimal markup) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(markup, "Markup cannot be null");

        if (markup.compareTo(BigDecimal.ZERO) < 0 || markup.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Markup must be between 0 and 100%");

        var oldMarkup = team.getMarkup();
        team.setMarkup(markup);

        // for at rollback i BLL hvis der sker in fejl i DAL.
        try {
            teamDAO.updateMultipliers(team);
        } catch (Exception e) {
            e.printStackTrace();
            team.setMarkup(oldMarkup);
            throw e;
        }
    }

    public void setMarkup(UUID teamId, BigDecimal markup) throws Exception {
        Objects.requireNonNull(markup, "Markup cannot be null");

        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");

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

        var oldMarkup = team.getMarkup();
        var oldGrossMargin = team.getGrossMargin();
        team.setMarkup(markup);
        team.setGrossMargin(grossMargin);

        // for at rollback i BLL hvis der sker in fejl i DAL.
        try {
            teamDAO.updateMultipliers(team);
        } catch (Exception e) {
            e.printStackTrace();
            team.setMarkup(oldMarkup);
            team.setGrossMargin(oldGrossMargin);
            throw e;
        }
    }

    public void setMultipliers(UUID teamId, BigDecimal markup, BigDecimal grossMargin) throws Exception {
        Objects.requireNonNull(markup, "Markup cannot be null");
        Objects.requireNonNull(grossMargin, "Gross margin cannot be null");

        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");

        this.setMultipliers(this.get(teamId), markup, grossMargin);
    }

    public List<TeamProfile> assignProfiles(UUID teamId, List<TeamProfile> teamProfiles) throws Exception {
        Objects.requireNonNull(teamId, "Team cannot be null");
        Objects.requireNonNull(teamProfiles, "Profiles cannot be null");

        teamProfiles = calculateIndividualDayRates(teamProfiles);
        teamProfiles = updateAllocatedCostAndHours(teamProfiles);
        teamDAO.assignProfilesToTeam(teamId, teamProfiles);
        return teamProfiles;
    }

    public boolean updateProfiles(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.updateTeamProfiles(team, profiles);
    }

    public boolean updateProfile(UUID teamId, TeamProfile teamProfile) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
        Objects.requireNonNull(teamProfile, "Profile cannot be null");

        return teamProfileManagementService.updateTeamProfile(teamId, teamProfile);
    }

    public boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.removeProfilesFromTeam(team, profiles);
    }

    public List<Profile> getTeamProfiles(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return getTeamProfiles(team.getTeamId());
    }

    public List<Profile> getTeamProfiles(UUID teamId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.getTeamProfiles(teamId);
    }

    public Profile getTeamProfile(UUID teamId, UUID profileId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
        if (profileId == null) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return teamDAO.getTeamProfile(teamId, profileId);
    }

    public boolean archive(UUID teamId, boolean archive) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.archive(teamId, archive);
    }

    public boolean archive(Team team, boolean archive) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return archive(team.getTeamId(), archive);
    }

    public boolean archive(List<Team> teams) throws Exception {
        Objects.requireNonNull(teams, "Teams cannot be null");

        return teamDAO.archive(teams);
    }

    public List<Profile> canUnarchive(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return teamDAO.canUnarchive(team.getTeamId());
    }

    public LocalDateTime getLastUpdated(UUID teamId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.getLastUpdated(teamId);
    }

//  Alle metoder herfra og ned, skal måske flyttes til et andet sted end teamService.

    public Team calculateTotalAllocatedCostAndHoursFromProfiles(Team team, List<TeamProfile> teamProfiles) throws Exception {
        BigDecimal totalAllocatedCost = BigDecimal.ZERO;
        BigDecimal totalAllocatedHours = BigDecimal.ZERO;
        BigDecimal totalHourlyRate = BigDecimal.ZERO;
        BigDecimal totalDailyRate = BigDecimal.ZERO;

        for (TeamProfile teamProfile : teamProfiles) {
            BigDecimal annualCost = teamProfile.getAnnualCost();
            BigDecimal annualHours = teamProfile.getAnnualHours();

            BigDecimal costAllocationPercentage = teamProfile.getCostAllocation();
            BigDecimal hourAllocationPercentage = teamProfile.getHourAllocation();

            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal hourlyRateOnTeam = annualCost.divide(annualHours, 2, RoundingMode.HALF_UP);
            BigDecimal dailyRateOnTeam = hourlyRateOnTeam.multiply(BigDecimal.valueOf(8));

            totalAllocatedCost = totalAllocatedCost.add(allocatedCost);
            totalAllocatedHours = totalAllocatedHours.add(allocatedHours);
            totalHourlyRate = totalHourlyRate.add(hourlyRateOnTeam);
            totalDailyRate = totalDailyRate.add(dailyRateOnTeam);
        }

        team.setTotalAllocatedCost(totalAllocatedCost);
        team.setTotalAllocatedHours(totalAllocatedHours);
        team.setHourlyRate(totalHourlyRate);
        team.setDayRate(totalDailyRate);
        return team;
    }

    public List<TeamProfile> calculateIndividualDayRates(List<TeamProfile> teamProfiles) throws Exception{

        for (TeamProfile teamProfile : teamProfiles) {
            BigDecimal annualCost = teamProfile.getAnnualCost();
            BigDecimal annualHours = teamProfile.getAnnualHours();
            BigDecimal costAllocationPercentage = teamProfile.getCostAllocation();
            BigDecimal hourAllocationPercentage = teamProfile.getHourAllocation();

            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);


            if (allocatedCost.compareTo(BigDecimal.ZERO) == 0 || allocatedHours.compareTo(BigDecimal.ZERO) == 0) {
                teamProfile.setDayRateOnTeam(BigDecimal.ZERO);
            } else {
                BigDecimal hourlyRate = allocatedCost.divide(allocatedHours, 2, RoundingMode.HALF_UP);
                BigDecimal dailyRate = hourlyRate.multiply(BigDecimal.valueOf(8));

                teamProfile.setDayRateOnTeam(dailyRate);
            }
        }
        return teamProfiles;
    }

    public List<TeamProfile> updateAllocatedCostAndHours(List<TeamProfile> teamProfiles) throws Exception {

        for(TeamProfile teamProfile : teamProfiles) {
            BigDecimal annualCost = teamProfile.getAnnualCost();
            BigDecimal annualHours = teamProfile.getAnnualHours();

            BigDecimal costAllocationPercentage;
            BigDecimal hourAllocationPercentage;
            BigDecimal dayRate;

            costAllocationPercentage = teamProfile.getCostAllocation();
            hourAllocationPercentage = teamProfile.getHourAllocation();

            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (annualHours.compareTo(BigDecimal.ZERO) == 0) {
                dayRate = BigDecimal.ZERO;
            } else {
                dayRate = annualCost.divide(annualHours, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(8));
            }

            teamProfile.setAllocatedCostOnTeam(allocatedCost);
            teamProfile.setAllocatedHoursOnTeam(allocatedHours);
            teamProfile.setDayRateOnTeam(dayRate);
        }
        return teamProfiles;
    }

    public List<TeamProfile> saveTeamProfiles(UUID teamId, List<TeamProfile> teamProfiles) throws Exception {
        return teamDAO.saveTeamProfiles(teamId, teamProfiles);
    }

    public boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws SQLException {
        try {
            if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
            return teamDAO.removeProfileFromTeam(teamId, profileId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public TeamProfile updateTeamProfile(UUID teamId, TeamProfile teamProfile) {
        try {
            if (teamId == null) throw new IllegalArgumentException("Team ID must not be empty");
            var updatedProfileList = updateAllocatedCostAndHours(Collections.singletonList(teamProfile));
            return teamDAO.updateTeamProfile(teamId, updatedProfileList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}