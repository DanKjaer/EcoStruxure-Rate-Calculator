package ecostruxure.rate.calculator.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team-profile")
public class TeamProfileController {
//    private final TeamService teamService;
//
//    public TeamProfileController() throws Exception {
//        this.teamService = new TeamService();
//    }
//
//    @GetMapping(produces = "application/json")
//    public TeamDTO getTeamProfile(@RequestParam UUID teamId) {
//        Map<String, Object> teamProfileMap = teamService.getTeamAndProfiles(teamId);
//        if (teamProfileMap == null) {
//            return null;
//        }
//
//        TeamDTO teamDTO = new TeamDTO();
//        teamDTO.setTeam((Team) teamProfileMap.get("team"));
//        teamDTO.setTeamProfiles((List<TeamProfile>) teamProfileMap.get("profiles"));
//        return teamDTO;
//    }
//
//    @PostMapping()
//    public List<TeamProfile> AssignProfileToTeam(@RequestParam UUID teamId, @RequestBody List<TeamProfile> teamProfile) throws Exception {
//        return teamService.assignProfiles(teamId, teamProfile);
//    }
//
//    @DeleteMapping()
//    public boolean removeProfilesFromTeam(@RequestParam UUID teamId, @RequestParam UUID profileId) throws Exception {
//        return teamService.removeProfileFromTeam(teamId, profileId);
//    }
//
//    @PutMapping()
//    public TeamProfile updateHourOrCostAllocation(@RequestParam UUID teamId, @RequestBody TeamProfile teamProfile) throws Exception {
//        return teamService.updateTeamProfile(teamId, teamProfile);
//    }
}
