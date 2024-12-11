package ecostruxure.rate.calculator.bll.team;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.TeamDTO;
import ecostruxure.rate.calculator.be.dto.TeamProfileDTO;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
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
import java.sql.Timestamp;
import java.util.*;

@Service
public class TeamService {
    @PersistenceContext
    private EntityManager entityManager;

    private final ITeamRepository teamRepository;
    private final ITeamProfileRepository teamProfileRepository;
    private final List<ITeamObserver> teamObservers;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public TeamService(List<ITeamObserver> teamObservers,
                       ITeamRepository teamRepository,
                       ITeamProfileRepository teamProfileRepository) {
        this.teamObservers = teamObservers;
        this.teamRepository = teamRepository;
        this.teamProfileRepository = teamProfileRepository;
    }

    @Transactional
    public Team create(Team team) throws Exception {
        Team newTeam = RateUtils.calculateTotalAllocatedHoursAndCost(team);
        boolean anyTeamProfiles = newTeam.getTeamProfiles() != null && !newTeam.getTeamProfiles().isEmpty();
        if (anyTeamProfiles) {
            newTeam = RateUtils.calculateRates(newTeam);
        }
        newTeam.setTotalCostWithMarkup(newTeam.getTotalAllocatedCost());
        newTeam.setTotalCostWithGrossMargin(newTeam.getTotalAllocatedCost());
        newTeam.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        teamRepository.save(newTeam);
        notifyTeamObservers(newTeam);
        return newTeam;
    }

    public Iterable<Team> all() throws Exception {
        return teamRepository.findAllByArchived(false);
    }

    public TeamDTO getById(UUID teamId) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Team> cq = cb.createQuery(Team.class);
        Root<Team> team = cq.from(Team.class);

        //include
        team.fetch("teamProfiles", JoinType.LEFT).fetch("profile", JoinType.LEFT);

        //select by team id
        cq.select(team).where(cb.equal(team.get("teamId"), teamId));

        Team result = entityManager.createQuery(cq).getSingleResult();

        //map to TeamDTO
        TeamDTO teamDTO = modelMapper.map(result, TeamDTO.class);
        return teamDTO;
    }

    public List<TeamProfileDTO> getByProfileId(UUID profileId) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TeamProfile> cq = cb.createQuery(TeamProfile.class);
        Root<TeamProfile> teamProfile = cq.from(TeamProfile.class);

        //include
        teamProfile.fetch("team", JoinType.LEFT);
        teamProfile.fetch("profile", JoinType.LEFT);

        //select by profile id
        cq.select(teamProfile).where(cb.equal(teamProfile.get("profile").get("profileId"), profileId));

        List<TeamProfile> teamProfiles = entityManager.createQuery(cq).getResultList();

        //map to TeamProfileDTO list
        Type listType = new TypeToken<List<TeamProfileDTO>>() {}.getType();
        List<TeamProfileDTO> teamProfilesDTO = modelMapper.map(teamProfiles, listType);
        return teamProfilesDTO;
    }

    @Transactional
    public TeamDTO update(Team team) throws Exception {
        for (TeamProfile teamProfile : team.getTeamProfiles()) {
            if (teamProfile.getProfile() == null) {
                UUID teamProfileId = teamProfile.getTeamProfileId();
                TeamProfile existingTeamprofile = teamProfileRepository.findById(teamProfileId)
                        .orElseThrow(() -> new EntityNotFoundException("Team profile not found with ID: " + teamProfileId));
                teamProfile.setProfile(existingTeamprofile.getProfile());
            }
        }
        team = RateUtils.calculateTotalAllocatedHoursAndCost(team);
        team = RateUtils.calculateRates(team);
        team = RateUtils.calculateTotalMarkupAndTotalGrossMargin(team);
        team.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        teamRepository.save(team);
        notifyTeamObservers(team);
        TeamDTO updatedTeam = getById(team.getTeamId());

        return updatedTeam;
    }

    @Transactional
    public boolean deleteTeam(UUID teamId) throws Exception {
        Team oldTeam = teamRepository.findById(teamId).orElseThrow(() -> new Exception("Team not found."));
        teamRepository.deleteById(teamId);
        notifyTeamObservers(oldTeam);
        return !teamRepository.existsById(teamId);
    }

    public boolean archiveTeam(UUID teamId) throws Exception {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new Exception("Team not found."));
        team.setArchived(true);
        teamRepository.save(team);
        return true;
    }

    @Transactional
    public boolean deleteTeamProfile(UUID teamProfileId, UUID teamId) throws Exception{
        TeamProfile teamProfileToDelete = null;
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new Exception("Team not found."));
        for (TeamProfile teamProfile : team.getTeamProfiles()) {
            if (teamProfile.getTeamProfileId().equals(teamProfileId)) {
                teamProfile.setAllocationPercentageCost(BigDecimal.ZERO);
                teamProfile.setAllocationPercentageHours(BigDecimal.ZERO);
                teamProfile.setAllocatedCost(BigDecimal.ZERO);
                teamProfile.setAllocatedHours(BigDecimal.ZERO);

                teamProfileToDelete = teamProfile;
                break;
            }
        }

        if(teamProfileToDelete == null){
            throw new Exception("team profile not found");
        }

        team = RateUtils.calculateTotalAllocatedHoursAndCost(team);
        team = RateUtils.calculateRates(team);
        team = RateUtils.calculateTotalMarkupAndTotalGrossMargin(team);
        teamRepository.save(team);
        entityManager.detach(team);

        notifyTeamObservers(team);
        teamProfileRepository.deleteById(teamProfileId);
        return !teamRepository.existsById(teamProfileId);
    }

    @Transactional
    public List<TeamProfileDTO> addProfileToTeams(List<TeamProfile> teamProfiles) {
        Iterable<TeamProfile> updatedTeamProfiles =  teamProfileRepository.saveAll(teamProfiles);

        List<TeamProfileDTO> teamProfilesDTO = new ArrayList<>();
        for (TeamProfile teamProfile : updatedTeamProfiles) {
            TeamProfileDTO teamProfileDTO = modelMapper.map(teamProfile, TeamProfileDTO.class);
            Team team = teamRepository.findById(teamProfile.getTeam().getTeamId()).orElseThrow(() ->
                    new EntityNotFoundException("Team not found with ID: " + teamProfile.getTeam().getTeamId()));
            team = RateUtils.calculateTotalAllocatedHoursAndCost(team);
            team = RateUtils.calculateRates(team);
            team = RateUtils.calculateTotalMarkupAndTotalGrossMargin(team);
            teamRepository.save(team);

            notifyTeamObservers(team);
            teamProfilesDTO.add(teamProfileDTO);
        }

        return teamProfilesDTO;
    }

    private void notifyTeamObservers(Team updatedTeam) {
        for (ITeamObserver teamObserver : teamObservers) {
            teamObserver.update(updatedTeam);
        }
    }
}