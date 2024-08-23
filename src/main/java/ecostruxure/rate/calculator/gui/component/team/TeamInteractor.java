package ecostruxure.rate.calculator.gui.component.team;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamHistory;
import ecostruxure.rate.calculator.bll.RateService;
import ecostruxure.rate.calculator.be.enums.RateType;
import ecostruxure.rate.calculator.be.data.Rates;
import ecostruxure.rate.calculator.gui.util.ExportToExcel;
import ecostruxure.rate.calculator.be.TeamProfileHistory;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.HistoryService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.bll.service.TeamService;
import ecostruxure.rate.calculator.gui.component.geography.IGeographyItemModel;
import ecostruxure.rate.calculator.gui.component.geography.geograph.GeographyItemModel;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamInteractor {
    private final TeamModel model;

    private TeamService teamService;
    private GeographyService geographyService;
    private HistoryService historyService;
    private ProfileService profileService;
    private RateService rateService;
    private Team team;
    private LocalDateTime currentDateTime = LocalDateTime.now();
    private LocalDateTime lastUpdate = LocalDateTime.now();

    private List<ProfileItemModel> profileItemModels;
    private List<IGeographyItemModel> geographyItemModels = new ArrayList<>();
    private List<TeamHistoryItemModel> historyItemModels;
    private LocalDateTime updatedAt;

    private Rates hourlyRates;
    private Rates dayRates;
    private Rates annualRates;
    private BigDecimal totalHours = BigDecimal.ZERO;

    public TeamInteractor(TeamModel model,Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
            geographyService = new GeographyService();
            historyService = new HistoryService();
            profileService = new ProfileService();
            rateService = new RateService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean fetchTeam(UUID id) {
        try {
            currentDateTime = LocalDateTime.now();
            totalHours = BigDecimal.ZERO;
            team = teamService.get(id);
            updatedAt = teamService.getLastUpdated(id);
            historyItemModels = convertToHistoryModels(historyService.getTeamHistory(id));
            profileItemModels = fetchTeamMembers(id);
            hourlyRates = rateService.calculateRates(team, RateType.HOURLY);
            dayRates = rateService.calculateRates(team, RateType.DAY);
            annualRates = rateService.calculateRates(team, RateType.ANNUAL);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private List<TeamHistoryItemModel> convertToHistoryModels(List<TeamHistory> history) throws Exception {
        List<TeamHistoryItemModel> historyModels = new ArrayList<>();

        for (TeamHistory team : history) {
            TeamHistoryItemModel historyModel = new TeamHistoryItemModel();
            historyModel.teamIdProperty().set(team.teamId());

            historyModel.updatedAtProperty().set(team.updatedAt());
            historyModel.reasonProperty().set(team.reason());
            historyModel.totalHoursProperty().set(team.annualHours());
            historyModel.setHourlyRate(team.hourlyRate());
            historyModel.setDayRate(team.dayRate());

            for (TeamProfileHistory teamProfileHistory : team.teamProfileHistories()) {
                TeamHistoryProfileItemModel profileItemModel = new TeamHistoryProfileItemModel();
                if (teamProfileHistory.profileId() != null) continue;

                Profile profile = profileService.get(teamProfileHistory.profileId());

                profileItemModel.nameProperty().set(profile.getName());
                profileItemModel.profileIdProperty().set(teamProfileHistory.profileHistoryId());
                profileItemModel.costAllocationProperty().set(teamProfileHistory.costAllocation());
                profileItemModel.hourAllocationProperty().set(teamProfileHistory.hourAllocation());
                profileItemModel.setHourlyRate(teamProfileHistory.hourlyRate());
                profileItemModel.setDayRate(teamProfileHistory.dayRate());
                profileItemModel.setAnnualCost(teamProfileHistory.annualCost());
                profileItemModel.totalHoursProperty().set(teamProfileHistory.annualHours());

                historyModel.details().add(profileItemModel);
            }

            historyModels.add(historyModel);
        }

        return historyModels;
    }

    public List<ProfileItemModel> fetchTeamMembers(UUID teamId) throws Exception {
        List<Profile> profilesTeams = teamService.getTeamProfiles(teamId);
        return convertToProfileItemModels(profilesTeams);
    }

    private List<ProfileItemModel> convertToProfileItemModels(List<Profile> profiles) throws Exception {
        List<ProfileItemModel> profileItemModels = new ArrayList<>();
        geographyItemModels.clear();

        for (Profile profile : profiles) {
            ProfileItemModel profileItemModel = new ProfileItemModel();
            profileItemModel.UUIDProperty().set(profile.getProfileId());
            profileItemModel.nameProperty().set(profile.getName());
            profileItemModel.setAnnualCost(profile.getAnnualCost());
            profileItemModel.currencyProperty().set(profile.getCurrency());
            profileItemModel.countryIdProperty().set(profile.getCountryId());
            profileItemModel.hoursPerDayProperty().set(profile.getHoursPerDay());
            profileItemModel.effectivenessProperty().set(profile.getEffectivenessPercentage());
            profileItemModel.annualHoursProperty().set(profile.getAnnualHours());
            profileItemModel.effectiveWorkHoursProperty().set(profile.getEffectiveWorkHours());
            profileItemModel.updatedAtProperty().set(profile.getUpdatedAt());
            profileItemModel.resourceTypeProperty().set(profile.isResourceType());
            profileItemModel.archivedProperty().set(profile.isArchived());


            String geographyName = geographyService.getByCountryId(profile.getCountryId()).name();
            profileItemModel.locationProperty().set(geographyName);

            IGeographyItemModel geographyItemModel = new GeographyItemModel();
            geographyItemModel.nameProperty().set(geographyName);

            geographyItemModels.add(geographyItemModel);
            profileItemModels.add(profileItemModel);
        }

        return profileItemModels;
    }

    public boolean removeTeamMember(ProfileItemModel profileItemModel) {
        try {
            return teamService.removeProfileFromTeam(model.teamIdProperty().get(), profileItemModel.UUIDProperty().get());
        } catch (Exception e) {
            return false;
        }
    }

    public void updateModel() {
        model.teamIdProperty().set(team.getTeamId());
        model.teamNameProperty().set(team.getName());
        model.archivedProperty().set(team.isArchived());
        model.updatedAtProperty().set(updatedAt);
        model.profiles().setAll(profileItemModels);
        model.geographies().setAll(geographyItemModels);
        model.history().setAll(historyItemModels);
        model.profilesFetchedProperty().set(true);
        model.numProfilesProperty().set("" + profileItemModels.size());

        model.currentDateProperty().set(currentDateTime);

        model.setRawRate(hourlyRates.rawRate());
        model.setMarkupRate(hourlyRates.markupRate());
        model.setGrossMarginRate(hourlyRates.grossMarginRate());
        model.totalHoursProperty().set(totalHours.toString());
    }

    public boolean exportTeamToExcel(File file, UUID teamId) {
        ExportToExcel exportToExcel = new ExportToExcel();
        try {
            exportToExcel.exportTeam(teamId, file);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean archiveTeam() {
        try {
            Team team = teamService.get(model.teamIdProperty().get());
            return teamService.archive(team, true);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Profile> canUnarchiveTeam() {
        try {
            Team team = teamService.get(model.teamIdProperty().get());
            return teamService.canUnarchive(team);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean unArchiveTeam() {
        try {
            Team team = teamService.get(model.teamIdProperty().get());

            boolean unArchived = teamService.archive(team, false);
            if (unArchived)
                model.archivedProperty().set(false);

            return unArchived;
        } catch (Exception e) {
            return false;
        }
    }
}