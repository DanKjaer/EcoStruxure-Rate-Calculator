package ecostruxure.rate.calculator.bll.profile.observers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.profile.IProfileObserver;
import ecostruxure.rate.calculator.bll.team.ITeamObserver;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
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
            team = RateUtils.calculateTotalAllocatedHoursAndCost(team);
            team = RateUtils.calculateTotalMarkupAndTotalGrossMargin(team);
            team = RateUtils.calculateRates(team);

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
