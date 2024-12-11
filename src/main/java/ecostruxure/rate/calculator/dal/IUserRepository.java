package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
}
