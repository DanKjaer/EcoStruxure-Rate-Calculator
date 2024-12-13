package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IUserRepository extends CrudRepository<User, UUID> {
    Iterable<User> findAllByUsername(String username);
}
