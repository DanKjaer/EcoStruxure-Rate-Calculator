package ecostruxure.rate.calculator.gui.component.modals.addteam;

import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.modal.ModalModel;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.List;

public class AddTeamController implements ModalController {
    private final AddTeamModel model;
    private final AddTeamInteractor interactor;
    private final View view;
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;

    private List<Node> tabOrder;

    public AddTeamController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new AddTeamModel();
        interactor = new AddTeamInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        this.view = new AddTeamView(model, this::setTabOrder);
        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;
    }

    @Override
    public void activate(Object sum) {
        model.profilesFetchedProperty().set(false);
        model.profiles().clear();
        fetchProfiles();
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
                return interactor.addTeam();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                outerLookupHandler.run();
                fetchProfiles();
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_CREATED));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_CREATE));
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
                LocalizedText.ADD_NEW_TEAM,
                LocalizedText.CREATE,
                LocalizedText.GO_BACK,
                model.okToAddProperty(),
                new SimpleIntegerProperty(550),
                new SimpleIntegerProperty(650)
        );
    }

    private void fetchProfiles() {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchProfiles();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_MARKUP));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }

}
