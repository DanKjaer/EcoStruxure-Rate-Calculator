package ecostruxure.rate.calculator.gui.component.modals.addprofile;

import ecostruxure.rate.calculator.gui.component.currency.CurrencyController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.common.View;
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

public class AddProfileController implements ModalController {
    private final AddProfileModel model;
    private final AddProfileInteractor interactor;
    private final View view;
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;

    private List<Node> tabOrder;
    private Region viewRegion;
    private boolean isViewInitialized = false;

    public AddProfileController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new AddProfileModel();
        interactor = new AddProfileInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new AddProfileView(model, this::setTabOrder);
        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;

        fetchData();

        eventBus.subscribe(RefreshEvent.class, event -> {
            if (event.target().equals(CurrencyController.class)) fetchData();
        });
    }
    @Override
    public void activate(Object data) {

    }

    @Override
    public Region view() {
        if (!isViewInitialized) {
            viewRegion = view.build();
            isViewInitialized = true;
        }
        return viewRegion;
    }

    @Override
    public void onSave(Runnable postTaskGuiActions) {
        Task<Boolean> addTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.addProfile();
            }
        };

        addTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (addTask.getValue()) {
                outerLookupHandler.run();
                interactor.updateModelAfterAdd();
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, LocalizedText.SUCCESS_PROFILE_CREATED));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_PROFILE_CREATE));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(addTask));
    }

    @Override
    public Runnable onClose() {
        return null;
    }

    @Override
    public ModalModel modalModel() {
        return new ModalModel(
                LocalizedText.ADD_NEW_PROFILE,
                LocalizedText.CREATE,
                LocalizedText.GO_BACK,
                model.okToAddProperty(),
                new SimpleIntegerProperty(384),
                new SimpleIntegerProperty(725)
        );
    }

    @Override
    public List<Node> order() {
        return tabOrder;
    }

    private void fetchData() {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchData();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModelAfterFetch();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_TEAMS));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }


    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }
}