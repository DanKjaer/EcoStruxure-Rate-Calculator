package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public Team create(@RequestBody Team team) throws Exception {
        return teamService.create(team);
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
