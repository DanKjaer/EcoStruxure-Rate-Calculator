package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.ProfileHistory;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.dal.dao.*;
import ecostruxure.rate.calculator.dal.db.ProfileDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class ProfileService {
    private static final int FINANCIAL_SCALE = 4;
    private static final int GENERAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    private final TeamProfileManagementService teamProfileManagementService;
    private final IProfileDAO profileDAO;


    public ProfileService() throws Exception {
        this.teamProfileManagementService = new TeamProfileManagementService();
        this.profileDAO = new ProfileDAO();
    }

    public Profile create(Profile profile) throws Exception {
        validateProfile(profile);
        if (profile.id() != 0) throw new IllegalArgumentException("Profile ID must be set upon creation.");
        if (profile.profileData().archived()) throw new IllegalArgumentException("Profile cannot be archived upon creation");

        return profileDAO.create(profile);
    }

    public List<Profile> all() throws Exception {
        return profileDAO.all();
    }

    public List<Profile> allWithUtilization() throws Exception {
        return profileDAO.allWithUtilization();
    }

    public List<Profile> allWithUtilizationByTeam(Team team) throws Exception {
        Objects.requireNonNull(team, "Team cannot be null");

        return profileDAO.allWithUtilizationByTeam(team.id());
    }

    public List<Profile> allWithUtilizationByTeam(int teamId) throws Exception {
        return profileDAO.allWithUtilizationByTeam(teamId);
    }

    public Profile get(int id) throws Exception {
        return profileDAO.get(id);
    }

    public BigDecimal totalHoursPercentage(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(new BigDecimal("100.00"), GENERAL_SCALE, ROUNDING_MODE);
        return profile.totalHours().multiply(percentageAsDecimal);
    }

    public BigDecimal hourlyRate(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        if(profile.totalHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return annualCost(profile).divide(profile.totalHours(), GENERAL_SCALE, ROUNDING_MODE);
    }

    public BigDecimal hourlyRate(ProfileHistory profile) {
        Objects.requireNonNull(profile, "ProfileHistory cannot be null");

        return annualCost(profile).divide(profile.totalHours(), GENERAL_SCALE, ROUNDING_MODE);
    }

    public BigDecimal hourlyRate(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(new BigDecimal("100.00"), GENERAL_SCALE, ROUNDING_MODE);
        return hourlyRate(profile).multiply(percentageAsDecimal);
    }

    public BigDecimal hourlyRate(List<Profile> profiles) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return profiles.stream()
                       .map(this::hourlyRate)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal hourlyRate(List<Profile> profiles, BigDecimal markup) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");
        var totalHourlyRate = profiles.stream()
                                      .map(this::hourlyRate)
                                      .reduce(BigDecimal.ZERO, BigDecimal::add);

        var markupFactor = BigDecimal.ONE.add(markup.divide(new BigDecimal("100"), GENERAL_SCALE, ROUNDING_MODE));
        return totalHourlyRate.multiply(markupFactor);
    }

    public BigDecimal hourlyRate(List<Profile> profiles, BigDecimal markup, BigDecimal grossMargin) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");
        var hourlyRateWithMarkup = hourlyRate(profiles, markup);
        var grossMarginFactor = BigDecimal.ONE.add(grossMargin.divide(new BigDecimal("100"), GENERAL_SCALE, ROUNDING_MODE));
        return hourlyRateWithMarkup.multiply(grossMarginFactor).setScale(GENERAL_SCALE, ROUNDING_MODE);
    }

    public BigDecimal dayRate(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        return hourlyRate(profile).multiply(profile.hoursPerDay());
    }

    public BigDecimal dayRate(ProfileHistory profile) {
        Objects.requireNonNull(profile, "ProfileHistory cannot be null");

        return hourlyRate(profile).multiply(profile.hoursPerDay());
    }

    public BigDecimal dayRate(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(new BigDecimal("100.00"), GENERAL_SCALE, ROUNDING_MODE);
        return dayRate(profile).multiply(percentageAsDecimal);
    }

    public BigDecimal dayRate(List<Profile> profiles) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return profiles.stream()
                       .map(this::dayRate)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal dayRate(List<Profile> profiles, BigDecimal markup) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        var totalDayRate = profiles.stream()
                                   .map(this::dayRate)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
        var markupFactor = BigDecimal.ONE.add(markup.divide(new BigDecimal("100"), GENERAL_SCALE, ROUNDING_MODE));
        return totalDayRate.multiply(markupFactor);
    }

    public BigDecimal dayRate(List<Profile> profiles, BigDecimal markup, BigDecimal grossMargin) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        var dayRateWithMarkup = dayRate(profiles, markup);
        var grossMarginFactor = BigDecimal.ONE.add(grossMargin.divide(new BigDecimal("100"), GENERAL_SCALE, ROUNDING_MODE));
        return dayRateWithMarkup.multiply(grossMarginFactor).setScale(GENERAL_SCALE, ROUNDING_MODE);
    }

    public BigDecimal annualCost(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        return profile.annualSalary().multiply(profile.effectiveness());
    }

    public BigDecimal annualCost(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(new BigDecimal("100.00"), GENERAL_SCALE, ROUNDING_MODE);
        return annualCost(profile).multiply(percentageAsDecimal);
    }

    public BigDecimal annualCost(ProfileHistory profile) {
        Objects.requireNonNull(profile, "ProfileHistory cannot be null");

        return profile.annualSalary().multiply(profile.effectiveness());
    }

    public BigDecimal annualCost(List<Profile> profiles) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        return profiles.stream()
                       .map(this::annualCost)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateProfile(Profile profile) {
        validateProfileNotNull(profile);
        validateProfileValues(profile);
        validateProfileValueScale(profile);
    }

    private void validateProfileNotNull(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(profile.annualSalary(), "Annual salary cannot be null");
        Objects.requireNonNull(profile.effectiveness(), "Effectiveness cannot be null");
        Objects.requireNonNull(profile.totalHours(), "Effective work hours cannot be null");
        Objects.requireNonNull(profile.profileData(), "Profile data cannot be null");
        Objects.requireNonNull(profile.profileData().name(), "Profile name cannot be null");
        Objects.requireNonNull(profile.profileData().currency(), "Profile currency cannot be null");
    }

    private void validateProfileValues(Profile profile) {
        if (profile.annualSalary().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Annual salary cannot be negative");

//        if (profile.effectiveness().compareTo(BigDecimal.ZERO) < 0 || profile.effectiveness().compareTo(new BigDecimal("1")) > 0)
//            throw new IllegalArgumentException("Effectiveness must be between 0 and 1");

        //if (profile.totalHours().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Effective work hours must be greater than zero");

        if (profile.hoursPerDay().compareTo(BigDecimal.ZERO) < 0 || profile.hoursPerDay().compareTo(new BigDecimal("24")) > 0)
            throw new IllegalArgumentException("Hours per day must be between 0 and 24");

        if (profile.annualSalary().compareTo(new BigDecimal("999999999999999.9999")) > 0)
            throw new IllegalArgumentException("Annual salary must be less than or equal to 999999999999999.9999");

         if (profile.totalHours().compareTo(new BigDecimal("8760")) > 0)
            throw new IllegalArgumentException("Effective work hours must be less than or equal to 8760");
    }

    private void validateProfileValueScale(Profile profile) {
        // Tjek scale af finansielle felter
        if (profile.annualSalary().scale() > FINANCIAL_SCALE) throw new IllegalArgumentException("Annual salary scale must be less than or equal to " + FINANCIAL_SCALE);

        // Tjek scale af numeriske felter
        if (profile.effectiveness().scale() > GENERAL_SCALE) throw new IllegalArgumentException("Effectiveness scale must be less than or equal to " + GENERAL_SCALE);
        if (profile.totalHours().scale() > GENERAL_SCALE) throw new IllegalArgumentException("Effective work hours scale must be less than or equal to " + GENERAL_SCALE);
        if (profile.hoursPerDay().scale() > GENERAL_SCALE) throw new IllegalArgumentException("Hours per day scale must be less than or equal to " + GENERAL_SCALE);
    }

    public List<Team> getTeams(Profile profile) throws Exception {
        return profileDAO.getTeams(profile);
    }

    public List<Profile> allByTeam(Team team) throws Exception {
        return profileDAO.allByTeam(team);
    }


    // todo: rename til total/sum
    public BigDecimal getTotalCostAllocation(int id) throws Exception {
        return profileDAO.getTotalCostAllocation(id);
    }

    public BigDecimal getTotalHourAllocation(int id) throws Exception {
        return profileDAO.getTotalHourAllocation(id);
    }

    public BigDecimal getProfileCostAllocationForTeam(int id, int teamId) throws Exception {
        return profileDAO.getProfileCostAllocationForTeam(id, teamId);
    }

    public BigDecimal getProfileHourAllocationForTeam(int id, int teamId) throws Exception {
        return profileDAO.getProfileHourAllocationForTeam(id, teamId);
    }

    public BigDecimal getProfileCostAllocationForArchivedTeam(int id, int teamId) throws Exception {
        return profileDAO.getProfileCostAllocationForTeam(id, teamId);
    }

    public BigDecimal getProfileHourAllocationForArchivedTeam(int id, int teamId) throws Exception {
        return profileDAO.getProfileHourAllocationForTeam(id, teamId);
    }


    public boolean isValidTeamUtilization(BigDecimal utilization) {
        return utilization.compareTo(BigDecimal.ZERO) >= 0 && utilization.compareTo(HUNDRED) <= 0;
    }

    public boolean shouldProcessUtilization(BigDecimal utilization) {
        return isValidTeamUtilization(utilization) && HUNDRED.compareTo(utilization) != 0;
    }

    public boolean update(Profile original, Profile updated) throws Exception {
        validateProfile(updated);
        if (updated.id() <= 0) throw new IllegalArgumentException("Profile ID must be greater than 0");
        if (updated.profileData().archived()) throw new IllegalArgumentException("Profile cannot be archived upon update");

        if (original.equals(updated)) return profileDAO.update(updated);

        return teamProfileManagementService.updateProfile(updated);
    }

    public boolean archive(Profile profile, boolean shouldArchive) throws Exception {
        if (profile.id() <= 0) throw new IllegalArgumentException("Profile ID must be greater than 0");

        return profileDAO.archive(profile, shouldArchive);
    }

    public boolean archive(List<Profile> profiles) throws Exception {
        if (profiles.isEmpty()) throw new IllegalArgumentException("Profiles cannot be empty");

        return profileDAO.archive(profiles);
    }
}
