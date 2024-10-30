package ecostruxure.rate.calculator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.TeamDTO;
import ecostruxure.rate.calculator.bll.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {
    private final TeamService teamService;

    public TeamsController() throws Exception{
        this.teamService = new TeamService();
    }

    @GetMapping("/{id}")
    public Team get(@PathVariable UUID id) throws Exception {
        return teamService.get(id);
    }

    @PostMapping
    public Team create(@RequestBody TeamDTO teamDTO) throws Exception {
        Team team = teamDTO.getTeam();
        List<TeamProfile> teamProfiles = teamDTO.getProfiles();

        return teamService.create(team, teamProfiles);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable UUID id, @RequestBody Team team) throws Exception {
        return teamService.update(id, team);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable UUID id) throws Exception {
        return teamService.archive(id, true);
    }
}
