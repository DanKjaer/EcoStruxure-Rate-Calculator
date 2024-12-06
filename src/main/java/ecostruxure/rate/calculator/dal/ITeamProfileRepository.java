package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ITeamProfileRepository extends CrudRepository<TeamProfile, UUID> {
    List<TeamProfile> findAllByProfile_ProfileId(UUID profileId);
    <T> SimpleJpaRepository<T, UUID> findTeamProfileByProfile_ProfileId(UUID profileProfileId);
}
