package ecostruxure.rate.calculator.gui.system.notification;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;

public class NotificationManager {
    private final NotificationPane notificationPane = new NotificationPane();

    public NotificationManager(EventBus eventBus) {
        eventBus.subscribe(NotificationEvent.class, this::handleEvent);
    }

    public NotificationPane notificationPane() {
        return notificationPane;
    }

    private void handleEvent(NotificationEvent event) {
        var notification = new Notification(event.message().get());
        notification.setPrefHeight(Region.USE_PREF_SIZE);
        notification.setMaxHeight(Region.USE_PREF_SIZE);

        notification.setGraphic(switch (event.type()) {
            case REGULAR -> new FontIcon(Icons.CHATBUBBLE);
            case INFO -> new FontIcon(Icons.HELP);
            case SUCCESS -> new FontIcon(Icons.CHECK_CIRCLE);
            case WARNING -> new FontIcon(Icons.FLAG);
            case FAILURE -> new FontIcon(Icons.ERROR);
        });

        var style = switch (event.type()) {
            case INFO -> Styles.ACCENT;
            case SUCCESS -> Styles.SUCCESS;
            case WARNING -> Styles.WARNING;
            case FAILURE -> Styles.DANGER;
            default -> "";
        };

        notification.getStyleClass().addAll(Styles.INTERACTIVE, style);
        notificationPane.addNotification(notification);
    }
}
