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

import java.util.List;
import java.util.UUID;
import java.lang.reflect.Type;

@Service
public class ProfileService {

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
}