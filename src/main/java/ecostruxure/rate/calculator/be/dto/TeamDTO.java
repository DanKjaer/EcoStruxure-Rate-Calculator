package ecostruxure.rate.calculator.be.dto;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;

import java.util.List;

public class TeamDTO {
    private Team team;
    private List<TeamProfile> profiles;

    public TeamDTO(TeamDTO team, List<TeamProfile> profiles) {
    }
}
