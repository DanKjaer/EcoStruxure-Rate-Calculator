package ecostruxure.rate.calculator.dal.interfaces;

import ecostruxure.rate.calculator.be.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IProjectRepository extends CrudRepository<Project, UUID> {
}
