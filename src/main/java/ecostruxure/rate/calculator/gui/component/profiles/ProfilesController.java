package ecostruxure.rate.calculator.gui.component.profiles;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.gui.component.teams.TeamItemModel;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.component.profile.ProfileController;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.modals.addprofile.AddProfileController;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.system.modal.ShowModalEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.system.view.ChangeViewEvent;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

import java.util.List;

public class ProfilesController implements Controller {
    private final ProfilesModel model;
    private final ProfilesInteractor interactor;
    private final View view;
    private final EventBus eventBus;

    private final ModalController addProfileController;

    public ProfilesController(EventBus eventBus) {
        model = new ProfilesModel();
        interactor = new ProfilesInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new ProfilesView(model, this::addProfile, this::showProfile, this::archiveProfile, this::unArchiveProfile, this::archiveProfiles);
        this.eventBus = eventBus;

        this.addProfileController = new AddProfileController(eventBus, this::refresh);

        fetchProfiles();

        eventBus.subscribe(RefreshEvent.class, event -> {
            if (event.target().equals(ProfilesController.class)) refresh();
        });
    }

    @Override
    public void activate(Object data) {

    }

    @Override
    public Region view() {
        return view.build();
    }

    private void fetchProfiles() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.fetchProfiles();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModel();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_PROFILES));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void addProfile() {
        eventBus.publish(new ShowModalEvent(addProfileController));
    }

    private void refresh() {
        fetchProfiles();
    }

    private void showProfile(ProfileItemModel data) {
        eventBus.publish(new ChangeViewEvent(ProfileController.class, data.getUUID()));
    }

    private void archiveProfile(ProfileItemModel profile) {
        model.profileToArchive().set(profile);

        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveProfile(profile, true);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
                interactor.updateArchivedProfile(true);
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_ARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_ARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void unArchiveProfile(ProfileItemModel profile) {
        model.profileToArchive().set(profile);

        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveProfile(profile, false);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
                interactor.updateArchivedProfile(false);
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_UNARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_UNARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void archiveProfiles(List<ProfileItemModel> profileItemModels) {
        model.profilesToArchive().clear();
        model.profilesToArchive().setAll(profileItemModels);

        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return interactor.archiveProfiles();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
                interactor.updateArchivedProfiles();
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILES_ARCHIVE));
            }
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILES_ARCHIVE));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }
}
