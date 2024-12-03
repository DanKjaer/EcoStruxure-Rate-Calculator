package ecostruxure.rate.calculator.bll.team;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.be.dto.TeamProfileDTO;
import ecostruxure.rate.calculator.dal.ITeamProfileRepository;
import ecostruxure.rate.calculator.dal.ITeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private ITeamRepository teamRepository;
    @Autowired
    private ITeamProfileRepository teamProfileRepository;
    @PersistenceContext
    private EntityManager em;

    private final List<ITeamObserver> teamObservers;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public TeamService(List<ITeamObserver> teamObservers) {
        this.teamObservers = teamObservers;
    }

    @Transactional
    public Team create(Team team) throws Exception {
        em.getTransaction().begin();
        em.persist(team);
        em.getTransaction().commit();
        return team;
    }

    public Iterable<Team> all() throws Exception {
        return teamRepository.findAllByArchived(false);
    }

    public Team getById(UUID teamId) throws Exception {
        return teamRepository.findById(teamId).orElseThrow(() -> new Exception("Team not found."));
    }

    public List<TeamProfileDTO> getByProfileId(UUID profileId) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TeamProfile> cq = cb.createQuery(TeamProfile.class);
        Root<TeamProfile> teamProfile = cq.from(TeamProfile.class);

        //include
        teamProfile.fetch("team", JoinType.LEFT);
        teamProfile.fetch("profile", JoinType.LEFT);

        //select by profile id
        cq.select(teamProfile).where(cb.equal(teamProfile.get("profile").get("profileId"), profileId));

        List<TeamProfile> teamProfiles = em.createQuery(cq).getResultList();

        //map to TeamProfileDTO list
        Type listType = new TypeToken<List<TeamProfileDTO>>() {}.getType();
        List<TeamProfileDTO> teamProfilesDTO = modelMapper.map(teamProfiles, listType);
        return teamProfilesDTO;
    }

    public Team update(Team team) throws Exception {
        for (TeamProfile teamProfile : team.getTeamProfiles()) {
            if (teamProfile.getProfile() == null) {
                UUID teamProfileId = teamProfile.getTeamProfileId();
                TeamProfile existingTeamprofile = teamProfileRepository.findById(teamProfileId)
                        .orElseThrow(() -> new EntityNotFoundException("Team profile not found with ID: " + teamProfileId));
                teamProfile.setProfile(existingTeamprofile.getProfile());
            }
        }
        team = calculateTotalMarkupAndTotalGrossMargin(team);
        Team updatedTeam = teamRepository.save(team);

        notifyTeamObservers(updatedTeam);

        return updatedTeam;
    }

    private void notifyTeamObservers(Team updatedTeam) {
        for (ITeamObserver teamObserver : teamObservers) {
            teamObserver.update(updatedTeam);
        }
    }

    public boolean delete(UUID teamId) throws Exception {
        teamRepository.deleteById(teamId);
        return !teamRepository.existsById(teamId);
    }

    public boolean archive(UUID teamId) throws Exception {
        var team = teamRepository.findById(teamId).orElseThrow(() -> new Exception("Team not found."));
        team.setArchived(true);
        teamRepository.save(team);
        return true;
    }

//    public Map<String, Object> getTeamAndProfiles(UUID teamId) {
//        Map<String, Object> teamAndProfiles = new HashMap<>();
//        try {
//            Team team = get(teamId);
//            List<TeamProfile> profiles = teamProfileManagementService.getTeamProfiles(teamId);
//            teamAndProfiles.put("team", team);
//            teamAndProfiles.put("profiles", profiles);
//            return teamAndProfiles;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public Team update(UUID teamId,Team team) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//        var updatedTeam = calculateTotalMarkupAndTotalGrossMargin(team);
//
//        return teamDAO.update(teamId, updatedTeam);
//    }
//
//    /**
//     * This one is used by controller to create profiles
//     * @param team
//     * @param teamProfiles
//     * @return
//     * @throws Exception
//     */
//    public Team create(Team team, List<TeamProfile> teamProfiles) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//        Team createdTeam = teamDAO.create(team);
//        if(teamProfiles == null) {
//            return createdTeam;
//        }
//        List<TeamProfile> updatedTeamProfile = updateAllocatedCostAndHours(teamProfiles);
//        teamDAO.assignProfilesToTeam(createdTeam.getTeamId(), updatedTeamProfile);
//        createdTeam = calculateTotalAllocatedCostAndHoursFromProfiles(createdTeam, updatedTeamProfile);
//        teamDAO.updateTeamRateCostHours(createdTeam);
//        teamDAO.updateTotalAllocationOfProfiles(updatedTeamProfile);
//        return createdTeam;
//    }
//
//    /**
//     * Set the markup for the given team.
//     *
//     * @param team   Must not be null.
//     * @param markup between 0 - 100%. Must not be null.
//     * @throws Exception                if an error occurred in updating the markup.
//     * @throws NullPointerException     if the team or markup is null.
//     * @throws IllegalArgumentException if the markup is not between 0 and 100%.
//     */
//    public void setMarkup(Team team, BigDecimal markup) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//        Objects.requireNonNull(markup, "Markup cannot be null");
//
//        if (markup.compareTo(BigDecimal.ZERO) < 0 || markup.compareTo(BigDecimal.valueOf(100)) > 0)
//            throw new IllegalArgumentException("Markup must be between 0 and 100%");
//
//        var oldMarkup = team.getMarkup();
//        team.setMarkup(markup);
//
//        // for at rollback i BLL hvis der sker in fejl i DAL.
//        try {
//            teamDAO.updateMultipliers(team);
//        } catch (Exception e) {
//            e.printStackTrace();
//            team.setMarkup(oldMarkup);
//            throw e;
//        }
//    }
//
//    public void setMarkup(UUID teamId, BigDecimal markup) throws Exception {
//        Objects.requireNonNull(markup, "Markup cannot be null");
//
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//
//        this.setMarkup(this.get(teamId), markup);
//    }
//
//    public void setMultipliers(Team team, BigDecimal markup, BigDecimal grossMargin) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//        Objects.requireNonNull(markup, "Markup cannot be null");
//        Objects.requireNonNull(grossMargin, "Gross margin cannot be null");
//
//        if (markup.compareTo(BigDecimal.ZERO) < 0 || markup.compareTo(BigDecimal.valueOf(100)) > 0)
//            throw new IllegalArgumentException("Markup must be between 0 and 100%");
//
//        if (grossMargin.compareTo(BigDecimal.ZERO) < 0 || grossMargin.compareTo(BigDecimal.valueOf(100)) > 0)
//            throw new IllegalArgumentException("Gross margin must be between 0 and 100%");
//
//        var oldMarkup = team.getMarkup();
//        var oldGrossMargin = team.getGrossMargin();
//        team.setMarkup(markup);
//        team.setGrossMargin(grossMargin);
//
//        // for at rollback i BLL hvis der sker in fejl i DAL.
//        try {
//            teamDAO.updateMultipliers(team);
//        } catch (Exception e) {
//            e.printStackTrace();
//            team.setMarkup(oldMarkup);
//            team.setGrossMargin(oldGrossMargin);
//            throw e;
//        }
//    }
//
//    public void setMultipliers(UUID teamId, BigDecimal markup, BigDecimal grossMargin) throws Exception {
//        Objects.requireNonNull(markup, "Markup cannot be null");
//        Objects.requireNonNull(grossMargin, "Gross margin cannot be null");
//
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//
//        this.setMultipliers(this.get(teamId), markup, grossMargin);
//    }
//
//    public List<TeamProfile> assignProfiles(UUID teamId, List<TeamProfile> teamProfiles) throws Exception {
//        Objects.requireNonNull(teamId, "Team cannot be null");
//        Objects.requireNonNull(teamProfiles, "Profiles cannot be null");
//
//        teamProfiles = calculateIndividualDayRates(teamProfiles);
//        teamProfiles = updateAllocatedCostAndHours(teamProfiles);
//        teamDAO.assignProfilesToTeam(teamId, teamProfiles);
//        teamDAO.updateTotalAllocationOfProfiles(teamProfiles);
//
//        return teamProfiles;
//    }
//
//    public boolean updateProfiles(Team team, List<Profile> profiles) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        return teamProfileManagementService.updateTeamProfiles(team, profiles);
//    }
//
//    public boolean updateProfile(UUID teamId, TeamProfile teamProfile) throws Exception {
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//        Objects.requireNonNull(teamProfile, "Profile cannot be null");
//
//        return teamProfileManagementService.updateTeamProfile(teamId, teamProfile);
//    }
//
//    /**
//     * Updates team profiles and teams based on one profile
//     * @param profile
//     * @return
//     * @throws Exception
//     */
//    public List<Team> updateProfile(Profile profile) throws Exception {
//        var teamProfilesMatchingProfile = teamDAO.getByProfileId(profile.getProfileId());
//        var teams = teamDAO.getTeams(teamProfilesMatchingProfile);
//        var teamProfilesOnTeams = teamDAO.getTeamProfiles(teams);
//        teamProfilesMatchingProfile = updateAllocatedCostAndHours(teamProfilesMatchingProfile);
//
//        List<Team> updatedTeams = new ArrayList<>();
//        for (Team team : teams) {
//            var id = team.getTeamId();
//            var teamProfiles = teamProfilesOnTeams.stream().filter(tp -> tp.getTeamId().equals(id)).toList();
//            team = calculateTotalAllocatedCostAndHoursFromProfiles(team, teamProfiles);
//            team = calculateTotalMarkupAndTotalGrossMargin(team);
//            var updatedTeam = teamDAO.update(team.getTeamId(), team);
//            updatedTeams.add(updatedTeam);
//        }
//        teamDAO.updateTeamProfile(teamProfilesMatchingProfile);
//
//        return updatedTeams;
//    }
//
//    public boolean removeAssignedProfiles(Team team, List<Profile> profiles) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        return teamProfileManagementService.removeProfilesFromTeam(team, profiles);
//    }
//
//    public List<Profile> getTeamProfiles(Team team) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//
//        return getTeamProfiles(team.getTeamId());
//    }
//
//    public List<Profile> getTeamProfiles(UUID teamId) throws Exception {
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//
//        return teamDAO.getTeamProfiles(teamId);
//    }
//
//    public Profile getTeamProfile(UUID teamId, UUID profileId) throws Exception {
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//        if (profileId == null) throw new IllegalArgumentException("Profile ID must be greater than 0");
//
//        return teamDAO.getTeamProfile(teamId, profileId);
//    }
//
//    /**
//     * Used by Controller to "delete" a team by archiving it.
//     * @param teamId
//     * @param archive
//     * @return true if the team was archived, false otherwise.
//     * @throws Exception
//     */
//    public boolean archive(UUID teamId, boolean archive) throws Exception {
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//        var response = teamDAO.archive(teamId, archive);
//
//        return response;
//    }
//
//    public boolean archive(Team team, boolean archive) throws Exception {
//        Objects.requireNonNull(team, "Team cannot be null");
//
//        return archive(team.getTeamId(), archive);
//    }
//
//    public boolean archive(List<Team> teams) throws Exception {
//        Objects.requireNonNull(teams, "Teams cannot be null");
//
//        return teamDAO.archive(teams);
//    }
//
//    public LocalDateTime getLastUpdated(UUID teamId) throws Exception {
//        if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//
//        return teamDAO.getLastUpdated(teamId);
//    }
//
//  Alle metoder herfra og ned, skal måske flyttes til et andet sted end teamService.
//
//    /**
//     * Calculate the total allocated cost and hours for a team.
//     * @param team
//     * @param teamProfiles
//     * @return
//     * @throws Exception
//     */
//    public Team calculateTotalAllocatedCostAndHoursFromProfiles(Team team, List<TeamProfile> teamProfiles) throws Exception {
//        BigDecimal totalAllocatedCost = BigDecimal.ZERO;
//        BigDecimal totalAllocatedHours = BigDecimal.ZERO;
//        BigDecimal totalHourlyRate = BigDecimal.ZERO;
//        BigDecimal totalDailyRate = BigDecimal.ZERO;
//
//        for (TeamProfile teamProfile : teamProfiles) {
//            BigDecimal annualCost = teamProfile.getAnnualCost();
//            BigDecimal annualHours = teamProfile.getAnnualHours();
//
//            BigDecimal costAllocationPercentage = teamProfile.getCostAllocation();
//            BigDecimal hourAllocationPercentage = teamProfile.getHourAllocation();
//
//            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//            BigDecimal hourlyRateOnTeam;
//            if (annualHours.compareTo(BigDecimal.ZERO) == 0) {
//                hourlyRateOnTeam = BigDecimal.ZERO;
//            } else {
//                hourlyRateOnTeam = annualCost.divide(annualHours, 2, RoundingMode.HALF_UP);
//            }
//            BigDecimal dailyRateOnTeam = hourlyRateOnTeam.multiply(BigDecimal.valueOf(8));
//
//            totalAllocatedCost = totalAllocatedCost.add(allocatedCost);
//            totalAllocatedHours = totalAllocatedHours.add(allocatedHours);
//            totalHourlyRate = totalHourlyRate.add(hourlyRateOnTeam);
//            totalDailyRate = totalDailyRate.add(dailyRateOnTeam);
//        }
//
//        team.setTotalAllocatedCost(totalAllocatedCost);
//        team.setTotalAllocatedHours(totalAllocatedHours);
//        team.setHourlyRate(totalHourlyRate);
//        team.setDayRate(totalDailyRate);
//        return team;
//    }
//
//    public List<TeamProfile> calculateIndividualDayRates(List<TeamProfile> teamProfiles) throws Exception{
//
//        for (TeamProfile teamProfile : teamProfiles) {
//            BigDecimal annualCost = teamProfile.getAnnualCost();
//            BigDecimal annualHours = teamProfile.getAnnualHours();
//            BigDecimal costAllocationPercentage = teamProfile.getCostAllocation();
//            BigDecimal hourAllocationPercentage = teamProfile.getHourAllocation();
//
//            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//
//
//            if (allocatedCost.compareTo(BigDecimal.ZERO) == 0 || allocatedHours.compareTo(BigDecimal.ZERO) == 0) {
//                teamProfile.setDayRateOnTeam(BigDecimal.ZERO);
//            } else {
//                BigDecimal hourlyRate = allocatedCost.divide(allocatedHours, 2, RoundingMode.HALF_UP);
//                BigDecimal dailyRate = hourlyRate.multiply(BigDecimal.valueOf(8));
//
//                teamProfile.setDayRateOnTeam(dailyRate);
//            }
//        }
//        return teamProfiles;
//    }
//
//    /**
//     * Calculates the allocated cost, hours and day rate for a team profile.
//     * @param teamProfiles
//     * @return
//     * @throws Exception
//     */
//    public List<TeamProfile> updateAllocatedCostAndHours(List<TeamProfile> teamProfiles) throws Exception {
//
//        for(TeamProfile teamProfile : teamProfiles) {
//            BigDecimal annualCost = teamProfile.getAnnualCost();
//            BigDecimal annualHours = teamProfile.getAnnualHours();
//
//            BigDecimal costAllocationPercentage;
//            BigDecimal hourAllocationPercentage;
//            BigDecimal dayRate;
//
//            costAllocationPercentage = teamProfile.getCostAllocation();
//            hourAllocationPercentage = teamProfile.getHourAllocation();
//
//            BigDecimal allocatedCost = annualCost.multiply(costAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//            BigDecimal allocatedHours = annualHours.multiply(hourAllocationPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//
//            if (annualHours.compareTo(BigDecimal.ZERO) == 0) {
//                dayRate = BigDecimal.ZERO;
//            } else {
//                dayRate = annualCost.divide(annualHours, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(8));
//            }
//
//            teamProfile.setAllocatedCostOnTeam(allocatedCost);
//            teamProfile.setAllocatedHoursOnTeam(allocatedHours);
//            teamProfile.setDayRateOnTeam(dayRate);
//        }
//        return teamProfiles;
//    }
//
//    public List<TeamProfile> saveTeamProfiles(UUID teamId, List<TeamProfile> teamProfiles) throws Exception {
//        return teamDAO.saveTeamProfiles(teamId, teamProfiles);
//    }
//
//    public boolean removeProfileFromTeam(UUID teamId, UUID profileId) throws SQLException {
//        try {
//            if (teamId == null) throw new IllegalArgumentException("Team ID must be greater than 0");
//            return teamDAO.removeProfileFromTeam(teamId, profileId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public TeamProfile updateTeamProfile(UUID teamId, TeamProfile teamProfile) {
//        try {
//            if (teamId == null) throw new IllegalArgumentException("Team ID must not be empty");
//
//            // Update allocated cost and hours for the team profile
//            var updatedProfileList = updateAllocatedCostAndHours(Collections.singletonList(teamProfile));
//            TeamProfile updateTeamProfile  = teamDAO.updateTeamProfile(teamId, updatedProfileList.get(0));
//
//            // Update total allocated cost and hours for the teams on profiles
//            teamDAO.updateTotalAllocationOfProfiles(updatedProfileList);
//
//            return updateTeamProfile;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
    public Team calculateTotalMarkupAndTotalGrossMargin(Team team) {
        BigDecimal markup = team.getMarkupPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal grossMargin = team.getGrossMarginPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal totalAnnualCost = team.getTotalAllocatedCost();

        BigDecimal totalMarkup = totalAnnualCost.multiply(markup);
        BigDecimal totalGrossMargin = totalMarkup.multiply(grossMargin);

        team.setTotalCostWithMarkup(totalMarkup);
        team.setTotalCostWithGrossMargin(totalGrossMargin);

        return team;
    }
}