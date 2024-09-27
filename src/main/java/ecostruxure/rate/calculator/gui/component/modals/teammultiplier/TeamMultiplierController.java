package ecostruxure.rate.calculator.gui.component.modals.teammultiplier;

import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.View;
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

public class TeamMultiplierController implements ModalController {
    private final TeamMultiplierModel model;
    private final TeamMultiplierInteractor interactor;
    private final View view;
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;

    private List<Node> tabOrder;

    public TeamMultiplierController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new TeamMultiplierModel();
        interactor = new TeamMultiplierInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new TeamMultiplierView(model, this::setTabOrder);
        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;
    }

    @Override
    public void activate(Object teamId) {
        if (teamId instanceof UUID) {
            model.teamIdProperty().get();
            model.markupFetchedProperty().set(false);
            model.markupProperty().set("");
            model.grossMarginFetchedProperty().set(false);
            model.grossMarginProperty().set("");
            fetchMultipliers((UUID) teamId);
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
                return interactor.saveMultipliers();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                outerLookupHandler.run();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_MULTIPLIERS_SAVED));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_MARKUP_SAVE));
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
                LocalizedText.ADJUST_MULTIPLIERS,
                LocalizedText.SAVE,
                LocalizedText.GO_BACK,
                model.okToSaveProperty(),
                new SimpleIntegerProperty(384),
                new SimpleIntegerProperty(284)
        );
    }

    private void fetchMultipliers(UUID teamId) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchMultipliers(teamId);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateModel();
            } else{
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_MARKUP));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }
}