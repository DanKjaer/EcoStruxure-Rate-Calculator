package ecostruxure.rate.calculator.bll.team.observers;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.team.ITeamObserver;
import ecostruxure.rate.calculator.dal.IProfileRepository;
import ecostruxure.rate.calculator.dal.ITeamProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProfileObserver implements ITeamObserver {

    private final ITeamProfileRepository teamProfileRepository;
    private final IProfileRepository profileRepository;

    @Autowired
    public ProfileObserver(ITeamProfileRepository teamProfileRepository, IProfileRepository profileRepository) {
        this.teamProfileRepository = teamProfileRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public void update(Team team) {
        for (TeamProfile teamProfile : team.getTeamProfiles()) {
            List<TeamProfile> teamProfiles = teamProfileRepository
                    .findAllByProfile_ProfileId(teamProfile.getProfile().getProfileId());
            BigDecimal totalCostAllocationPercentage = BigDecimal.ZERO;
            BigDecimal totalHoursAllocationPercentage = BigDecimal.ZERO;
            for (TeamProfile tp : teamProfiles) {
                totalCostAllocationPercentage = totalCostAllocationPercentage.add(tp.getAllocationPercentageCost());
                totalHoursAllocationPercentage = totalHoursAllocationPercentage.add(tp.getAllocationPercentageHours());
            }
            teamProfile.getProfile().setTotalCostAllocation(totalCostAllocationPercentage);
            teamProfile.getProfile().setTotalHourAllocation(totalHoursAllocationPercentage);
            profileRepository.save(teamProfile.getProfile());
        }
    }
}
