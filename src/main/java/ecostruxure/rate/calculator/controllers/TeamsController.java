package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.TeamDTO;
import ecostruxure.rate.calculator.be.dto.TeamProfileDTO;
import ecostruxure.rate.calculator.bll.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {
    private final TeamService teamService;

    @Autowired
    public TeamsController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/all")
    public Iterable<Team> get() throws Exception {
        return teamService.all();
    }

    @GetMapping
    public TeamDTO getById(@RequestParam UUID teamId) throws Exception {
        return teamService.getById(teamId);
    }

    @GetMapping("/profiles")
    public List<TeamProfileDTO> getByProfileId(@RequestParam UUID profileId) throws Exception {
        return teamService.getByProfileId(profileId);
    }

    @PostMapping
    public Team create(@RequestBody Team team) throws Exception {
        System.out.println("Creating team: " + team.getName());
        return teamService.create(team);
    }

    @PutMapping()
    public TeamDTO update(@RequestBody Team team) throws Exception {
        return teamService.update(team);
    }

    @PutMapping("/addProfileToTeams")
    public List<TeamProfileDTO> addProfileToTeams(@RequestBody List<TeamProfile> teamProfiles) throws Exception {
        return teamService.addProfileToTeams(teamProfiles);
    }

    @DeleteMapping()
    public boolean deleteTeam(@RequestParam UUID id) throws Exception {
        return teamService.deleteTeam(id);
    }

    @DeleteMapping("/archive")
    public boolean archiveTeam(@RequestParam UUID id) throws Exception {
        return teamService.archiveTeam(id);
    }

    @DeleteMapping("/teamProfile")
    public boolean deleteTeamProfile(@RequestParam UUID teamProfileId) throws Exception {
        return teamService.deleteTeamProfile(teamProfileId);
    }
}