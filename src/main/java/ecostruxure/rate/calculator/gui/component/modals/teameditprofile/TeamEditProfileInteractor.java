package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.service.TeamService;
import ecostruxure.rate.calculator.gui.component.profile.ProfileTeamItemModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamEditProfileInteractor {
    private final TeamEditProfileModel model;
    private TeamService teamService;
    private Profile profile;
    private final TeamProfile teamProfile;

    public TeamEditProfileInteractor(TeamEditProfileModel model, Runnable onFetchError) {
        this.model = model;
        this.teamProfile = new TeamProfile();

        try {
            teamService = new TeamService();
        } catch (Exception e) {
            e.printStackTrace();
            onFetchError.run();
        }

        model.costAllocationIsValidProperty().bind(model.costAllocationProperty().isNotEmpty());
        model.hourAllocationIsValidProperty().bind(model.hourAllocationProperty().isNotEmpty());

        configureSaveBindings();

    }

    public Boolean fetchTeamProfile(UUID teamId, UUID profileId) {
        try {
            profile = teamService.getTeamProfile(teamId, profileId);
            teamProfile.setProfile(profile);
            teamProfile.setTeamId(teamId);
            Platform.runLater(() -> {
                model.profileNameProperty().set(profile.getName());
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
        if(teamProfile != null){
        model.costAllocationProperty().set(teamProfile.getCostAllocation() != null? teamProfile.getCostAllocation().toString() : "");
        model.originalCostAllocationProperty().set(teamProfile.getCostAllocation() != null? teamProfile.getCostAllocation().toString() : "");
        model.hourAllocationProperty().set(teamProfile.getHourAllocation() != null? teamProfile.getHourAllocation().toString() : "");
        model.originalhourAllocationProperty().set(teamProfile.getHourAllocation() != null? teamProfile.getHourAllocation().toString(): "");
        model.profileNameProperty().set(profile.getName());
        model.costAllocationFetchedProperty().set(true);
        model.hourAllocationFetchedProperty().set(true);
        //model.costAllocationProperty().set(teamProfile.getCostAllocation().toString());
        //model.originalCostAllocationProperty().set(teamProfile.getCostAllocation().toString());
        //model.hourAllocationProperty().set(teamProfile.getHourAllocation().toString());
        //model.originalhourAllocationProperty().set(teamProfile.getHourAllocation().toString());
    }}

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
            UUID teamId = model.teamIdProperty().get();
            teamProfile.setCostAllocation(new BigDecimal(model.costAllocationProperty().get()));
            teamProfile.setHourAllocation(new BigDecimal(model.hourAllocationProperty().get()));
            return teamService.updateProfile(teamId, profile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
