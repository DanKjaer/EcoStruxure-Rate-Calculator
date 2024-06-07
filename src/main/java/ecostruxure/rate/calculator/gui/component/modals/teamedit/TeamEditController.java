package ecostruxure.rate.calculator.gui.component.modals.teamedit;

import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
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

public class TeamEditController implements ModalController {
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;
    private final TeamEditModel model;
    private final TeamEditInteractor interactor;
    private final TeamEditView view;

    private List<Node> tabOrder;

    public TeamEditController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new TeamEditModel();
        interactor = new TeamEditInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new TeamEditView(model, this::setTabOrder);

        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;
    }

    @Override
    public void activate(Object teamId) {
        if (teamId instanceof Integer) {
            model.teamIdProperty().set((Integer) teamId);
            model.teamNameProperty().set("");
            model.newNameProperty().set("");
            model.teamFetchedProperty().set(false);
            fetchTeam((int) teamId);
        }
    }

    private void fetchTeam(int teamId) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchTeam(teamId);
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
                return interactor.saveTeam();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                outerLookupHandler.run();
                interactor.updateModelSave();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_TEAM_SAVED));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_TEAM_SAVE));
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
                LocalizedText.EDIT_TEAM,
                LocalizedText.SAVE,
                LocalizedText.GO_BACK,
                model.okToSaveProperty(),
                new SimpleIntegerProperty(325),
                new SimpleIntegerProperty(200)
        );
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }

}
