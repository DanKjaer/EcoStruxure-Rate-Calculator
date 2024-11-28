package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.Profile;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IProfileRepository extends CrudRepository<Profile, UUID> {
}
