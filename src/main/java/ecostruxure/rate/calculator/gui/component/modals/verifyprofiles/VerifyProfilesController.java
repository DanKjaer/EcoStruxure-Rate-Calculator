package ecostruxure.rate.calculator.gui.component.modals.verifyprofiles;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.gui.component.team.TeamController;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.background.BackgroundTaskEvent;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.teams.TeamData;
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

public class VerifyProfilesController implements ModalController {
    private final VerifyProfilesModel model;
    private final VerifyProfilesInteractor interactor;
    private final View view;
    private final EventBus eventBus;
    private final Runnable outerLookupHandler;

    private List<Node> tabOrder;

    public VerifyProfilesController(EventBus eventBus, Runnable outerLookupHandler) {
        model = new VerifyProfilesModel();
        interactor = new VerifyProfilesInteractor(model, () -> eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_CONNECTION)));
        this.view = new VerifyProfilesView(model, this::setTabOrder);
        this.eventBus = eventBus;
        this.outerLookupHandler = outerLookupHandler;
    }

    @Override
    public void activate(Object data) {
        if (data instanceof TeamData team) {
            model.profiles().clear();
            model.setTeamId(model.getTeamId());
            convertProfiles(team.teamId(), team.profiles());
        }
    }

    private void convertProfiles(UUID teamId, List<Profile> data) {
        Task<Boolean> fetchTask = new Task<>() {
            @Override
            protected Boolean call() {
                return interactor.convertTeam(data, teamId);
            }
        };

        fetchTask.setOnSucceeded(evt -> {
            if (fetchTask.getValue()) interactor.updateModel();
            else eventBus.publish(new NotificationEvent(NotificationType.FAILURE, LocalizedText.ERROR_FETCH_TEAM));
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
                return interactor.updateProfiles();
            }
        };

        saveTask.setOnSucceeded(evt -> {
            postTaskGuiActions.run();
            if (saveTask.getValue()) {
                outerLookupHandler.run();
                eventBus.publish(new RefreshEvent(TeamsController.class));
                eventBus.publish(new RefreshEvent(TeamController.class));
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
                LocalizedText.VERIFY_PROFILES,
                LocalizedText.SAVE,
                LocalizedText.GO_BACK,
                model.okToUnArchiveProperty(),
                new SimpleIntegerProperty(550),
                new SimpleIntegerProperty(500)
        );
    }

    private void setTabOrder(List<Node> tabOrder) {
        this.tabOrder = tabOrder;
    }
}
