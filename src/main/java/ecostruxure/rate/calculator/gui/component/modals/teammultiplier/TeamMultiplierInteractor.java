package ecostruxure.rate.calculator.gui.component.modals.teammultiplier;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;

public class TeamMultiplierInteractor {
    private final TeamMultiplierModel model;
    private TeamService teamService;
    private Team team;

    public TeamMultiplierInteractor(TeamMultiplierModel model, Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
        } catch (Exception e) {
            onFetchError.run();
        }

        model.markupIsValidProperty().bind(model.markupProperty().isNotEmpty());
        model.grossMarginIsValidProperty().bind(model.grossMarginProperty().isNotEmpty());

        model.okToSaveProperty().bind(Bindings.createBooleanBinding(
                this::isDataValid,
                model.markupProperty(), model.markupFetchedProperty(),
                model.grossMarginProperty(), model.grossMarginFetchedProperty())
        );
    }

    public Boolean fetchMultipliers(int teamId) {
        try {
            team = teamService.get(teamId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateModel() {
        model.markupProperty().set(team.markup().toString());
        model.markupFetchedProperty().set(true);
        model.grossMarginProperty().set(team.grossMargin().toString());
        model.grossMarginFetchedProperty().set(true);
    }

    public boolean saveMultipliers() {
        try {
            teamService.setMultipliers(
                    model.teamIdProperty().get(),
                    new BigDecimal(model.markupProperty().get()),
                    new BigDecimal(model.grossMarginProperty().get())
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDataValid() {
        return model.markupProperty().isNotEmpty().get() &&
                model.markupFetchedProperty().get() &&
                model.grossMarginProperty().isNotEmpty().get() &&
                model.grossMarginFetchedProperty().get();
    }
}