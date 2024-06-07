package ecostruxure.rate.calculator.gui.component.modals.addgeography;

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

public class AddGeographyController implements ModalController {
    private final AddGeographyModel model;
    private final AddGeographyInteractor interactor;
    private final View view;
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;

    private List<Node> tabOrder;

    public AddGeographyController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new AddGeographyModel();
        interactor = new AddGeographyInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new AddGeographyView(model, this::checkIfGeographyExists, this::setTabOrder);
        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;

        fetchCountries();
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }

    @Override
    public void activate(Object data) {

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
        Task<Boolean> createTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.createGeography();
            }
        };

        createTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (createTask.getValue()) {
                outerLookupHandler.run();
                interactor.updateModelAfterAdd();
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_GEOGRAPHY_CREATED));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_GEOGRAPHY_CREATE));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(createTask));
    }

    @Override
    public Runnable onClose() {
        return null;
    }

    @Override
    public ModalModel modalModel() {
        return new ModalModel(
                LocalizedText.ADD_NEW_GEOGRAPHY,
                LocalizedText.CREATE,
                LocalizedText.GO_BACK,
                model.okToCreateProperty(),
                new SimpleIntegerProperty(384),
                new SimpleIntegerProperty(684)
        );
    }


    private void fetchCountries() {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchCountries();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModel();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_COUNTRIES));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void checkIfGeographyExists() {
        Task<Boolean> checkTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.geographyExists();
            }
        };

        checkTask.setOnSucceeded(evt -> {
            if (checkTask.getValue()) interactor.updateModalAfterCheck();
        });

        eventBus.publish(new BackgroundTaskEvent<>(checkTask));
    }
}