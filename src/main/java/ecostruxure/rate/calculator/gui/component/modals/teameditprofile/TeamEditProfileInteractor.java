package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;

public class TeamEditProfileInteractor {
    private final TeamEditProfileModel model;
    private TeamService teamService;
    private Profile profile;

    public TeamEditProfileInteractor(TeamEditProfileModel model, Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
        } catch (Exception e) {
            onFetchError.run();
        }

        model.utilizationRateIsValidProperty().bind(model.utilizationRateProperty().isNotEmpty());
        model.utilizationHoursIsValidProperty().bind(model.utilizationHoursProperty().isNotEmpty());

        configureSaveBindings();

    }

    public Boolean fetchTeamProfile(int teamId, int profileId) {
        try {
            profile = teamService.getTeamProfile(teamId, profileId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void configureSaveBindings() {
        model.okToSaveProperty().unbind();
        model.okToSaveProperty().bind(Bindings.createBooleanBinding(
                () -> isDataValid() && hasDataChanged(),
                model.utilizationRateProperty(), model.utilizationRateFetchedProperty(),
                model.utilizationHoursProperty(), model.utilizationHoursFetchedProperty())
        );
    }

    public void updateModelSave() {
        updateModel();
        configureSaveBindings();
    }

    public void updateModel() {
        model.utilizationRateProperty().set(profile.utilizationRate().toString());
        model.originalUtilizationRateProperty().set(profile.utilizationRate().toString());
        model.profileNameProperty().set(profile.profileData().name());
        model.utilizationRateFetchedProperty().set(true);
        model.utilizationHoursProperty().set(profile.utilizationHours().toString());
        model.originalUtilizationHoursProperty().set(profile.utilizationHours().toString());
        model.utilizationHoursFetchedProperty().set(true);
    }

    private boolean hasDataChanged() {
        return !model.utilizationRateProperty().get().equals(model.originalUtilizationRateProperty().get()) ||
                !model.utilizationHoursProperty().get().equals(model.originalUtilizationHoursProperty().get());
    }

    private boolean isDataValid() {
        return model.utilizationRateProperty().isNotEmpty().get() &&
                model.utilizationRateFetchedProperty().get() &&
                model.utilizationHoursProperty().isNotEmpty().get() &&
                model.utilizationHoursFetchedProperty().get();
    }

    public boolean saveTeamProfile() {
        try {
            profile.utilizationRate(new BigDecimal(model.utilizationRateProperty().get()));
            profile.utilizationHours(new BigDecimal(model.utilizationHoursProperty().get()));

            return teamService.updateProfile(model.teamIdProperty().get(), profile);
        } catch (Exception e) {
            return false;
        }
    }
}
