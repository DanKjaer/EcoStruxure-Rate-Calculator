package ecostruxure.rate.calculator.gui.component.geography;

import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.modals.addgeography.AddGeographyController;
import ecostruxure.rate.calculator.gui.component.geography.country.CountryInteractor;
import ecostruxure.rate.calculator.gui.component.geography.geograph.GeographyInteractor;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.modal.ShowModalEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.system.view.ChangeViewEvent;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

public class GeographyController implements Controller {
    private final GeographyModel model;
    private final GeographyInteractor geographyInteractor;
    private final CountryInteractor countryInteractor;
    private final EventBus eventBus;
    private final View view;
    private final ModalController addProfileController;

    public GeographyController(EventBus eventBus) {
        model = new GeographyModel();
        countryInteractor = new CountryInteractor(model, this::connectionError);
        geographyInteractor = new GeographyInteractor(countryInteractor, model, this::connectionError);
        view = new GeographyView(model, this::addGeography);
        this.eventBus = eventBus;
        this.addProfileController = new AddGeographyController(eventBus, this::refresh);

        model.all().clear();
        fetchGeographies();
        fetchCountries();
    }

    @Override
    public void activate(Object data) {

    }

    @Override
    public Region view() {
        return view.build();
    }

    private void refresh() {
        model.all().clear();
        fetchGeographies();
        fetchCountries();
    }

    private void fetchGeographies() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return geographyInteractor.getGeographies();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) geographyInteractor.updateStats();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_GEOGRAPHIES));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void fetchCountries() {
        Task<Boolean> fetchTask = new Task<>() {
            protected Boolean call() {
                return countryInteractor.getCountries();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) countryInteractor.updateStats();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_COUNTRIES));
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void addGeography() {
        eventBus.publish(new ShowModalEvent(addProfileController));
    }

    private void connectionError() {
        eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION));
    }
}