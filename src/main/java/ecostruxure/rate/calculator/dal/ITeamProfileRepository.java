package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.TeamProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ITeamProfileRepository extends CrudRepository<TeamProfile, UUID> {
    List<TeamProfile> findTeamProfilesByProfile_ProfileId(UUID profileProfileId);
}
