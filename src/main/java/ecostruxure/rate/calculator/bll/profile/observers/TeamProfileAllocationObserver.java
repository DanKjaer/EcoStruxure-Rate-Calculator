package ecostruxure.rate.calculator.bll.profile.observers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.profile.IProfileObserver;
import ecostruxure.rate.calculator.dal.ITeamProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TeamProfileAllocationObserver implements IProfileObserver {
    private final ITeamProfileRepository teamProfileRepository;

    @Autowired
    public TeamProfileAllocationObserver(ITeamProfileRepository teamProfileRepository) {
        this.teamProfileRepository = teamProfileRepository;
    }

    @Override
    public void update(Profile profile) {
        List<TeamProfile> teamProfiles = teamProfileRepository.findAllByProfile_ProfileId(profile.getProfileId());
        for (TeamProfile teamProfile : teamProfiles) {
            teamProfile.setAllocatedCost(profile.getAnnualCost().multiply(teamProfile.getAllocationPercentageCost()
                    .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP)));
            teamProfile.setAllocatedHours(profile.getAnnualHours().multiply(teamProfile.getAllocationPercentageHours()
                    .divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP)));
            teamProfileRepository.save(teamProfile);
        }
    }
}
