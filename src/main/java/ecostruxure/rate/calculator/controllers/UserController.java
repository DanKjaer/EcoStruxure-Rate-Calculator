package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.dto.UserDTO;
import ecostruxure.rate.calculator.bll.utils.JwtUtil;
import ecostruxure.rate.calculator.be.User;
import ecostruxure.rate.calculator.bll.user.UserService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService,
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager, EntityManager entityManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping()
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/register")
    public User createUser(@RequestBody UserDTO user) {
        return userService.createUser(user.getUsername(), user.getPassword());
    }

    @PutMapping
    public User updateUser(@RequestBody UserDTO user) throws Exception {
        return userService.update(user);
    }

    @DeleteMapping()
    public boolean delete(@RequestParam UUID id) throws Exception {
        return userService.delete(id);
    }

    @PostMapping("/authenticate")
    public Map<String, String> authenticate(@RequestBody User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        final UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // Wrap the JWT in a JSON response
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return response;
    }
}
