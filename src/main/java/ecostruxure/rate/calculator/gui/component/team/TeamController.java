package ecostruxure.rate.calculator.gui.component.team;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.gui.component.profiles.ProfilesController;
import ecostruxure.rate.calculator.gui.util.ExportToExcel;
import ecostruxure.rate.calculator.gui.component.modals.teamedit.TeamEditController;
import ecostruxure.rate.calculator.gui.component.modals.verifyprofiles.VerifyProfilesController;
import ecostruxure.rate.calculator.gui.component.teams.TeamData;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.modals.assignprofile.AssignProfileController;
import ecostruxure.rate.calculator.gui.component.modals.teameditprofile.TeamEditProfileController;
import ecostruxure.rate.calculator.gui.component.modals.teammultiplier.TeamMultiplierController;
import ecostruxure.rate.calculator.gui.component.profile.ProfileController;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.component.teams.TeamDataId;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.system.modal.ShowModalEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.system.view.ChangeViewEvent;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class TeamController implements Controller {
    private final TeamModel model;
    private final TeamInteractor interactor;
    private final View view;
    private final EventBus eventBus;

    private final ModalController teamMultiplierController;
    private final ModalController assignProfileController;
    private final ModalController editTeamProfileController;
    private final ModalController teamEditController;
    private final ModalController verifyProfilesController;

    public TeamController(EventBus eventBus) {
        model = new TeamModel();
        interactor = new TeamInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new TeamView(model, this::adjustMultipliers, this::assignProfiles, this::teamEditProfile,
                            this::showProfile, this::teamRemoveProfile, this::refresh, this::exportTeam, this::editTeam,
                            this::archiveTeam, this::unArchiveTeam);
        this.eventBus = eventBus;

        this.teamMultiplierController = new TeamMultiplierController(eventBus, this::refresh);
        this.assignProfileController = new AssignProfileController(eventBus, this::refresh);
        this.editTeamProfileController = new TeamEditProfileController(eventBus, this::refresh);
        this.teamEditController = new TeamEditController(eventBus, this::refresh);
        this.verifyProfilesController = new VerifyProfilesController(eventBus, this::refresh);

    }

    @Override
    public void activate(Object teamId) {
        if (teamId instanceof UUID) {
            model.teamNameProperty().set(LocalizedText.LOADING.get());
            model.numProfilesProperty().set(LocalizedText.LOADING.get());
            model.profilesFetchedProperty().set(false);
            fetchTeam((UUID) teamId);
        }
    }

    @Override
    public Region view() {
        return view.build();
    }

    private void fetchTeam(UUID id) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchTeam(id);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModel();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_TEAM));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void teamRemoveProfile(ProfileItemModel profileItemModel) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.removeTeamMember(profileItemModel);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                refresh();
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_PROFILE_REMOVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_PROFILE_REMOVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void refresh() {
        model.teamNameProperty().set(LocalizedText.LOADING.get());
        model.numProfilesProperty().set(LocalizedText.LOADING.get());
        model.profilesFetchedProperty().set(false);
        fetchTeam(model.teamIdProperty().get());
    }

    public void adjustMultipliers() {
        eventBus.publish(new ShowModalEvent(teamMultiplierController, model.teamIdProperty()));
    }

    public void assignProfiles() {
        eventBus.publish(new ShowModalEvent(assignProfileController, model.teamIdProperty()));
    }

    private void teamEditProfile(ProfileItemModel profileItemModel) {
        eventBus.publish(new ShowModalEvent(editTeamProfileController, new TeamDataId(model.teamIdProperty().get(), profileItemModel.UUIDProperty().get())));
    }


    private void showProfile(ProfileItemModel data) {
        eventBus.publish(new ChangeViewEvent(ProfileController.class, data.UUIDProperty()));
    }

    private void showVerifyProfiles(UUID teamId, List<Profile> profilesToVerify) {
        eventBus.publish(new ShowModalEvent(verifyProfilesController, new TeamData(teamId, profilesToVerify)));
    }

    public void editTeam() {
        eventBus.publish(new ShowModalEvent(teamEditController, model.teamIdProperty()));
    }

    private void archiveTeam() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveTeam();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
                model.archivedProperty().set(true);
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_ARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_ARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void unArchiveTeam() {
        List<Profile> profilesToVerify = interactor.canUnarchiveTeam();
        if (profilesToVerify != null && !profilesToVerify.isEmpty()) {
            showVerifyProfiles(model.teamIdProperty().get(), profilesToVerify);
            return;
        }

        if (interactor.unArchiveTeam()) {
            eventBus.publish(new RefreshEvent(TeamsController.class));
            eventBus.publish(new RefreshEvent(ProfilesController.class));
            eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_UNARCHIVE));
        }
        else
            eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_UNARCHIVE));

    }

    private void exportTeam() {
        File file = ExportToExcel.saveFile(model.teamNameProperty().get());

        if (file != null) {
            Task<Boolean> fetchTask = new Task<>() {
                protected Boolean call() {
                    return interactor.exportTeamToExcel(file, model.teamIdProperty().get());
                }
            };

            fetchTask.setOnSucceeded(evt -> {
                if (fetchTask.getValue()) eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_EXPORT_TEAM));
                else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_EXPORT_TEAM));
            });

            eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
        }
    }
}