package ecostruxure.rate.calculator.controllers;

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

    @GetMapping("/{teamId}")
    public TeamDTO getTeamProfile(@PathVariable UUID teamId) {
        Map<String, Object> teamProfileMap = teamService.getTeamAndProfiles(teamId);
        if (teamProfileMap == null) {
            return null;
        }

        return new TeamDTO((TeamDTO) teamProfileMap.get("team"), (List<TeamProfile>) teamProfileMap.get("profiles"));
    }

    @PostMapping("/{teamId}")
    public List<TeamProfile> AssignProfileToTeam(@PathVariable UUID teamId, @RequestBody List<TeamProfile> teamProfile) throws Exception {
        return teamService.assignProfiles(teamId, teamProfile);
    }

    @DeleteMapping("?teamId={teamId}&profileId={profileId}")
    public boolean removeProfilesFromTeam(@PathVariable UUID teamId, @PathVariable UUID profileId) throws Exception {
        return teamService.removeProfileFromTeam(teamId, profileId);
    }
    
    @PutMapping("/{teamId}")
    public TeamProfile updateOrCostAllocation(@PathVariable UUID teamId, @RequestBody TeamProfile teamProfile) throws Exception {
        return teamService.updateTeamProfile(teamId, teamProfile);
    }
}
