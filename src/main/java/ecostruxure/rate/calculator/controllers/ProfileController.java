package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    public ProfileController() {
    }

    @GetMapping(produces = "application/json")
    public Profile getProfile(@RequestParam UUID id) throws Exception {
        return profileService.getById(id);
    }

    @GetMapping("/all")
    public Iterable<Profile> getProfiles() throws Exception {
        return profileService.all();
    }

    @PostMapping
    public Profile createProfile(@RequestBody ProfileDTO profileDTO) throws Exception {
        return profileService.create(profileDTO.getProfile());
    }

    @PutMapping()
    public Profile updateProfile(@RequestBody Profile profile) throws Exception {
        return profileService.update(profile);
    }

    @DeleteMapping()
    public boolean deleteProfile(@RequestParam UUID id) throws Exception {
        return profileService.delete(id);
    }
}
