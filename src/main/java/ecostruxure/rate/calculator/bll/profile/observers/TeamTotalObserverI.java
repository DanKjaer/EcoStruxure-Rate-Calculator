package ecostruxure.rate.calculator.bll.profile.observers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.profile.IProfileObserver;
import ecostruxure.rate.calculator.dal.ITeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TeamTotalObserverI implements IProfileObserver {
    @Autowired
    private ITeamRepository teamRepository;

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
                System.out.println("total cost: " + totalCost);
                System.out.println("total hours: " + totalHours);
            }

            team.setTotalAllocatedCost(totalCost);
            team.setTotalCostWithMarkup(totalCost.multiply(BigDecimal.ONE.add(team.getMarkupPercentage()
                    .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP))));

            team.setTotalAllocatedHours(totalHours);
            team.setTotalCostWithGrossMargin(team.getTotalCostWithMarkup().multiply(BigDecimal.ONE.add(
                    team.getGrossMarginPercentage()
                            .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP))));

            teamRepository.save(team);
        }
    }
}
