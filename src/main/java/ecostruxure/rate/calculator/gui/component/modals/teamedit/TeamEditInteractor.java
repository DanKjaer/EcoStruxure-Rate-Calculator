package ecostruxure.rate.calculator.gui.component.modals.teamedit;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;

import java.util.UUID;

public class TeamEditInteractor {
    private final TeamEditModel model;
    private TeamService teamService;
    private Team team;

    public TeamEditInteractor(TeamEditModel model, Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
        } catch (Exception e) {
            onFetchError.run();
        }

        model.newNameValidProperty().bind(model.newNameProperty().isNotEmpty());
        configureSaveBindings();
    }

    public Boolean fetchTeam(UUID teamId) {
        try {
            team = teamService.get(teamId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveTeam() {
        try {
            team.setName(model.newNameProperty().get());
            teamService.update(team);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void configureSaveBindings() {
        model.okToSaveProperty().unbind();
        model.okToSaveProperty().bind(Bindings.createBooleanBinding(
                this::isDataValid,
                model.newNameProperty(),
                model.teamFetchedProperty()
        ));
    }

    private boolean isDataValid() {
        String newName = model.newNameProperty().get();
        return model.newNameProperty().isNotEmpty().get() &&
                !newName.trim().isEmpty() &&
                !Character.isWhitespace(newName.charAt(0)) &&
                !model.teamNameProperty().get().equals(newName) &&
                model.teamFetchedProperty().get();
    }

    public void updateModelSave() {
        updateModel();
        configureSaveBindings();
    }

    public void updateModel() {
        model.teamNameProperty().set(team.getName());
        model.newNameProperty().set(team.getName());
        model.teamFetchedProperty().set(true);
    }
}
