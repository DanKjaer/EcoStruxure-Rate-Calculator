package ecostruxure.rate.calculator.bll.profile.observers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.profile.ProfileObserver;
import ecostruxure.rate.calculator.dal.ITeamProfileRepository;
import ecostruxure.rate.calculator.dal.ITeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TeamProfileAllocationObserver implements ProfileObserver {

    @Autowired
    private ITeamProfileRepository teamProfileRepository;

    @Override
    public void update(Profile profile) {
        List<TeamProfile> teamProfiles = teamProfileRepository.findTeamProfilesByProfile_ProfileId(profile.getProfileId());
        for (TeamProfile teamProfile : teamProfiles) {
            teamProfile.setAllocatedCost(profile.getAnnualCost().multiply(teamProfile.getAllocationPercentageCost()
                    .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP)));
            teamProfile.setAllocatedHours(profile.getAnnualHours().multiply(teamProfile.getAllocationPercentageHours()
                    .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP)));
            teamProfileRepository.save(teamProfile);
        }

    }
}
