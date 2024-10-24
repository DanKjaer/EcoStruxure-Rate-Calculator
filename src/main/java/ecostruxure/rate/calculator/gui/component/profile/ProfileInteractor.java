package ecostruxure.rate.calculator.gui.component.profile;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.be.enums.ResourceType;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.HistoryService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.gui.component.modals.addprofile.AddProfileGeographyItemModel;
import ecostruxure.rate.calculator.gui.component.profile.ProfileModel.ProfileTableType;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager.CurrencyType;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  ProfileInteractor is responsible for managing the interaction between the ProfileModel and the ProfileService.
 *  It handles fetching, updating, and saving profile data, as well as managing the state of the ProfileModel.
 */
public class ProfileInteractor {
    private static final BigDecimal INITIAL_VALUE = new BigDecimal("0.00");

    private final ProfileModel model;


    private ProfileService profileService;
    private HistoryService historyService;
    private GeographyService geographyService;
    private Profile profile;
    private List<AddProfileGeographyItemModel> addProfileGeographyItemModels;
    private List<ProfileTeamItemModel> teamItemModels;
    private List<ProfileHistoryItemModel> historyItemModels;
    private BigDecimal contributedHours = INITIAL_VALUE;
    private BigDecimal totalHourlyRate = INITIAL_VALUE;
    private BigDecimal totalDayRate = INITIAL_VALUE;
    private BigDecimal totalAnnualCost = INITIAL_VALUE;
    private ProfileSaveModel originalSaveModel;
    private LocalDateTime currentDateTime = LocalDateTime.now();

    // Constructor initializes services and sets up bindings.
    public ProfileInteractor(ProfileModel model, Runnable onFetchError) {
        this.model = model;

        try {
            profileService = new ProfileService();
            geographyService = new GeographyService();
            historyService = new HistoryService();
        } catch (Exception e) {
            e.printStackTrace();
            onFetchError.run();
        }

        setupBindings();
    }

    // Fetches profile data from ProfileService and updates the ProfileModel.
    public boolean fetchProfile(UUID id) {
        try {
            currentDateTime = LocalDateTime.now();
            contributedHours = INITIAL_VALUE;
            totalHourlyRate = INITIAL_VALUE;
            totalDayRate = INITIAL_VALUE;
            totalAnnualCost = INITIAL_VALUE;
            profile = profileService.get(id);
            historyItemModels = convertToHistoryModels(historyService.getProfileHistory(id));
            historyItemModels.addFirst(convertProfileToHistory(profile));
            addProfileGeographyItemModels = convertToGeographyModels(geographyService.all());
            teamItemModels = convertToTeamModels(profileService.getTeams(profile));
            return true;
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // Updates the ProfileModel with the fetched profile data.
    public void updateModelPostFetchProfile() {
        model.setId(profile.getProfileId());
        model.profileName().set(profile.getName());
        model.saveModel().nameProperty().set(profile.getName());
        model.saveModel().selectedResourceTypeProperty().set(profile.isResourceType() ? ResourceType.OVERHEAD : ResourceType.PRODUCTION);
        model.archivedProperty().set(profile.isArchived());

        model.saveModel().annualSalaryProperty().set(String.valueOf(profile.getAnnualCost()));
        model.saveModel().effectivenessProperty().set(String.valueOf(profile.getEffectivenessPercentage()));
        model.saveModel().effectiveWorkHoursProperty().set(String.valueOf(profile.getEffectiveWorkHours()));
        model.saveModel().annualTotalHoursProperty().set(String.valueOf(profile.getAnnualHours()));
        model.saveModel().hoursPerDayProperty().set(String.valueOf(profile.getHoursPerDay()));

        model.saveModel().locations().setAll(addProfileGeographyItemModels);
        model.teams().setAll(teamItemModels);
        model.history().setAll(historyItemModels);

        if (contributedHours == null) contributedHours = INITIAL_VALUE;
        if (totalHourlyRate == null) totalHourlyRate = INITIAL_VALUE;
        if (totalDayRate == null) totalDayRate = INITIAL_VALUE;
        if (totalAnnualCost == null) totalAnnualCost = INITIAL_VALUE;

        model.contributedHours().set(contributedHours.toString());
        model.setTotalHourlyRate(totalHourlyRate);
        model.setTotalDayRate(totalDayRate);
        model.setTotalAnnualCost(totalAnnualCost);
        model.currentDateProperty().set(currentDateTime);
        updateSelectedGeography();
        originalSaveModel = cloneProfileToSaveModel(model.saveModel());
        setupBindings();
    }

    // Saves the profile data and updates the model.
    public boolean saveProfile() {
        try {
            boolean saved = profileService.update(profile, createProfileFromModel());
            if (saved) fetchProfile(model.getUUID());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updates the model with the selected history data.
    public void changeHistoryModel(ProfileHistoryItemModel historyModel) {
        model.historySelectedProperty().set(!historyModel.updatedAtProperty().get().equals(currentDateTime));

        model.saveModel().selectedResourceTypeProperty().set(historyModel.resourceTypeProperty().get());
        model.saveModel().annualSalaryProperty().set(historyModel.annualSalaryProperty().get().toString());
        model.saveModel().effectivenessProperty().set(historyModel.effectivenessProperty().get().toString());
        model.saveModel().annualTotalHoursProperty().set(historyModel.totalHoursProperty().get().toString());
        model.saveModel().hoursPerDayProperty().set(historyModel.hoursPerDayProperty().get().toString());
    }

    // Updates the model after saving the profile.
    public void updateModelPostSave() {
        updateModelPostFetchProfile();
        var firstHistory = model.history().getFirst();
        model.historySelectedProperty().set(false);
        model.saveModel().selectedResourceTypeProperty().set(firstHistory.resourceTypeProperty().get());

        CurrencyType currentCurrency = CurrencyManager.currencyTypeProperty().get();
        BigDecimal annualSalaryInEUR;

        if (currentCurrency == CurrencyType.USD) {
            BigDecimal conversionRateToEUR = CurrencyManager.conversionRateProperty().get().stripTrailingZeros();

            annualSalaryInEUR = firstHistory.annualSalaryProperty().get().divide(conversionRateToEUR, 4, RoundingMode.HALF_UP);
        } else {
            annualSalaryInEUR = firstHistory.annualSalaryProperty().get();
        }

        model.saveModel().annualSalaryProperty().set(annualSalaryInEUR.toString());
        model.saveModel().effectivenessProperty().set(firstHistory.effectivenessProperty().get().toString());
        model.saveModel().annualTotalHoursProperty().set(firstHistory.totalHoursProperty().get().toString());
        model.saveModel().hoursPerDayProperty().set(firstHistory.hoursPerDayProperty().get().toString());
    }

    // Clears the profileModel.
    public void clearModel() {
        model.profileName().set("");
        model.location().set("");
        model.saveModel().selectedGeographyProperty().set(null);
        model.teams().clear();
        model.contributedHours().set("0.00");
        model.setTotalHourlyRate(INITIAL_VALUE);
        model.setTotalDayRate(INITIAL_VALUE);
        model.setTotalAnnualCost(INITIAL_VALUE);
        model.selectedTableTypeProperty().set(ProfileTableType.TEAM);
        model.selectedHistoryItemProperty().set(null);
        model.historySelectedProperty().set(false);
    }

    // Undoes changes made to the ProfileModel.
    public void undoChanges() {
        if (originalSaveModel == null) return;

        ProfileSaveModel current = model.saveModel();
        current.nameProperty().set(originalSaveModel.nameProperty().get());
        current.selectedGeographyProperty().set(originalSaveModel.selectedGeographyProperty().get());
        current.selectedResourceTypeProperty().set(originalSaveModel.selectedResourceTypeProperty().get());
        current.annualSalaryProperty().set(originalSaveModel.annualSalaryProperty().get());
        current.annualTotalHoursProperty().set(originalSaveModel.annualTotalHoursProperty().get());
        current.effectivenessProperty().set(originalSaveModel.effectivenessProperty().get());
        current.hoursPerDayProperty().set(originalSaveModel.hoursPerDayProperty().get());
    }

    // Updates the selected geograpy in the ProfileModel.
    private void updateSelectedGeography() {
        var geographyId = profile.getCountryId();
        var geographyItem = model.saveModel().locations()
                                             .stream()
                                             .filter(geography -> geography.idProperty().get() == geographyId)
                                             .findFirst()
                                             .orElse(null);

        model.saveModel().selectedGeographyProperty().set(geographyItem);
        if (geographyItem != null) model.location().set(geographyItem.nameProperty().get());
    }


    // Converts the ProfileModel to a Profile object.
    private Profile createProfileFromModel() {
        ProfileSaveModel saveModel = model.saveModel();

        var profile = new Profile();
        profile.setProfileId(model.getUUID());
        profile.setName((saveModel.nameProperty().get()));
        profile.setCurrency(saveModel.currencyProperty().get().name());
        profile.setCountryId(saveModel.selectedGeographyProperty().get().idProperty().get());
        profile.setResourceType(saveModel.selectedResourceTypeProperty().get() == ResourceType.OVERHEAD);
        profile.setArchived(false);

        profile.setAnnualCost(new BigDecimal(saveModel.annualSalaryProperty().get()));
        profile.setEffectivenessPercentage(new BigDecimal(saveModel.effectivenessProperty().get()));
        profile.setAnnualHours(new BigDecimal(saveModel.annualTotalHoursProperty().get()));
        BigDecimal effectiveWorkHours = RateUtils.effectiveWorkHours(profile);
        profile.setEffectiveWorkHours(effectiveWorkHours);
        profile.setHoursPerDay(new BigDecimal(saveModel.hoursPerDayProperty().get()));

        return profile;
    }

    // Clones the ProfileSaveModel.
    private ProfileSaveModel cloneProfileToSaveModel(ProfileSaveModel original) {
        ProfileSaveModel clone = new ProfileSaveModel();

        clone.nameProperty().set(original.nameProperty().get());
        clone.selectedGeographyProperty().set(original.selectedGeographyProperty().get());
        clone.selectedResourceTypeProperty().set(original.selectedResourceTypeProperty().get());
        clone.annualSalaryProperty().set(original.annualSalaryProperty().get());
        clone.annualTotalHoursProperty().set(original.annualTotalHoursProperty().get());
        clone.effectiveWorkHoursProperty().set(original.effectiveWorkHoursProperty().get());
        clone.effectivenessProperty().set(original.effectivenessProperty().get());
        clone.hoursPerDayProperty().set(original.hoursPerDayProperty().get());

        return clone;
    }

    // Converts a profile object to a ProfileHistoryItemModel.
    private ProfileHistoryItemModel convertProfileToHistory(Profile profile) {
        ProfileHistoryItemModel historyModel = new ProfileHistoryItemModel();

        historyModel.setIdProperty(profile.getProfileId());

        if (profile.isResourceType()) historyModel.resourceTypeProperty().set(ResourceType.OVERHEAD);
        else historyModel.resourceTypeProperty().set(ResourceType.PRODUCTION);

        historyModel.setAnnualSalary(profile.getAnnualCost());
        historyModel.effectivenessProperty().set(profile.getEffectivenessPercentage());
        historyModel.totalHoursProperty().set(profile.getAnnualHours());
        historyModel.effectiveWorkHoursProperty().set(profile.getEffectiveWorkHours());
        historyModel.hoursPerDayProperty().set(profile.getHoursPerDay());
        historyModel.updatedAtProperty().set(currentDateTime);
        historyModel.setHourlyRate(profileService.hourlyRate(profile));
        historyModel.setDayRate(profileService.dayRate(profile));
        historyModel.setAnnualCost(profileService.annualCost(profile));

        return historyModel;
    }

    // Converts a list of ProfileHistory objects to a list of ProfileHistoryItemModel objects.
    private List<ProfileHistoryItemModel> convertToHistoryModels(List<ProfileHistory> history) {
        List<ProfileHistoryItemModel> historyModels = new ArrayList<>();

        for (ProfileHistory profile : history) {
            ProfileHistoryItemModel historyModel = new ProfileHistoryItemModel();

            historyModel.setIdProperty(profile.profileId());

            if (profile.resourceType()) historyModel.resourceTypeProperty().set(ResourceType.OVERHEAD);
            else historyModel.resourceTypeProperty().set(ResourceType.PRODUCTION);

            historyModel.setAnnualSalary(profile.annualCost());
            historyModel.effectivenessProperty().set(profile.effectiveness());
            historyModel.totalHoursProperty().set(profile.annualHours());
            historyModel.effectiveWorkHoursProperty().set(profile.effectiveWorkHours());
            historyModel.hoursPerDayProperty().set(profile.hoursPerDay());
            historyModel.updatedAtProperty().set(profile.updatedAt());
            historyModel.setHourlyRate(profileService.hourlyRate(profile));
            historyModel.setDayRate(profileService.dayRate(profile));
            historyModel.setAnnualCost(profileService.annualCost(profile));
            historyModels.add(historyModel);
        }

        return historyModels;
    }

    // Converts a list of Geography objects to a list of AddProfileGeographyItemModel objects.
    private List<AddProfileGeographyItemModel> convertToGeographyModels(List<Geography> geographies) {
        List<AddProfileGeographyItemModel> geographyModels = new ArrayList<>();

        for (Geography geography : geographies) {
            AddProfileGeographyItemModel geographyModel = new AddProfileGeographyItemModel();
            geographyModel.idProperty().set(geography.id());
            geographyModel.nameProperty().set(geography.name());
            geographyModels.add(geographyModel);
        }

        return geographyModels;
    }

    // Converts a list of Team objects to a list of ProfileTeamItemModel objects.
    private List<ProfileTeamItemModel> convertToTeamModels(List<Team> teams) throws Exception {
        List<ProfileTeamItemModel> teamModels = new ArrayList<>();

        for (Team team : teams) {
            if (team.isArchived()) continue;
            ProfileTeamItemModel profileTeamModel = new ProfileTeamItemModel();
            profileTeamModel.teamIdProperty().set(team.getTeamId());
            profileTeamModel.profileIdProperty().set(profile.getProfileId());
            profileTeamModel.nameProperty().set(team.getName());
            BigDecimal costAllocation = profileService.getProfileCostAllocationForTeam(profile.getProfileId(), team.getTeamId());
            BigDecimal hourAllocation = profileService.getProfileHourAllocationForTeam(profile.getProfileId(), team.getTeamId());

            profileTeamModel.costAllocationProperty().set(costAllocation);
            profileTeamModel.hourAllocationProperty().set(hourAllocation);

            BigDecimal hourlyRate = RateUtils.hourlyRate(profile, costAllocation);
            BigDecimal dayRate = RateUtils.dayRate(profile, costAllocation);
            BigDecimal annualCost = RateUtils.annualCost(profile, costAllocation);

            BigDecimal annualTotalHours = RateUtils.utilizedHours(profile, hourAllocation);
            BigDecimal effectiveWorkHours = RateUtils.effectiveWorkHours(profile);

            profileTeamModel.setHourlyRate(hourlyRate);
            profileTeamModel.setDayRate(dayRate);
            profileTeamModel.setAnnualCost(annualCost);
            profileTeamModel.annualTotalHoursProperty().set(annualTotalHours);
            profileTeamModel.effectiveWorkHoursProperty().set(effectiveWorkHours);

            contributedHours = contributedHours.add(annualTotalHours);
            totalHourlyRate = totalHourlyRate.add(hourlyRate);
            totalDayRate = totalDayRate.add(dayRate);
            totalAnnualCost = totalAnnualCost.add(annualCost);
            effectiveWorkHours = effectiveWorkHours.add(effectiveWorkHours);

            teamModels.add(profileTeamModel);
        }

        return teamModels;
    }

    // Data validation & bindings
    private void setupBindings() {
        configureValidationBindings();
        configureSaveBindings();
        configureUndoBindings();
        configureCheckoutBindings();
    }

    // Configures validation bindings for the ProfileModel.
    private void configureValidationBindings() {
        var saveModel = model.saveModel();
        saveModel.nameIsValidProperty().bind(model.saveModel().nameProperty().isNotEmpty());
        saveModel.selectedGeographyIsValidProperty().bind(model.saveModel().selectedGeographyProperty().isNotNull());
        saveModel.annualSalaryIsValidProperty().bind(model.saveModel().annualSalaryProperty().isNotEmpty());
        saveModel.annualTotalHoursIsValidProperty().bind(model.saveModel().annualTotalHoursProperty().isNotEmpty());
        saveModel.effectiveWorkHoursIsValidProperty().bind(model.saveModel().effectiveWorkHoursProperty().isNotEmpty());
        saveModel.effectivenessIsValidProperty().bind(model.saveModel().effectivenessProperty().isNotEmpty());

        model.saveModel().disableFieldsProperty().bind(model.historySelectedProperty());
    }

    // Configures save bindings for the ProfileModel.
    private void configureSaveBindings() {
        model.okToSaveProperty().unbind();
        model.okToSaveProperty().bind(Bindings.createBooleanBinding(
                () -> isDataValid() && hasDataChanged(),
                model.saveModel().nameProperty(),
                model.saveModel().selectedGeographyProperty(),
                model.saveModel().selectedResourceTypeProperty(),
                model.saveModel().annualSalaryProperty(),
                model.saveModel().annualTotalHoursProperty(),
                model.saveModel().effectiveWorkHoursProperty(),
                model.saveModel().effectivenessProperty(),
                model.saveModel().hoursPerDayProperty()
        ));
    }

    // Configures undo bindings for the ProfileModel.
    private void configureUndoBindings() {
        model.okToUndoProperty().unbind();
        model.okToUndoProperty().bind(Bindings.createBooleanBinding(
                this::hasDataChanged,
                model.saveModel().nameProperty(),
                model.saveModel().selectedGeographyProperty(),
                model.saveModel().selectedResourceTypeProperty(),
                model.saveModel().annualSalaryProperty(),
                model.saveModel().annualTotalHoursProperty(),
                model.saveModel().effectiveWorkHoursProperty(),
                model.saveModel().effectivenessProperty(),
                model.saveModel().hoursPerDayProperty())
        );
    }

    // Configures checkout bindings for the ProfileModel.
    private void configureCheckoutBindings() {
        var historyModel = model.selectedHistoryItemProperty();

        model.okToCheckoutProperty().unbind();
        model.okToCheckoutProperty().bind(Bindings.createBooleanBinding(
                () -> doesHistoryDifferFromOriginal(historyModel.get()),
                model.historySelectedProperty(),
                model.saveModel().selectedResourceTypeProperty(),
                model.saveModel().annualSalaryProperty(),
                model.saveModel().effectivenessProperty(),
                model.saveModel().annualTotalHoursProperty(),
                model.saveModel().effectiveWorkHoursProperty(),
                model.saveModel().hoursPerDayProperty())
        );
    }

    // Archives or un-archives the profile
    public boolean archiveProfile(boolean shouldArchive) {
        try {
            Profile profile = profileService.get(model.getUUID());
            return profileService.archive(profile, shouldArchive);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updates the archived status of the profile in the ProfileModel.
    public void updateArchivedProfile(boolean archived) {
        model.archivedProperty().set(archived);
    }

    // Checks if the history data differs from the original data.
    private boolean doesHistoryDifferFromOriginal(ProfileHistoryItemModel historyModel) {
        if (historyModel == null || originalSaveModel == null) return false;

        if (!model.historySelectedProperty().get()) return false;

        return !historyModel.resourceTypeProperty().get().equals(originalSaveModel.selectedResourceTypeProperty().get()) ||
                !historyModel.annualSalaryProperty().get().equals(new BigDecimal(originalSaveModel.annualSalaryProperty().get())) ||
                !historyModel.effectivenessProperty().get().equals(new BigDecimal(originalSaveModel.effectivenessProperty().get())) ||
                !historyModel.totalHoursProperty().get().equals(new BigDecimal(originalSaveModel.annualTotalHoursProperty().get())) ||
                !historyModel.effectiveWorkHoursProperty().get().equals(new BigDecimal(originalSaveModel.effectiveWorkHoursProperty().get())) ||
                !historyModel.hoursPerDayProperty().get().equals(new BigDecimal(originalSaveModel.hoursPerDayProperty().get()));
    }

    // Checks if the data in the ProfileModel is valid.
    private boolean isDataValid() {
        return !model.saveModel().nameProperty().get().isEmpty() &&
                model.saveModel().selectedGeographyProperty().get() != null &&
                !model.saveModel().annualSalaryProperty().get().isEmpty() &&
                !model.saveModel().annualTotalHoursProperty().get().isEmpty() &&
                !model.saveModel().effectiveWorkHoursProperty().get().isEmpty() &&
                !model.saveModel().effectivenessProperty().get().isEmpty() &&
                !model.saveModel().hoursPerDayProperty().get().isEmpty();
    }

    // Checks if the data in the ProfileModel has changed.
    private boolean hasDataChanged() {
        if (originalSaveModel == null) return false;
        return !model.saveModel().nameProperty().get().equals(originalSaveModel.nameProperty().get()) ||
                model.saveModel().selectedGeographyProperty().get() != originalSaveModel.selectedGeographyProperty().get() ||
                model.saveModel().selectedResourceTypeProperty().get() != originalSaveModel.selectedResourceTypeProperty().get() ||
                !model.saveModel().annualSalaryProperty().get().equals(originalSaveModel.annualSalaryProperty().get()) ||
                !model.saveModel().annualTotalHoursProperty().get().equals(originalSaveModel.annualTotalHoursProperty().get()) ||
                !model.saveModel().effectiveWorkHoursProperty().get().equals(originalSaveModel.effectiveWorkHoursProperty().get()) ||
                !model.saveModel().effectivenessProperty().get().equals(originalSaveModel.effectivenessProperty().get()) ||
                !model.saveModel().hoursPerDayProperty().get().equals(originalSaveModel.hoursPerDayProperty().get());
    }
}