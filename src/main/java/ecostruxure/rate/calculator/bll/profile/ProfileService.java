package ecostruxure.rate.calculator.bll.profile;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.IProfileRepository;
import ecostruxure.rate.calculator.dal.ITeamProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Type;

@Service
public class ProfileService {
//    private static final int FINANCIAL_SCALE = 4;
//    private static final int GENERAL_SCALE = 2;
//    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
//
//    private static final BigDecimal HUNDRED = new BigDecimal(100);


    @Autowired
    private IProfileRepository profileRepository;
    @Autowired
    private ITeamProfileRepository teamProfileRepository;
    @PersistenceContext
    private EntityManager em;

    private final ModelMapper modelMapper = new ModelMapper();
    private final List<IProfileObserver> observers;

    @Autowired
    public ProfileService(List<IProfileObserver> observers) {
        this.observers = observers;
    }


    public Profile create(ProfileDTO profile) throws Exception {
        var newProfile = modelMapper.map(profile, Profile.class);
        em.getTransaction().begin();
        em.persist(newProfile);
        em.getTransaction().commit();
        return newProfile;
    }

    public Iterable<ProfileDTO> all() throws Exception {
        var profiles = profileRepository.findAllByArchived(false);

        Type listType = new TypeToken<Iterable<ProfileDTO>>() {}.getType();

        Iterable<ProfileDTO> profilesDto = modelMapper.map(profiles, listType);
        return profilesDto;
    }

    public ProfileDTO getById(UUID profileId) throws Exception {
        var profile = profileRepository.findById(profileId).orElseThrow(() -> new EntityNotFoundException("Profile not found."));
        var dto = modelMapper.map(profile, ProfileDTO.class);
        return dto;
    }

    public Profile update(Profile profile) throws Exception {
        profile.setEffectiveWorkHours(RateUtils.effectiveWorkHours(profile));

        for (TeamProfile teamProfile : profile.getTeamProfiles()) {
            if (teamProfile.getTeam() == null) {
                UUID teamProfileId = teamProfile.getTeamProfileId();
                TeamProfile existingTeamProfile = teamProfileRepository.findById(teamProfileId)
                        .orElseThrow(() -> new EntityNotFoundException("TeamProfile not found with ID: " + teamProfileId));

                teamProfile.setTeam(existingTeamProfile.getTeam());
            }
        }
        Profile updatedProfile = profileRepository.save(profile);
        notifyObservers(updatedProfile);
        return updatedProfile;
    }

    private void notifyObservers(Profile updatedProfile) {
        for (IProfileObserver observer : observers) {
            observer.update(updatedProfile);
        }
    }

    public boolean delete(UUID profileId) throws Exception {
        profileRepository.deleteById(profileId);
        return true;
    }

    public boolean archive(UUID profileId) throws Exception {
        var profile = profileRepository.findById(profileId).orElseThrow(() -> new EntityNotFoundException("Profile not found."));
        profile.setArchived(true);
        profileRepository.save(profile);
        return true;
    }

//    public Profile update(Profile profile) throws Exception{
//        profile.setEffectiveWorkHours(RateUtils.effectiveWorkHours(profile));
//
//        var profileUpdated = profileDAO.update(profile);
//        List<Team> teams = new ArrayList<>();
//        if (profileUpdated) {
//            teams = teamService.updateProfile(profile);
//        }
//        for (Team team : teams) {
//            projectService.updateProjectBasedOnTeam(team);
//        }
//        return profile;
//    }
//
//    public BigDecimal hourlyRate(Profile profile) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//        if (profile.getAnnualCost().compareTo(BigDecimal.ZERO) == 0 || profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
//            return BigDecimal.ZERO;
//        }
//        return annualCost(profile).divide(profile.getAnnualHours(), GENERAL_SCALE, ROUNDING_MODE);
//    }
//
//    public BigDecimal hourlyRate(Profile profile, BigDecimal utilizationPercentage) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//
//        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
//        return hourlyRate(profile).multiply(percentageAsDecimal);
//    }
//
//    public BigDecimal hourlyRate(List<Profile> profiles) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        return profiles.stream()
//                .map(this::hourlyRate)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//
//    public BigDecimal hourlyRate(List<Profile> profiles, BigDecimal markup) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//        var totalHourlyRate = profiles.stream()
//                .map(this::hourlyRate)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        var markupFactor = BigDecimal.ONE.add(markup.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE));
//        return totalHourlyRate.multiply(markupFactor);
//    }
//
//    public BigDecimal hourlyRate(List<Profile> profiles, BigDecimal markup, BigDecimal grossMargin) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//        var hourlyRateWithMarkup = hourlyRate(profiles, markup);
//        var grossMarginFactor = BigDecimal.ONE.add(grossMargin.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE));
//        return hourlyRateWithMarkup.multiply(grossMarginFactor).setScale(GENERAL_SCALE, ROUNDING_MODE);
//    }
//
//    public BigDecimal dayRate(Profile profile) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//
//        return hourlyRate(profile).multiply(profile.getHoursPerDay());
//    }
//
//    public BigDecimal dayRate(Profile profile, BigDecimal utilizationPercentage) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//
//        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
//        return dayRate(profile).multiply(percentageAsDecimal);
//    }
//
//    public BigDecimal dayRate(List<Profile> profiles) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        return profiles.stream()
//                .map(this::dayRate)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    public BigDecimal dayRate(List<Profile> profiles, BigDecimal markup) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        var totalDayRate = profiles.stream()
//                .map(this::dayRate)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        var markupFactor = BigDecimal.ONE.add(markup.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE));
//        return totalDayRate.multiply(markupFactor);
//    }
//
//    public BigDecimal dayRate(List<Profile> profiles, BigDecimal markup, BigDecimal grossMargin) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        var dayRateWithMarkup = dayRate(profiles, markup);
//        var grossMarginFactor = BigDecimal.ONE.add(grossMargin.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE));
//        return dayRateWithMarkup.multiply(grossMarginFactor).setScale(GENERAL_SCALE, ROUNDING_MODE);
//    }
//
//    public BigDecimal annualCost(Profile profile) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//
//        return profile.getAnnualCost().multiply(profile.getEffectivenessPercentage());
//    }
//
//    public BigDecimal annualCost(Profile profile, BigDecimal utilizationPercentage) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//
//        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
//        return annualCost(profile).multiply(percentageAsDecimal);
//    }
//
//    public BigDecimal annualCost(List<Profile> profiles) {
//        Objects.requireNonNull(profiles, "Profiles cannot be null");
//
//        return profiles.stream()
//                .map(this::annualCost)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    private void validateProfile(Profile profile) {
//        validateProfileNotNull(profile);
//        validateProfileValues(profile);
//        validateProfileValueScale(profile);
//    }
//
//    private void validateProfileNotNull(Profile profile) {
//        Objects.requireNonNull(profile, "Profile cannot be null");
//        Objects.requireNonNull(profile.getAnnualCost(), "Annual cost cannot be null");
//        Objects.requireNonNull(profile.getEffectivenessPercentage(), "Effectiveness cannot be null");
//        Objects.requireNonNull(profile.getName(), "Profile name cannot be null");
//        Objects.requireNonNull(profile.getCurrency(), "Profile currency cannot be null");
//    }
//
//    private void validateProfileValues(Profile profile) {
//        if (profile.getAnnualCost().compareTo(BigDecimal.ZERO) < 0)
//            throw new IllegalArgumentException("Annual salary cannot be negative");
//
//        if (profile.getHoursPerDay().compareTo(BigDecimal.ZERO) < 0 || profile.getHoursPerDay().compareTo(new BigDecimal("24")) > 0)
//            throw new IllegalArgumentException("Hours per day must be between 0 and 24");
//
//        if (profile.getAnnualCost().compareTo(new BigDecimal("999999999999999.9999")) > 0)
//            throw new IllegalArgumentException("Annual salary must be less than or equal to 999999999999999.9999");
//    }
//
//    private void validateProfileValueScale(Profile profile) {
//        // Tjek scale af finansielle felter
//        if (profile.getAnnualCost().scale() > FINANCIAL_SCALE)
//            throw new IllegalArgumentException("Annual salary scale must be less than or equal to " + FINANCIAL_SCALE);
//
//        // Tjek scale af numeriske felter
//        if (profile.getEffectivenessPercentage().scale() > GENERAL_SCALE)
//            throw new IllegalArgumentException("Effectiveness scale must be less than or equal to " + GENERAL_SCALE);
//        if (profile.getHoursPerDay().scale() > GENERAL_SCALE)
//            throw new IllegalArgumentException("Hours per day scale must be less than or equal to " + GENERAL_SCALE);
//    }
//
//    public List<Team> getTeams(Profile profile) throws Exception {
//        return profileDAO.getTeams(profile);
//    }
//
//    public List<Profile> allByTeam(Team team) throws Exception {
//        return profileDAO.allByTeam(team);
//    }
//
//    public BigDecimal getProfileCostAllocationForTeam(UUID id, UUID teamId) throws Exception {
//        return profileDAO.getProfileCostAllocationForTeam(id, teamId);
//    }
//
//    public BigDecimal getProfileHourAllocationForTeam(UUID id, UUID teamId) throws Exception {
//        return profileDAO.getProfileHourAllocationForTeam(id, teamId);
//    }
}

