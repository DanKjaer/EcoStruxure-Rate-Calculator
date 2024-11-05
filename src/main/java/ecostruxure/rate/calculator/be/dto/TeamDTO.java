package ecostruxure.rate.calculator.be.dto;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;

import java.util.List;

public class TeamDTO {
    private Team team;
    private List<TeamProfile> teamProfiles;

    public TeamDTO(Team team, List<TeamProfile> teamProfiles) {
    }

    public TeamDTO() {

    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<TeamProfile> getTeamProfiles() {
        return teamProfiles;
    }

    public void setTeamProfiles(List<TeamProfile> teamProfiles) {
        this.teamProfiles = teamProfiles;
    }
}
