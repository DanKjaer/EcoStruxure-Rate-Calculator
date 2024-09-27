package ecostruxure.rate.calculator.gui.component.profiles;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfilesInteractor {
    private final ProfilesModel model;
    private ProfileService profileService;
    private List<ProfileItemModel> profileItemModels;
    private UUID profileId;
    private BigDecimal costAllocation;
    private BigDecimal hourAllocation;

    private Profile profile;

    public ProfilesInteractor(ProfilesModel model, Runnable onFetchError) {
        this.model = model;

        try {
            profileService = new ProfileService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean fetchProfiles() {
        try {
            List<Profile> profiles = profileService.all();
            profileItemModels = convertToProfileItemModel(profiles);
            return true;
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    private List<ProfileItemModel> convertToProfileItemModel(List<Profile> profiles) throws Exception {
        List<ProfileItemModel> profileItemModels = new ArrayList<>();

        for (Profile profile : profiles) {
            ProfileItemModel profileItemModel = new ProfileItemModel();
            profileItemModel.UUIDProperty().set(profile.getProfileId());
            profileItemModel.nameProperty().set(profile.getName());
            profileItemModel.currencyProperty().set(profile.getCurrency());
            profileItemModel.countryIdProperty().set(profile.getCountryId());
            profileItemModel.resourceTypeProperty().set(profile.isResourceType());
            profileItemModel.setAnnualCost(profile.getAnnualCost());
            profileItemModel.annualHoursProperty().set(profile.getAnnualHours());
            profileItemModel.effectivenessProperty().set(profile.getEffectivenessPercentage());
            profileItemModel.setEffectiveWorkHours(profile.getEffectiveWorkHours());
            profileItemModel.hoursPerDayProperty().set(profile.getHoursPerDay());
            profileItemModel.updatedAtProperty().set(profile.getUpdatedAt());
            profileItemModel.archivedProperty().set(profile.isArchived());

            BigDecimal costAllocation = profileService.getTotalCostAllocation(profile.getProfileId());
            BigDecimal hourAllocation = profileService.getTotalHourAllocation(profile.getProfileId());
            profileItemModel.allocatedCostProperty().set(costAllocation);
            profileItemModel.allocatedHoursProperty().set(hourAllocation);

            profileItemModels.add(profileItemModel);
        }

        return profileItemModels;
    }


    public void updateModel(){
        model.profiles().setAll(profileItemModels);
    }
    /**
     * Unødvendig metode. Eftersom totalHourly og totalDaily rate skal udregnes på teams fremfor profile?


    public void updateModel() {
        model.profiles().setAll(profileList);

        BigDecimal totalHourly = BigDecimal.ZERO;
        BigDecimal totalDaily = BigDecimal.ZERO;
        Set<String> uniqueGeographies = new HashSet<>();

        for (Profile profile : model.profiles()) {
            totalHourly = totalHourly.add(profileItemModel.hourlyRateProperty().get());
            totalDaily = totalDaily.add(profileItemModel.dayRateProperty().get());
            uniqueGeographies.add(profileItemModel.locationProperty().get());
        }

        model.setTotalHourlyRate(totalHourly);
        model.setTotalDayRate(totalDaily);
        model.numProfiles().set(String.valueOf(model.profiles().size()));
        model.uniqueGeographies().set(String.valueOf(uniqueGeographies.size()));
    } */

    private List<Profile> convertModelsToEntity(ObservableList<ProfileItemModel> profile, boolean checkArchived) {
        List<Profile> profileList = new ArrayList<>();
        for (ProfileItemModel profileItemModel : profile) {
            if (checkArchived && profileItemModel.isArchived())
                continue;

            Profile profiles = new Profile();
            profiles.setProfileId(profiles.getProfileId());
            profiles.setName(profiles.getName());

            profileList.add(profiles);
        }
        return profileList;

    }

    public boolean archiveProfile(ProfileItemModel profileItemModel, boolean shouldArchive) {
        try {
            UUID profileUUID = profileItemModel.UUIDProperty().get();
            Profile profile = profileService.get(profileUUID);
            return profileService.archive(profile, shouldArchive);
        } catch (Exception e) {
            e.printStackTrace();
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

    public boolean updateAllocation() throws SQLException {
        profileService.updateAllocation(profileId, costAllocation, hourAllocation);
        return true;
    }

    public void setAllocationData(UUID profileId, BigDecimal costAllocation, BigDecimal hourAllocation) {
        this.profileId = profileId;
        this.costAllocation = costAllocation;
        this.hourAllocation = hourAllocation;
    }

}