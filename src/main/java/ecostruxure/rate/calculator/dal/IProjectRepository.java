package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectTeam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface IProjectRepository extends CrudRepository<Project, UUID> {
    Iterable<Project> findAllByProjectArchived(Boolean projectArchived);

    @Query("SELECT p FROM Project p JOIN p.projectTeams pt WHERE pt.team.teamId = :teamId")
    List<Project> findProjectsByTeamId(UUID teamId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.projectTeams pt LEFT JOIN FETCH pt.team WHERE p.projectArchived = false")
    List<Project> findAllWithProjectTeams();
}
