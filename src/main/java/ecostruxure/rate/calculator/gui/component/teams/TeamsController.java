package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.gui.component.profiles.ProfilesController;
import ecostruxure.rate.calculator.gui.util.ExportToExcel;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.component.modals.addteam.AddTeamController;
import ecostruxure.rate.calculator.gui.component.modals.assignprofile.AssignProfileController;
import ecostruxure.rate.calculator.gui.component.modals.teamedit.TeamEditController;
import ecostruxure.rate.calculator.gui.component.modals.teammultiplier.TeamMultiplierController;
import ecostruxure.rate.calculator.gui.component.modals.verifyprofiles.VerifyProfilesController;
import ecostruxure.rate.calculator.gui.component.team.TeamController;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.modal.ShowModalEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.system.view.ChangeViewEvent;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class TeamsController implements Controller {
    private final TeamsModel model;
    private final TeamsInteractor interactor;
    private final TeamsView view;
    private final EventBus eventBus;

    private final ModalController teamMultiplierController;
    private final ModalController addTeamController;
    private final ModalController verifyProfilesController;
    private final ModalController assignProfileController;
    private final ModalController teamEditController;

    public TeamsController(EventBus eventBus) {
        model = new TeamsModel();
        interactor = new TeamsInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));

        view = new TeamsView(model, this::showTeam, this::adjustMultipliers, this::adjustMarkup,
                            this::assignProfiles, this::editTeam, this::addTeam, this::archiveTeam,
                            this::unArchiveTeam, this::refresh, this::exportTeam, this::changeRateType,
                            this::archiveTeams, this::exportTeams);
        this.eventBus = eventBus;

        this.teamMultiplierController = new TeamMultiplierController(eventBus, this::refresh);
        this.addTeamController = new AddTeamController(eventBus, this::refresh);
        this.verifyProfilesController = new VerifyProfilesController(eventBus, this::refresh);
        this.assignProfileController = new AssignProfileController(eventBus, this::refresh);
        this.teamEditController = new TeamEditController(eventBus, this::refresh);

        fetchTeams();

        eventBus.subscribe(RefreshEvent.class, event -> {
            if (event.target().equals(TeamsController.class)) refresh();
        });
    }

    @Override
    public void activate(Object data) {

    }

    @Override
    public Region view() {
        return view.build();
    }


    private void refresh() {
        model.teamsFetchedProperty().set(false);
        fetchTeams();
    }

    private void addTeam() {
        eventBus.publish(new ShowModalEvent(addTeamController, true));
    }

    private void adjustMultipliers(TeamItemModel teamItemModel) {
        eventBus.publish(new ShowModalEvent(teamMultiplierController, teamItemModel.teamIdProperty().get()));
    }

    public void assignProfiles(TeamItemModel teamItemModel) {
        eventBus.publish(new ShowModalEvent(assignProfileController, teamItemModel.teamIdProperty().get()));
    }

    public void editTeam(TeamItemModel teamItemModel) {
        eventBus.publish(new ShowModalEvent(teamEditController, teamItemModel.teamIdProperty().get()));
    }

    private void showTeam(TeamItemModel teamItemModel) {
        eventBus.publish(new ChangeViewEvent(TeamController.class, teamItemModel.teamIdProperty().get()));
    }

    private void adjustMarkup(TeamItemModel teamItemModel, BigDecimal newValue) {
        interactor.saveMarkup(teamItemModel.teamIdProperty().get(), newValue);
    }

    private void showVerifyProfiles(TeamItemModel teamItemModel, List<Profile> profilesToVerify) {
        eventBus.publish(new ShowModalEvent(verifyProfilesController, new TeamData(teamItemModel.teamIdProperty().get(), profilesToVerify)));
    }

    private void archiveTeam(TeamItemModel teamItemModel) {
        model.teamToArchiveProperty().set(teamItemModel);

        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveTeam(teamItemModel);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
                interactor.updateArchivedTeam();
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_ARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_ARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void archiveTeams(List<TeamItemModel> teamItemModels) {
        model.teamsToArchive().clear();
        model.teamsToArchive().setAll(teamItemModels);

        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveTeams();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
                interactor.updateArchivedTeams();
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAMS_ARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAMS_ARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void unArchiveTeam(TeamItemModel teamItemModel) {
        List<Profile> profilesToVerify = interactor.canUnarchiveTeam(teamItemModel);
        if (profilesToVerify != null && !profilesToVerify.isEmpty()) {
            showVerifyProfiles(teamItemModel, profilesToVerify);
            return;
        }

        if (interactor.unArchiveTeam(teamItemModel)) {
            eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_UNARCHIVE));
            eventBus.publish(new RefreshEvent(ProfilesController.class));
        } else {
            eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_UNARCHIVE));
        }
    }

    private void changeRateType() {
        interactor.swapRateType();
    }

    private void exportTeam(TeamItemModel teamItemModel) {
        File file = ExportToExcel.saveFile(teamItemModel.nameProperty().get());

        if (file != null) {
            Task<Boolean> fetchTask = new Task<>() {
                protected Boolean call() {
                    return interactor.exportTeamToExcel(file, teamItemModel);
                }
            };

            fetchTask.setOnSucceeded(evt -> {
                if (fetchTask.getValue()) eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_EXPORT_TEAM));
                else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_EXPORT_TEAM));
            });

            eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
        }
    }

    private void exportTeams(List<TeamItemModel> teamItemModel) {
        File file = ExportToExcel.saveFile("Teams");

        if (file != null) {
            Task<Boolean> fetchTask = new Task<>() {
                protected Boolean call() {
                    return interactor.exportTeamsToExcel(file, teamItemModel);
                }
            };

            fetchTask.setOnSucceeded(evt -> {
                if (fetchTask.getValue()) eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_EXPORT_TEAMS));
                else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_EXPORT_TEAMS));
            });

            eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
        }
    }

    private void fetchTeams() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.fetchTeams();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModel();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_TEAMS));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }
}
