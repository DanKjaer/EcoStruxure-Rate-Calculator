package ecostruxure.rate.calculator.gui.component.modals.assignprofile;

import ecostruxure.rate.calculator.gui.component.profiles.ProfilesController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.modal.ModalModel;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.UUID;

public class AssignProfileController implements ModalController {
    private final EventBus eventBus;
    private final AssignProfileInteractor interactor;
    private final Runnable outerLookupHandler;
    private final AssignProfileModel model;
    private final AssignProfileView view;

    private List<Node> tabOrder;

    public AssignProfileController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new AssignProfileModel();
        interactor = new AssignProfileInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;

        this.view = new AssignProfileView(model, this::setTabOrder);
    }

    @Override
    public void activate(Object teamId) {
        if (teamId instanceof SimpleObjectProperty) {
            Object value = ((SimpleObjectProperty<?>) teamId).getValue();
            if (value instanceof UUID) {
                model.setTeamId(((UUID) value));
                model.profilesFetchedProperty().set(false);
                model.profiles().clear();
                model.originalProfiles().clear();
                fetchProfiles((UUID) value);
            }
        }
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
                return interactor.assignProfiles();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                outerLookupHandler.run();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(ProfilesController.class));
                fetchProfiles(model.getTeamId());
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_ASSIGNED_PROFILES));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_ASSIGN));
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
                LocalizedText.ASSIGN_PROFILES,
                LocalizedText.SAVE,
                LocalizedText.GO_BACK,
                model.okToSaveProperty(),
                new SimpleIntegerProperty(550),
                new SimpleIntegerProperty(650)
        );
    }

    private void fetchProfiles(UUID teamId) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchProfiles(teamId);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_PROFILES));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }
}
