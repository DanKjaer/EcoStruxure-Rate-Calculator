package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.User;
import ecostruxure.rate.calculator.bll.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public Iterable<User> getUsers() throws Exception {
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(User user) throws Exception {
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(User user) throws Exception {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable UUID id) throws Exception {
        return userService.delete(id);
    }
}
