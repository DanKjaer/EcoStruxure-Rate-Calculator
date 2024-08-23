package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.component.teams.TeamDataId;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.system.modal.ModalModel;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.UUID;

public class TeamEditProfileController implements ModalController {
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;
    private final TeamEditProfileModel model;
    private final TeamEditProfileInteractor interactor;
    private final TeamEditProfileView view;

    private List<Node> tabOrder;

    public TeamEditProfileController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new TeamEditProfileModel();
        interactor = new TeamEditProfileInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new TeamEditProfileView(model, this::setTabOrder);

        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;
    }

    @Override
    public void activate(Object data) {
        if (data instanceof TeamDataId teamData) {
            model.setProfileId(model.getProfileId());
            model.setProfileId(teamData.profileId());
            model.profileNameProperty().set("");
            model.costAllocationFetchedProperty().set(false);
            model.costAllocationProperty().set("");
            model.hourAllocationFetchedProperty().set(false);
            model.hourAllocationProperty().set("");

            model.originalhourAllocationProperty().set("");
            model.originalCostAllocationProperty().set("");

            fetchTeamProfile(teamData.teamId(), teamData.profileId());
        }
    }

    private void fetchTeamProfile(UUID teamId, UUID profileId) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchTeamProfile(teamId, profileId);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_TEAM_PROFILE));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    @Override
    public Region view() {
        return view.build();
    }

    @Override
    public List<Node> order() {
        return tabOrder;
    }

    @Override
    public void onSave(Runnable postTaskGuiActions) {
        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.saveTeamProfile();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                outerLookupHandler.run();
                interactor.updateModelSave();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_PROFILE_SAVED));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_PROFILE_SAVE));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(saveTask));

    }

    @Override
    public Runnable onClose() {
        return null;
    }

    @Override
    public ModalModel modalModel() {
        return new ModalModel(
                LocalizedText.EDIT_TEAM_PROFILE,
                LocalizedText.SAVE,
                LocalizedText.GO_BACK,
                model.okToSaveProperty(),
                new SimpleIntegerProperty(384),
                new SimpleIntegerProperty(284)
        );
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }
}
