package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamsInteractorTest {

    @Test
    void it_convertToTeamItemModels2() throws Exception {
        // Setup
        TeamsInteractor teamsInteractor = new TeamsInteractor(new TeamsModel(), () -> {});

        Team team1 = new Team(1, "North American Operations Center", new BigDecimal("10.00"), new BigDecimal("20.00"), false);
        Team team2 = new Team(2, "EMEA Market Growth Team", new BigDecimal("25.00"), new BigDecimal("40.00"), false);
        Team team3 = new Team(3, "APAC Product Innovation Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Team> teams = List.of(team1, team2, team3);

        List<Profile> profilesForTeam1 = new ProfileService().allByTeam(team1);
        List<Profile> profilesForTeam2 = new ProfileService().allByTeam(team2);
        List<Profile> profilesForTeam3 = new ProfileService().allByTeam(team3);

        // Call

        // Check
        assertThrows(Exception.class, () -> teamsInteractor.convertToTeamItemModels(null));
    }
}