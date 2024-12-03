package ecostruxure.rate.calculator.bll.profile.observers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.profile.IProfileObserver;
import ecostruxure.rate.calculator.bll.team.ITeamObserver;
import ecostruxure.rate.calculator.dal.ITeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TeamTotalObserver implements IProfileObserver {
    @Autowired
    private ITeamRepository teamRepository;
    private final List<ITeamObserver> teamObservers;

    @Autowired
    public TeamTotalObserver(List<ITeamObserver> teamObservers) {
        this.teamObservers = teamObservers;
    }

    @Override
    public void update(Profile profile) {
        List<Team> teams = teamRepository.findTeamsByProfileId(profile.getProfileId());

        for (Team team : teams) {
            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal totalHours = BigDecimal.ZERO;
            List<TeamProfile> teamProfiles = team.getTeamProfiles();
            for (TeamProfile teamProfile : teamProfiles) {
                totalCost = totalCost.add(teamProfile.getAllocatedCost());
                totalHours = totalHours.add(teamProfile.getAllocatedHours());
            }
            team.setTotalAllocatedHours(totalHours);
            team.setTotalAllocatedCost(totalCost);

            team.setTotalCostWithMarkup(totalCost.multiply(BigDecimal.ONE.add(team.getMarkupPercentage()
                    .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP))));
            team.setTotalCostWithGrossMargin(team.getTotalCostWithMarkup().multiply(BigDecimal.ONE.add(
                    team.getGrossMarginPercentage()
                            .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP))));

            team.setHourlyRate(team.getTotalAllocatedCost()
                    .divide(team.getTotalAllocatedHours(), 2, BigDecimal.ROUND_HALF_UP));
            team.setDayRate(team.getHourlyRate().multiply(BigDecimal.valueOf(8)));

            teamRepository.save(team);
            notifyTeamObservers(team);
        }
    }

    private void notifyTeamObservers(Team team) {
        for (ITeamObserver teamObserver : teamObservers) {
            teamObserver.update(team);
        }
    }
}
