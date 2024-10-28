/*
package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.dal.db.TeamDAO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TeamDAOTest {
    private ITeamDAO teamDAO;

    public TeamDAOTest() {
        try {
            teamDAO = new TeamDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void it_updateMarkup() {
        // Setup
        var team = new Team("Test team - update markup", new BigDecimal("0.0"), new BigDecimal("0.0"));
        try {
            team = teamDAO.create(team);
        } catch (Exception e) {
            fail(e);
        }

        var teamWithMarkup = new Team(team.id(), team.name(), new BigDecimal("20.5"), new BigDecimal("0.0"), false);

        // Call
        try {
            teamDAO.updateMultipliers(teamWithMarkup);
        } catch (Exception e) {
            fail(e);
        }

        // Check
        try {
            assertThat(teamDAO.get(team.id()).markup()).isEqualTo(new BigDecimal("20.50")); // gemmer som scale 2 i db
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void it_updateGrossMargin() {
        // Setup
        var team = new Team("Test team - update gross margin", new BigDecimal("0.0"), new BigDecimal("0.0"));
        try {
            team = teamDAO.create(team);
        } catch (Exception e) {
            fail(e);
        }

        var teamWithGrossMargin = new Team(team.id(), team.name(), new BigDecimal("0.0"), new BigDecimal("20.5"), false);

        // Call
        try {
            teamDAO.updateMultipliers(teamWithGrossMargin);
        } catch (Exception e) {
            fail(e);
        }

        // Check
        try {
            assertThat(teamDAO.get(team.id()).grossMargin()).isEqualTo(new BigDecimal("20.50")); // gemmer som scale 2 i db
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}*/
