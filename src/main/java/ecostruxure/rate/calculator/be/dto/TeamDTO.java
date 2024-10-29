package ecostruxure.rate.calculator.be.dto;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;

import java.util.List;

public class TeamDTO {
    private Team team;
    private List<TeamProfile> profiles;

    public TeamDTO(Team team, List<TeamProfile> profiles) {
    }

    public TeamDTO() {

    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<TeamProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<TeamProfile> profiles) {
        this.profiles = profiles;
    }
}
