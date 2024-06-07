package ecostruxure.rate.calculator.gui.component.main;

import atlantafx.base.theme.Theme;
import ecostruxure.rate.calculator.gui.common.FetchError;
import ecostruxure.rate.calculator.gui.component.modals.addprofile.AddProfileController;
import ecostruxure.rate.calculator.gui.dragdrop.GlobalDragAndDrop;
import ecostruxure.rate.calculator.gui.system.background.BackgroundManager;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.currency.CurrencyController;
import ecostruxure.rate.calculator.gui.component.geography.GeographyController;
import ecostruxure.rate.calculator.gui.component.profile.ProfileController;
import ecostruxure.rate.calculator.gui.component.profiles.ProfilesController;
import ecostruxure.rate.calculator.gui.component.team.TeamController;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.event.ConcurrentEventBus;
import ecostruxure.rate.calculator.gui.system.event.RefreshEvent;
import ecostruxure.rate.calculator.gui.system.modal.ModalManager;
import ecostruxure.rate.calculator.gui.system.notification.NotificationEvent;
import ecostruxure.rate.calculator.gui.system.notification.NotificationManager;
import ecostruxure.rate.calculator.gui.system.notification.NotificationType;
import ecostruxure.rate.calculator.gui.system.view.ChangeViewEvent;
import ecostruxure.rate.calculator.gui.system.view.NextViewEvent;
import ecostruxure.rate.calculator.gui.system.view.PreviousViewEvent;
import ecostruxure.rate.calculator.gui.system.view.ViewManager;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.DropInstruction;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;

public class MainController implements Controller {
    private final MainModel model;
    private final MainInteractor interactor;
    private final View view;
    private final EventBus eventBus;

    public MainController(Stage stage) {
        model = new MainModel();
        eventBus = new ConcurrentEventBus();
        interactor = new MainInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));

        var viewManager = new ViewManager(eventBus);
        var modalManager = new ModalManager(eventBus);
        var notificationManager = new NotificationManager(eventBus);
        var backgroundManager = new BackgroundManager(eventBus);

        stage.setOnCloseRequest(event -> backgroundManager.shutdown());

        Controller geographyController = new GeographyController(eventBus);
        Controller profilesController = new ProfilesController(eventBus);
        Controller profileController = new ProfileController(eventBus);
        Controller teamsController = new TeamsController(eventBus);
        Controller teamController = new TeamController(eventBus);
        Controller currencyController = new CurrencyController(eventBus);

        viewManager.addController(geographyController.getClass(), geographyController);
        viewManager.addController(profilesController.getClass(), profilesController);
        viewManager.addController(profileController.getClass(), profileController);
        viewManager.addController(teamsController.getClass(), teamsController);
        viewManager.addController(teamController.getClass(), teamController);
        viewManager.addController(currencyController.getClass(), currencyController);

        model.activeControllerClassProperty().bind(viewManager.activeControllerClassProperty());

        MainRegions regions = new MainRegions(
                geographyController.view(),
                profilesController.view(),
                profileController.view(),
                teamsController.view(),
                teamController.view(),
                currencyController.view()
        );

        MainActions actions = new MainActions(
                this::showGeography,
                this::showProfiles,
                this::showTeams,
                this::showCurrency,
                this::changeThemeMode,
                this::changeLanguage,
                this::changeCurrency
        );

        view = new MainView(
                stage.getScene(),
                model,
                modalManager.overlay(), notificationManager.notificationPane(), setupDragAndDrop(stage.getScene()),
                regions,
                actions
        );

        fetchSettings();
    }

    @Override
    public void activate(Object data) {

    }

    @Override
    public Region view() {
        var region = view.build();
        eventBus.publish(new ChangeViewEvent(GeographyController.class));

        region.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.BACK) eventBus.publish(new PreviousViewEvent());
            if (event.getButton() == MouseButton.FORWARD) eventBus.publish(new NextViewEvent());
        });
        region.requestFocus();

        return region;
    }

    private void showGeography() {
        eventBus.publish(new ChangeViewEvent(GeographyController.class));
    }

    private void showProfiles() {
        eventBus.publish(new ChangeViewEvent(ProfilesController.class));
    }

    private void showTeams() {
        eventBus.publish(new ChangeViewEvent(TeamsController.class));
    }

    private void showCurrency() {
        eventBus.publish(new ChangeViewEvent(CurrencyController.class));
    }

    private void fetchSettings() {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.fetchSettings();
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) {
                interactor.updateSettings();
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(fetchTask));
    }

    private void changeThemeMode(Runnable postTaskGuiActions) {
        Task<Boolean> changeTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.changeThemeMode();
            }
        };

        changeTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (changeTask.getValue()) {
                interactor.updateTheme();
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(changeTask));
    }

    private void changeLanguage(Runnable postTaskGuiActions) {
        Task<Boolean> changeTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.changeLanguage();
            }
        };

        changeTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (changeTask.getValue()) {
                interactor.updateLanguage();
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(changeTask));
    }

    private void changeCurrency(Runnable postTaskGuiActions) {
        Task<Boolean> changeTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.changeCurrency();
            }
        };

        changeTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (changeTask.getValue()) {
                interactor.updateCurrency();
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(changeTask));
    }

    private void importCurrencies(File file) {
        Task<FetchError> importTask = new Task<>() {
            @Override
            protected FetchError call() {
                return interactor.importCurrencies(file);
            }
        };

        importTask.setOnSucceeded(evt -> {
            if (importTask.getValue().success()) {
                interactor.updateCurrency();
                eventBus.publish(new RefreshEvent(CurrencyController.class));
                eventBus.publish(new RefreshEvent(AddProfileController.class));
                eventBus.publish(new NotificationEvent(NotificationType.SUCCESS, importTask.getValue().reason()));
            } else {
                eventBus.publish(new NotificationEvent(NotificationType.FAILURE, importTask.getValue().reason()));
            }
        });

        eventBus.publish(new BackgroundTaskEvent<>(importTask));
    }

    private void changeTheme(Pair<Theme, Theme> theme) {
        model.activeThemeProperty().set(theme);
    }

    private Node setupDragAndDrop(Scene scene) {
        var dnd = new GlobalDragAndDrop(
                scene,
                new DropInstruction(),
                ".csv",
                this::importCurrencies
        );

        return dnd.dropTargetNode();
    }
}
