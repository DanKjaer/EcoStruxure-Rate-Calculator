package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.*;
import ecostruxure.rate.calculator.be.enums.RateType;
import ecostruxure.rate.calculator.be.data.Rates;
import ecostruxure.rate.calculator.bll.service.TeamService;
import ecostruxure.rate.calculator.gui.util.ExportToExcel;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class TeamsInteractor {
    private final TeamsModel model;
    private TeamService teamService;
    private RateService rateService;
    private List<TeamItemModel> teamItemModels;
    private Map<UUID, Rates> hourlyRates = new HashMap<>();
    private Map<UUID, Rates> dayRates = new HashMap<>();
    private Map<UUID, Rates> annualRates = new HashMap<>();

    public TeamsInteractor(TeamsModel model, Runnable onFetchError) {
        this.model = model;

        try {
            this.teamService = new TeamService();
            this.rateService = new RateService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean fetchTeams() {
        try {
            List<Team> teams = teamService.all();
            teamItemModels = convertToTeamItemModels(teams);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportTeamToExcel(File file, TeamItemModel teamItemModel) {
        ExportToExcel exportToExcel = new ExportToExcel();
        try {
            exportToExcel.exportTeam(teamItemModel.teamIdProperty().get(), file);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean exportTeamsToExcel(File file, List<TeamItemModel> teams) {
        ExportToExcel exportToExcel = new ExportToExcel();
        try {
            List<Team> teamList = convertModelsToEntity(teams, false);
            exportToExcel.exportTeams(teamList, file);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateModel() {
        model.teams().setAll(teamItemModels);
        model.numTeamsProperty().set(String.valueOf(model.teams().size()));
        model.teamsFetchedProperty().set(true);
        updateColumnNames(model.selectedRateTypeProperty().get());
    }

    List<TeamItemModel> convertToTeamItemModels(List<Team> teams) throws Exception {
        List<TeamItemModel> teamItemModels = new ArrayList<>();

        for (Team team : teams) {
            TeamItemModel teamItemModel = new TeamItemModel();
            teamItemModel.teamIdProperty().set(team.getTeamId());

            hourlyRates.put(team.getTeamId(), rateService.calculateRates(team, RateType.HOURLY));
            dayRates.put(team.getTeamId(), rateService.calculateRates(team, RateType.DAY));
            annualRates.put(team.getTeamId(), rateService.calculateRates(team, RateType.ANNUAL));

            Rates rates;
            switch (model.selectedRateTypeProperty().get()) {
                case DAY -> rates = dayRates.get(team.getTeamId());
                case ANNUAL -> rates = annualRates.get(team.getTeamId());
                default -> rates = hourlyRates.get(team.getTeamId());
            }

            teamItemModel.setRawRate(rates.rawRate());
            teamItemModel.setMarkupRate(rates.markupRate());
            teamItemModel.setGrossMarginRate(rates.grossMarginRate());

            teamItemModel.nameProperty().set(team.getName());
            teamItemModel.markupProperty().set(team.getMarkup());
            teamItemModel.grossMarginProperty().set(team.getGrossMargin());
            teamItemModel.archivedProperty().set(team.isArchived());
            teamItemModel.updatedAtProperty().set(team.getUpdatedAt());
            teamItemModel.dayRateProperty().set(team.getDayRate());
            teamItemModel.hourlyRateProperty().set(team.getHourlyRate());
            teamItemModel.totalAllocatedCostProperty().set(team.getTotalAllocatedCost());
            teamItemModel.totalAllocatedHoursProperty().set(team.getTotalAllocatedHours());

            teamItemModels.add(teamItemModel);
        }

        return teamItemModels;
    }

    private List<Team> convertModelsToEntity(List<TeamItemModel> teamItemModels, boolean checkArchived) {
        List<Team> teams = new ArrayList<>();
        for (TeamItemModel teamItemModel : teamItemModels) {
            if (checkArchived && teamItemModel.archivedProperty().get())
                continue;

            Team team = new Team.Builder()
                    .teamId(teamItemModel.teamIdProperty().get())
                    .name(teamItemModel.nameProperty().get())
                    .markup(teamItemModel.markupProperty().get())
                    .grossMargin(teamItemModel.grossMarginProperty().get())
                    .archived(teamItemModel.archivedProperty().get())
                    .build();
            teams.add(team);
        }
        return teams;
    }

    public void swapRateType() {
        RateType rateType = model.selectedRateTypeProperty().get();
        for (TeamItemModel team : model.teams()) {
            Rates rates;
            switch (rateType) {
                case DAY -> rates = dayRates.get(team.teamIdProperty());
                case ANNUAL -> rates = annualRates.get(team.teamIdProperty());
                default -> rates = hourlyRates.get(team.teamIdProperty());
            }

            team.setRawRate(rates.rawRate());
            team.setMarkupRate(rates.markupRate());
            team.setGrossMarginRate(rates.grossMarginRate());
        }

        updateColumnNames(rateType);
    }

    private void updateColumnNames(RateType rateType) {
        model.rawColumnNameProperty().unbind();
        model.markupColumnNameProperty().unbind();
        model.grossMarginColumnNameProperty().unbind();

        switch (rateType) {
            case HOURLY -> {
                model.rawColumnNameProperty().bind(LocalizedText.RAW_HOURLY_RATE);
                model.markupColumnNameProperty().bind(LocalizedText.MARKUP_HOURLY_RATE);
                model.grossMarginColumnNameProperty().bind(LocalizedText.GM_HOURLY_RATE);
            }
            case DAY -> {
                model.rawColumnNameProperty().bind(LocalizedText.RAW_DAY_RATE);
                model.markupColumnNameProperty().bind(LocalizedText.MARKUP_DAY_RATE);
                model.grossMarginColumnNameProperty().bind(LocalizedText.GM_DAY_RATE);
            }
            case ANNUAL -> {
                model.rawColumnNameProperty().bind(LocalizedText.RAW_ANNUAL_COST);
                model.markupColumnNameProperty().bind(LocalizedText.MARKUP_ANNUAL_COST);
                model.grossMarginColumnNameProperty().bind(LocalizedText.GM_ANNUAL_COST);
            }
        }
    }

    public boolean saveMarkup(UUID teamId, BigDecimal newValue) {
        try {
            teamService.setMarkup(teamId, newValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean archiveTeam(TeamItemModel teamItemModel) {
        try {
            Team team = teamService.get(teamItemModel.teamIdProperty().get());

            return teamService.archive(team, true);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Profile> canUnarchiveTeam(TeamItemModel teamItemModel) {
        try {
            Team team = teamService.get(teamItemModel.teamIdProperty().get());
            return teamService.canUnarchive(team);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean unArchiveTeam(TeamItemModel teamItemModel) {
        try {
            Team team = teamService.get(teamItemModel.teamIdProperty().get());

            boolean unArchived = teamService.archive(team, false);
            if (unArchived)
                teamItemModel.archivedProperty().set(false);

            return unArchived;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean archiveTeams() {
        try {
            List<Team> teams = convertModelsToEntity(model.teamsToArchive(), true);
            return teamService.archive(teams);
        } catch (Exception e) {
            return false;
        }
    }

    public void updateArchivedTeams() {
        for (TeamItemModel teamItemModel : model.teamsToArchive())
            teamItemModel.archivedProperty().set(true);
    }

    public void updateArchivedTeam() {
        model.teamToArchiveProperty().get().archivedProperty().set(true);
    }
}