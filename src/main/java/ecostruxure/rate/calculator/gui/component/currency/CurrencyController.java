package ecostruxure.rate.calculator.gui.component.currency;

import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

public class CurrencyController implements Controller {
    private final CurrencyModel model;
    private final CurrencyInteractor interactor;
    private final View view;
    private final EventBus eventBus;

    public CurrencyController(EventBus eventBus) {
        model = new CurrencyModel();
        interactor = new CurrencyInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        view = new CurrencyView(model);
        this.eventBus = eventBus;

        fetchCurrencies();

        eventBus.subscribe(RefreshEvent.class, event -> {
            if (event.target().equals(CurrencyController.class)) fetchCurrencies();
        });
    }

    @Override
    public void activate(Object data) {

    }

    @Override
    public Region view() {
        return view.build();
    }

    private void fetchCurrencies() {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchCurrencies();
            }
        };

        fetchTask.setOnSucceeded(event -> {
            if (fetchTask.getValue()) interactor.updateModel();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_CURRENCIES));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }
}