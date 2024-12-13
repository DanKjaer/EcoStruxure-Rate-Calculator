package ecostruxure.rate.calculator.bll.user;

import ecostruxure.rate.calculator.be.User;
import ecostruxure.rate.calculator.dal.IUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(User user) throws Exception {
        entityManager.persist(user);
        return user;
    }

    public Iterable<User> getUsers() {
        return null;
    }

    public User getById(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public boolean delete(UUID id) {
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
    }
}
