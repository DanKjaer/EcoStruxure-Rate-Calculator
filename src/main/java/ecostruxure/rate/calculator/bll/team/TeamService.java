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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TeamService {
    @PersistenceContext
    private EntityManager em;

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

    public Team create(Team team) throws Exception {
        em.getTransaction().begin();
        em.persist(team);
        em.getTransaction().commit();
        return team;
    }

    public Iterable<Team> all() throws Exception {
        return teamRepository.findAllByArchived(false);
    }

    public TeamDTO getById(UUID teamId) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Team> cq = cb.createQuery(Team.class);
        Root<Team> team = cq.from(Team.class);

        //include
        team.fetch("teamProfiles", JoinType.LEFT).fetch("profile", JoinType.LEFT);

        //select by team id
        cq.select(team).where(cb.equal(team.get("teamId"), teamId));

        Team result = em.createQuery(cq).getSingleResult();

        //map to TeamDTO
        TeamDTO teamDTO = modelMapper.map(result, TeamDTO.class);
        System.out.println("name on a profile: " + teamDTO.getTeamProfiles().get(0).getProfile().getName());
        return teamDTO;
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
        teamRepository.save(team);
        notifyTeamObservers(team);
        TeamDTO updatedTeam = getById(team.getTeamId());

        return updatedTeam;
    }

    private void notifyTeamObservers(Team updatedTeam) {
        for (ITeamObserver teamObserver : teamObservers) {
            teamObserver.update(updatedTeam);
        }
    }

    public boolean deleteTeam(UUID teamId) throws Exception {
        teamRepository.deleteById(teamId);
        return !teamRepository.existsById(teamId);
    }

    public boolean archiveTeam(UUID teamId) throws Exception {
        var team = teamRepository.findById(teamId).orElseThrow(() -> new Exception("Team not found."));
        team.setArchived(true);
        teamRepository.save(team);
        return true;
    }

    public boolean deleteTeamProfile(UUID teamProfileId) throws Exception {
        teamProfileRepository.deleteById(teamProfileId);
        return !teamProfileRepository.existsById(teamProfileId);
    }

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

    public boolean deleteTeamProfile(UUID teamProfileId) throws Exception{
        teamProfileRepository.deleteById(teamProfileId);
        return !teamProfileRepository.existsById(teamProfileId);
    }

}