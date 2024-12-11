package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.User;
import ecostruxure.rate.calculator.bll.user.UserService;
import ecostruxure.rate.calculator.bll.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService,
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping()
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user.getUsername(), user.getPassword());
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        final UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return jwt;
    }
}
