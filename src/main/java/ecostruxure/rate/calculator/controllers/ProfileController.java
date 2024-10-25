package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController() throws Exception {
        this.profileService = new ProfileService();
    }

    @GetMapping("/{id}")
    public Profile getProfile(@PathVariable UUID id) throws Exception {
        return profileService.get(id);
    }
}
