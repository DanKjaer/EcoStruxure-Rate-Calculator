package ecostruxure.rate.calculator.bll.profile;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.be.dto.TeamProfileDTO;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.IProfileRepository;
import ecostruxure.rate.calculator.dal.ITeamProfileRepository;
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

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Type;

@Service
public class ProfileService {
    @PersistenceContext
    private EntityManager em;

    private final IProfileRepository profileRepository;
    private final ITeamProfileRepository teamProfileRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final List<IProfileObserver> observers;

    @Autowired
    public ProfileService(List<IProfileObserver> observers,
                          IProfileRepository profileRepository,
                          ITeamProfileRepository teamProfileRepository) {
        this.observers = observers;
        this.profileRepository = profileRepository;
        this.teamProfileRepository = teamProfileRepository;
    }

    @Transactional
    public Profile create(Profile profile) throws Exception {
        em.persist(profile);
        return profile;
    }

    public List<ProfileDTO> all() throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> profile = cq.from(Profile.class);

        cq.select(profile).where(cb.equal(profile.get("archived"), false));

        List<Profile> result = em.createQuery(cq).getResultList();

        Type listType = new TypeToken<List<ProfileDTO>>() {}.getType();
        List<ProfileDTO> profilesDTO = modelMapper.map(result, listType);
        profilesDTO.sort(Comparator.comparing(ProfileDTO::getName));
        return profilesDTO;
    }

    public ProfileDTO getById(UUID profileId) throws Exception {
        var profile = profileRepository.findById(profileId).orElseThrow(() -> new EntityNotFoundException("Profile not found."));
        var dto = modelMapper.map(profile, ProfileDTO.class);
        return dto;
    }

    public Profile update(Profile profile) throws Exception {
        profile.setEffectiveWorkHours(RateUtils.effectiveWorkHours(profile));

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