package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.TeamDTO;
import ecostruxure.rate.calculator.bll.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {
    private final TeamService teamService;

    public TeamsController() throws Exception{
        this.teamService = new TeamService();
    }

    @GetMapping()
    public Iterable<Team> get() throws Exception {
        return teamService.all();
    }

    @GetMapping("/profiles")
    public List<TeamProfile> getByProfileId(@RequestParam UUID profileId) throws Exception {
        return teamService.getByProfileId(profileId);
    }

    @PostMapping
    public Team create(@RequestBody TeamDTO teamDTO) throws Exception {
        Team team = teamDTO.getTeam();
        List<TeamProfile> teamProfiles = teamDTO.getTeamProfiles();

        return teamService.create(team, teamProfiles);
    }

    @PutMapping()
    public Team update(@RequestBody Team team) throws Exception {
        return teamService.update(team);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable UUID id) throws Exception {
        return teamService.delete(id);
    }
}
