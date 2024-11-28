package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ITeamRepository extends CrudRepository<Team, UUID> {
    Iterable<Team> findAllByArchived(boolean archived);
}
