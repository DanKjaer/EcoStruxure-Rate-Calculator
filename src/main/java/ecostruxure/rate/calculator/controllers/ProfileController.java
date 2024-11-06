package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController() throws Exception {
        this.profileService = new ProfileService();
    }

    @GetMapping(produces = "application/json")
    public Profile getProfile(@RequestParam UUID id) throws Exception {
        return profileService.get(id);
    }

    @GetMapping()
    public List<Profile> getProfiles() throws Exception {
        return profileService.all();
    }

    @PostMapping
    public Profile createProfile(@RequestBody ProfileDTO profileDTO) throws Exception {

        System.out.println("Create profile: " + profileDTO.getProfile().getAnnualCost());
        return profileService.create(profileDTO.getProfile());
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
