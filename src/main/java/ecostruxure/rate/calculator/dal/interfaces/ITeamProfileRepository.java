package ecostruxure.rate.calculator.dal.interfaces;

import ecostruxure.rate.calculator.be.TeamProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ITeamProfileRepository extends CrudRepository<TeamProfile, UUID> {
}
