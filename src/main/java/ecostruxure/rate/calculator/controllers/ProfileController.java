package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.bll.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(produces = "application/json")
    public ProfileDTO getProfile(@RequestParam UUID id) throws Exception {
        return profileService.getById(id);
    }

    @GetMapping("/all")
    public Iterable<ProfileDTO> getProfiles() throws Exception {
        return profileService.all();
    }

    @PostMapping
    public Profile createProfile(@RequestBody ProfileDTO profileDTO) throws Exception {
        return profileService.create(profileDTO);
    }

    @PutMapping()
    public Profile updateProfile(@RequestBody Profile profile) throws Exception {
        return profileService.update(profile);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable UUID id) throws Exception {
        return profileService.delete(id);
    }

    @DeleteMapping("/archive")
    public boolean archive(@RequestParam UUID id) throws Exception {
        return profileService.archive(id);
    }
}
