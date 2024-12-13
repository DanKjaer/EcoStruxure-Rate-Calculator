package ecostruxure.rate.calculator.bll.user;

import ecostruxure.rate.calculator.be.User;
import ecostruxure.rate.calculator.dal.IUserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Iterable<User> getUsers() {
            return userRepository.findAll();
        }

    public User getById(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Convert to Spring Security's UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Password is already hashed
                .roles("USER") // Add roles or authorities as needed
                .build();
   }

    public User update(User user) {
        return userRepository.save(user);
    }

    public boolean delete(UUID id) {
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
}
