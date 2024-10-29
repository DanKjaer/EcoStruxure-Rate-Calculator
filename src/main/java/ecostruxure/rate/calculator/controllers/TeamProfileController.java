package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.be.dto.TeamDTO;
import ecostruxure.rate.calculator.bll.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/team-profile")
public class TeamProfileController {
    private final TeamService teamService;

    public TeamProfileController() throws Exception {
        this.teamService = new TeamService();
    }

    @GetMapping(produces = "application/json")
    public TeamDTO getTeamProfile(@RequestParam UUID teamId) {
        Map<String, Object> teamProfileMap = teamService.getTeamAndProfiles(teamId);
        if (teamProfileMap == null) {
            return null;
        }

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeam((Team) teamProfileMap.get("team"));
        teamDTO.setProfiles((List<TeamProfile>) teamProfileMap.get("profiles"));
        return teamDTO;
    }

    @PostMapping()
    public List<TeamProfile> AssignProfileToTeam(@RequestParam UUID teamId, @RequestBody List<TeamProfile> teamProfile) throws Exception {
        return teamService.assignProfiles(teamId, teamProfile);
    }

    @DeleteMapping()
    public boolean removeProfilesFromTeam(@RequestParam UUID teamId, @RequestParam UUID profileId) throws Exception {
        return teamService.removeProfileFromTeam(teamId, profileId);
    }

    @PutMapping()
    public TeamProfile updateHourOrCostAllocation(@RequestParam UUID teamId, @RequestBody TeamProfile teamProfile) throws Exception {
        return teamService.updateTeamProfile(teamId, teamProfile);
    }
}
