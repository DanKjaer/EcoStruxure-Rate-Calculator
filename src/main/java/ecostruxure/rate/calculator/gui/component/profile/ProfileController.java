package ecostruxure.rate.calculator.gui.component.profile;

import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.component.profiles.ProfilesController;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.team.TeamController;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.system.view.ChangeViewEvent;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

public class ProfileController implements Controller {
    private final ProfileModel model;
    private final ProfileInteractor interactor;
    private final View view;
    private final EventBus eventBus;


    public ProfileController(EventBus eventBus) {
        model = new ProfileModel();
        interactor = new ProfileInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new ProfileView(model, this::showTeam, this::saveTask, this::selectHistoryItem, this::undoTask, this::checkoutTask, this::archiveProfile, this::unArchiveProfile);
        this.eventBus = eventBus;
    }

    @Override
    public void activate(Object profileId) {
        if (profileId instanceof Integer) {
            fetchProfile((int) profileId);
        }
    }
    @Override
    public Region view() {
        return view.build();
    }


    private void fetchProfile(int id) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchProfile(id);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModelPostFetchProfile();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_PROFILE));
        });
        interactor.clearModel();
        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void showTeam(ProfileTeamItemModel data) {
        eventBus.publish(new ChangeViewEvent(TeamController.class, data.idProperty().get()));
    }

    private void selectHistoryItem(ProfileHistoryItemModel data) {
        interactor.changeHistoryModel(data);
    }

    private void saveTask(Runnable postTaskGuiActions) {
        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.saveProfile();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                interactor.updateModelPostSave();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_SAVED));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_SAVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(saveTask));
    }

    private void checkoutTask(Runnable postTaskGuiActions) {
        Task<Boolean> checkoutTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.saveProfile();
            }
        };

        checkoutTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (checkoutTask.getValue()) {
                interactor.updateModelPostSave();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_CHECKOUT));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_CHECKOUT));
        });

        eventBus.publish(new BackgroundTaskEvent<>(checkoutTask));
    }

    private void archiveProfile() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveProfile(true);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateArchivedProfile(true);
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_ARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_ARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void unArchiveProfile() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveProfile(false);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateArchivedProfile(false);
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_UNARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_UNARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void undoTask(Runnable postTaskGuiActions) {
        postTaskGuiActions.run();
        interactor.undoChanges();
    }
}