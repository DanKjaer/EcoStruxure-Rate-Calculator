package ecostruxure.rate.calculator.gui.component.profiles;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.ProfileData;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.bll.service.TeamService;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfilesInteractor {
    private final ProfilesModel model;
    private ProfileService profileService;
    private GeographyService geographyService;
    private TeamService teamService;
    private List<ProfileItemModel> profileItemModels;

    private Profile profile;

    public ProfilesInteractor(ProfilesModel model, Runnable onFetchError) {
        this.model = model;

        try {
            profileService = new ProfileService();
            geographyService = new GeographyService();
            teamService = new TeamService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean fetchProfiles() {
        try {
            List<Profile> profiles = profileService.all();
            profileItemModels = convertToProfileItemModels(profiles);
            return true;
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    private List<ProfileItemModel> convertToProfileItemModels(List<Profile> profiles) throws Exception {
        List<ProfileItemModel> profileItemModels = new ArrayList<>();

        for (Profile profile : profiles) {
            ProfileItemModel profileItemModel = new ProfileItemModel();
            profileItemModel.idProperty().set(profile.id());
            profileItemModel.nameProperty().set(profile.profileData().name());

            BigDecimal costAllocation = profileService.getTotalCostAllocation(profile.id());
            BigDecimal hourAllocation = profileService.getTotalHourAllocation(profile.id());

            profileItemModel.hourAllocationProperty().set(hourAllocation);
            profileItemModel.hoursProperty().set(RateUtils.utilizedHours(profile, hourAllocation));

            profileItemModel.costAllocationProperty().set(costAllocation);
            profileItemModel.setHourlyRate(RateUtils.hourlyRate(profile, costAllocation));
            profileItemModel.setDayRate(RateUtils.dayRate(profile, costAllocation));
            profileItemModel.setAnnualCost(RateUtils.annualCost(profile, costAllocation));
            profileItemModel.setEffectiveWorkHours(RateUtils.effectiveWorkHours(profile));

            profileItemModel.teamsProperty().set(String.valueOf(profileService.getTeams(profile).size()));
            profileItemModel.locationProperty().set(geographyService.get(profile.profileData().geography()).name());
            profileItemModel.archivedProperty().set(profile.profileData().archived());


            profileItemModels.add(profileItemModel);
        }

        return profileItemModels;
    }



    public void updateModel() {
        model.profiles().setAll(profileItemModels);

        BigDecimal totalHourly = BigDecimal.ZERO;
        BigDecimal totalDaily = BigDecimal.ZERO;
        Set<String> uniqueGeographies = new HashSet<>();

        for (ProfileItemModel profileItemModel : model.profiles()) {
            totalHourly = totalHourly.add(profileItemModel.hourlyRateProperty().get());
            totalDaily = totalDaily.add(profileItemModel.dayRateProperty().get());
            uniqueGeographies.add(profileItemModel.locationProperty().get());
        }

        model.setTotalHourlyRate(totalHourly);
        model.setTotalDayRate(totalDaily);
        model.numProfiles().set(String.valueOf(model.profiles().size()));
        model.uniqueGeographies().set(String.valueOf(uniqueGeographies.size()));
    }

    private List<Profile> convertModelsToEntity(ObservableList<ProfileItemModel> profileItemModels, boolean checkArchived) {
        List<Profile> profiles = new ArrayList<>();
        for (ProfileItemModel profileItemModel : profileItemModels) {
            if (checkArchived && profileItemModel.archivedProperty().get())
                continue;

            Profile profile = new Profile();
            ProfileData profileData = new ProfileData();
            profile.id(profileItemModel.idProperty().get());
            profileData.name(profileItemModel.nameProperty().get());

            profile.profileData(profileData);

            profiles.add(profile);
        }
        return profiles;

    }

    public boolean archiveProfile(ProfileItemModel profileItemModel, boolean shouldArchive) {
        try {
            Profile profile = profileService.get(profileItemModel.idProperty().get());
            return profileService.archive(profile, shouldArchive);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean archiveProfiles() {
        try {
            List<Profile> profiles = convertModelsToEntity(model.profilesToArchive(), true);
            return profileService.archive(profiles);
        } catch (Exception e) {
            return false;
        }
    }

    public void updateArchivedProfiles() {
        for (ProfileItemModel profileItemModel : model.profilesToArchive())
            profileItemModel.archivedProperty().set(true);
    }

    public void updateArchivedProfile(boolean archived) {
        model.profileToArchive().get().archivedProperty().set(archived);
    }

}