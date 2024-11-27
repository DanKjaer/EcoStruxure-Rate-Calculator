package ecostruxure.rate.calculator.dal.interfaces;

import ecostruxure.rate.calculator.be.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ITeamRepository extends CrudRepository<Team, UUID> {
}
