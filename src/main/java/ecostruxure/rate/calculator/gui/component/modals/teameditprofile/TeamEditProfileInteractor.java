package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.util.UUID;

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

        model.costAllocationIsValidProperty().bind(model.costAllocationProperty().isNotEmpty());
        model.hourAllocationIsValidProperty().bind(model.hourAllocationProperty().isNotEmpty());

        configureSaveBindings();

    }

    public Boolean fetchTeamProfile(UUID teamId, UUID profileId) {
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
                model.costAllocationProperty(), model.costAllocationFetchedProperty(),
                model.hourAllocationProperty(), model.hourAllocationFetchedProperty())
        );
    }

    public void updateModelSave() {
        updateModel();
        configureSaveBindings();
    }

    public void updateModel() {
        model.costAllocationProperty().set(profile.getCostAllocation().toString());
        model.originalCostAllocationProperty().set(profile.getCostAllocation().toString());
        model.profileNameProperty().set(profile.getName());
        model.costAllocationFetchedProperty().set(true);
        model.hourAllocationProperty().set(profile.getHourAllocation().toString());
        model.originalhourAllocationProperty().set(profile.getHourAllocation().toString());
        model.hourAllocationFetchedProperty().set(true);
    }

    private boolean hasDataChanged() {
        return !model.costAllocationProperty().get().equals(model.originalCostAllocationProperty().get()) ||
                !model.hourAllocationProperty().get().equals(model.originalhourAllocationProperty().get());
    }

    private boolean isDataValid() {
        return model.costAllocationProperty().isNotEmpty().get() &&
                model.costAllocationFetchedProperty().get() &&
                model.hourAllocationProperty().isNotEmpty().get() &&
                model.hourAllocationFetchedProperty().get();
    }

    public boolean saveTeamProfile() {
        try {
            profile.setCostAllocation(new BigDecimal(model.costAllocationProperty().get()));
            profile.setHourAllocation(new BigDecimal(model.hourAllocationProperty().get()));

            return teamService.updateProfile(model.getProfileId(), profile);
        } catch (Exception e) {
            return false;
        }
    }
}
