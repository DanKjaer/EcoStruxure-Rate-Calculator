package ecostruxure.rate.calculator.gui.component.modals.teammultiplier;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamMultiplierInteractor {
    private final TeamMultiplierModel model;
    private TeamService teamService;
    private Team team;

    public TeamMultiplierInteractor(TeamMultiplierModel model, Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
        } catch (Exception e) {
            e.printStackTrace();
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

    public Boolean fetchMultipliers(UUID teamId) {
        try {
            team = teamService.get(teamId);
            model.teamIdProperty().set(teamId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateModel() {
        model.markupProperty().set(team.getMarkup().toString());
        model.markupFetchedProperty().set(true);
        model.grossMarginProperty().set(team.getGrossMargin().toString());
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
            e.printStackTrace();
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