package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ITeamRepository extends CrudRepository<Team, UUID> {
    Iterable<Team> findAllByArchived(boolean archived);

    @Query("SELECT DISTINCT tp.team FROM TeamProfile tp WHERE tp.profile.profileId = :profileId")
    List<Team> findTeamsByProfileId(UUID profileId);
}
