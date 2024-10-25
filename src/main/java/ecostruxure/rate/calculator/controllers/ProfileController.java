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

    @PostMapping
    public Profile createProfile(@RequestBody Profile profile) throws Exception {
        return profileService.create(profile);
    }

    @PutMapping("/{id}")
    public boolean updateProfile(@PathVariable UUID id, @RequestBody Profile profile) throws Exception {
        return profileService.update(id, profile);
    }

    @DeleteMapping("/{id}")
    public boolean deleteProfile(@PathVariable UUID id) throws Exception {
        return profileService.archive(id, true);
    }
}
