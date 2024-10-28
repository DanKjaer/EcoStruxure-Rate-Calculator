/*
package ecostruxure.rate.calculator.bll;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.TeamService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {
    TeamService teamService;
    public TeamServiceTest() {
        try {
            teamService = new TeamService();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createTeamIsNull() {
        assertThrows(NullPointerException.class, () -> teamService.create(null));
    }

    @Test
    void createTeamNameIsNull() {
        assertThrows(NullPointerException.class, () -> teamService.create(new Team(null, new BigDecimal("0.0"), new BigDecimal("0.0"))));
    }

    @Test
    void createTeamNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> teamService.create(new Team("", new BigDecimal("0.0"), new BigDecimal("0.0"))));
    }

    @Test
    void it_CreateTeam() {
        Team team = new Team("Team 1", new BigDecimal("0.0"), new BigDecimal("0.0"));
        try {
            Team createdTeam = teamService.create(team);
            assertThat(createdTeam).isNotNull();
            assertThat(createdTeam.id()).isGreaterThan(0);
            assertThat(createdTeam.name()).isEqualTo(team.name());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void setMarkupTeamIsNull() {
        assertThrows(NullPointerException.class, () -> teamService.setMarkup(null, new BigDecimal("10.0")));
    }

    @Test
    void setMarkupMarkupIsNull() {
        assertThrows(NullPointerException.class, () -> teamService.setMarkup(new Team("Hej", new BigDecimal("0.0"), new BigDecimal("0.0")), null));
    }

    @Test
    void setMarkupIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> teamService.setMarkup(new Team("Hej", new BigDecimal("0.0"), new BigDecimal("0.0")), new BigDecimal("-1.0")));
    }

    @Test
    void setMarkupIsGreaterThanHundred() {
        assertThrows(IllegalArgumentException.class, () -> teamService.setMarkup(new Team("Hej", new BigDecimal("0.0"), new BigDecimal("0.0")), new BigDecimal("100.1")));
    }

    @Test
    void it_setMarkup() {
        Team team = new Team("Team 2 - set markup test", new BigDecimal("0.0"), new BigDecimal("0.0"));
        try {
            team = teamService.create(team);
        } catch (Exception e) {
            fail(e);
        }

        try {
            teamService.setMarkup(team, BigDecimal.valueOf(10.0));
            assertThat(teamService.get(team.id()).markup()).isEqualTo(new BigDecimal("10.00")); // scale 2 i db
        } catch (Exception e) {
            fail(e);
        }
    }
}*/
