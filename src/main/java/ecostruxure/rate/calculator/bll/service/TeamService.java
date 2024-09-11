package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.dal.dao.ITeamDAO;
import ecostruxure.rate.calculator.dal.db.TeamDAO;
import org.apache.poi.xwpf.usermodel.BreakType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    public boolean update(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return teamDAO.update(team);
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

    public Team create(Team team, List<Profile> profiles) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return teamProfileManagementService.createTeam(team, profiles);
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

    public boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
        if (profileId == null) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return teamProfileManagementService.removeProfileFromTeam(teamId, profileId);
    }

    public LocalDateTime getLastUpdated(UUID teamId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");

        return teamDAO.getLastUpdated(teamId);
    }

    public BigDecimal getCostAllocationFromDatabase(UUID teamId, UUID profileId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
        if (profileId == null) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return teamDAO.getCostAllocation(teamId, profileId);
    }

    public BigDecimal getHourAllocationFromDatabase(UUID teamId, UUID profileId) throws Exception {
        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
        if (profileId == null) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return teamDAO.getHourAllocation(teamId, profileId);
    }

    // To nederste metoder skal måske flyttes til en anden klasse
    public BigDecimal calculateTotalAllocatedHoursFromProfile(UUID teamId) throws Exception {
        List<Profile> profiles = getTeamProfiles(teamId);
        BigDecimal totalAllocatedHours = BigDecimal.ZERO;

        for (Profile profile : profiles) {
            BigDecimal hourAllocationPercentage = getHourAllocationFromDatabase(teamId, profile.getProfileId());
            BigDecimal annualHours = profile.getAnnualHours();

            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalAllocatedHours = totalAllocatedHours.add(allocatedHours);
        }

        return totalAllocatedHours;
    }

    public BigDecimal calculateTotalAllocatedCostFromProfiles(UUID teamId) throws Exception {
        List<Profile> profiles = getTeamProfiles(teamId);
        BigDecimal totalAllocatedCost = BigDecimal.ZERO;

        for (Profile profile : profiles) {
            BigDecimal annualCost = profile.getAnnualCost();
            BigDecimal costAllocationPercentage = getCostAllocationFromDatabase(teamId, profile.getProfileId());

            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            totalAllocatedCost = totalAllocatedCost.add(allocatedCost);
        }
        return totalAllocatedCost;
    }

    public BigDecimal calculateTotalHourlyRateFromProfiles(UUID teamId) throws Exception {
        BigDecimal totalAllocatedCost = calculateTotalAllocatedCostFromProfiles(teamId);
        BigDecimal totalAllocatedHours = calculateTotalAllocatedHoursFromProfile(teamId);

        return totalAllocatedCost.divide(totalAllocatedHours, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateHourlyRateFromProfiles(UUID teamId) throws Exception {
        List<Profile> profiles = getTeamProfiles(teamId);
        BigDecimal hourlyRate = BigDecimal.ZERO;

        for (Profile profile : profiles) {
            BigDecimal annualCost = profile.getAnnualCost();
            if (profile.getAnnualCost().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            System.out.println("Annual cost teamSerivce: " + annualCost);
            BigDecimal costAllocationPercentage = getCostAllocationFromDatabase(teamId, profile.getProfileId());
            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal annualHours = profile.getAnnualHours();
            if(annualHours.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            System.out.println("Annual hours teamService: " + annualHours);
            BigDecimal hourAllocationPercentage = getHourAllocationFromDatabase(teamId, profile.getProfileId());

            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            hourlyRate = hourlyRate.add(allocatedCost.divide(allocatedHours, 2, RoundingMode.HALF_UP));
        }
        return hourlyRate;
    }

    public BigDecimal calculateDailyRateFromProfiles(UUID teamId) throws Exception {
        BigDecimal hourlyRate = calculateHourlyRateFromProfiles(teamId);
        System.out.println("Hourly rate teamService: " + hourlyRate);
        BigDecimal dailyRate = hourlyRate.multiply(BigDecimal.valueOf(8));
        System.out.println("Daily rate teamService: " + dailyRate);
        return dailyRate;
    }

    public BigDecimal calculateTotalDailyRateFromProfiles(UUID teamId) throws Exception {
        BigDecimal hourlyRate = calculateTotalHourlyRateFromProfiles(teamId);
        return hourlyRate.multiply(BigDecimal.valueOf(8));
    }
}