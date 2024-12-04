package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.ProjectTeam;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IProjectTeamRepository extends CrudRepository<ProjectTeam, UUID> {
    <T> SimpleJpaRepository<T, UUID> findProjectTeamByProject_ProjectId(UUID projectProjectId);
}
