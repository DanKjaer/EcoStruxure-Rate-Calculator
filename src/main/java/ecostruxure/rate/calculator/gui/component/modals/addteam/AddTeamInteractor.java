package ecostruxure.rate.calculator.gui.component.modals.addteam;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.ProfileData;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddTeamInteractor {
    private final AddTeamModel model;
    private TeamService teamService;

    private ProfileService profileService;
    private GeographyService geographyService;
    private List<AddProfileItemModel> profileItemModels;

    public AddTeamInteractor(AddTeamModel model, Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
            profileService = new ProfileService();
            geographyService = new GeographyService();
        } catch (Exception e) {
            onFetchError.run();
        }

        StringBinding nameValidityBinding = Bindings.createStringBinding(() -> {
            String name = model.nameProperty().get();
            return name != null && !name.trim().isEmpty() ? "" : "Invalid Name";
        }, model.nameProperty());

        model.nameIsValidProperty().bind(nameValidityBinding.isEmpty());
        model.okToAddProperty().bind(Bindings.createBooleanBinding(
                this::isDataValid,
                model.nameProperty()
        ));
    }

    private boolean isDataValid() {
        String name = model.nameProperty().get();
        return name != null && !name.trim().isEmpty() && model.profilesFetchedProperty().get();
    }

    public Boolean fetchProfiles() {
        try {
            List<Profile> profiles = profileService.allWithUtilization();
            profileItemModels = convertToProfileItemModels(profiles);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<AddProfileItemModel> convertToProfileItemModels(List<Profile> profiles) throws Exception {
        List<AddProfileItemModel> profileItemModels = new ArrayList<>();

        for (Profile profile : profiles) {
            if (!profileService.shouldProcessUtilization(profile.costAllocation()))
                continue;

            AddProfileItemModel profileItemModel = new AddProfileItemModel();
            profileItemModel.idProperty().set(profile.id());
            profileItemModel.nameProperty().set(profile.profileData().name());

            profileItemModel.currentCostAllocationProperty().set(new BigDecimal(100).subtract(profile.costAllocation()));
            profileItemModel.setCostAllocationProperty().set(BigDecimal.ZERO);
            profileItemModel.currentHourAllocationProperty().set(new BigDecimal(100).subtract(profile.hourAllocation()));
            profileItemModel.setHourAllocationProperty().set(BigDecimal.ZERO);

            profileItemModel.locationProperty().set(geographyService.get(profile.profileData().geography()).name());

            profileItemModels.add(profileItemModel);
        }

        return profileItemModels;
    }

    public void updateModel() {
        model.profiles().setAll(profileItemModels);
        model.nameProperty().set("");
        model.profilesFetchedProperty().set(true);
    }

    private Profile createProfileFromModel(AddProfileItemModel model) {
        var profileData = new ProfileData();
        profileData.id(model.idProperty().get());
        profileData.name((model.nameProperty().get()));

        var profile = new Profile();
        profile.id(model.idProperty().get());
        profile.profileData(profileData);
        profile.costAllocation(model.setCostAllocationProperty().get());
        profile.hourAllocation(model.setHourAllocationProperty().get());

        return profile;
    }

    public Boolean addTeam() {
        try {
            Team tempTeam = new Team(model.nameProperty().get(), BigDecimal.ZERO, BigDecimal.ZERO);
            List<Profile> profiles = model.profiles().stream()
                    .filter(addProfileItemModel -> addProfileItemModel.selectedProperty().get())
                    .map(this::createProfileFromModel)
                    .collect(Collectors.toList());

            teamService.create(tempTeam, profiles);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
