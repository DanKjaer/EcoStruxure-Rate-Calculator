package ecostruxure.rate.calculator.gui.system.notification;

import atlantafx.base.controls.Notification;
import atlantafx.base.util.Animations;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class NotificationPane extends StackPane {
    private static final Duration DEFAULT_ANIMATION_DURATION = Duration.millis(250);
    private static final Duration DEFAULT_DELAY = Duration.seconds(5);
    private static final int MAX_NOTIFICATIONS = 5;

    private final VBox notificationBox = new VBox(LayoutConstants.STANDARD_SPACING);

    public NotificationPane() {
        setAlignment(notificationBox, Pos.TOP_RIGHT);
        setPickOnBounds(false);
        notificationBox.setPickOnBounds(false);
        getChildren().add(notificationBox);
        notificationBox.setAlignment(Pos.TOP_RIGHT);
        setMargin(notificationBox, new Insets(0, LayoutConstants.STANDARD_PADDING, 0, 0));
    }

    public void addNotification(Notification notification) {
        if (notificationBox.getChildren().size() >= MAX_NOTIFICATIONS) {
            notificationBox.getChildren().removeFirst();
        }

        configureNotification(notification);
        notificationBox.getChildren().add(notification);
    }

    public void yPosition(double yPos) {
        StackPane.setMargin(notificationBox, new Insets(yPos, LayoutConstants.STANDARD_PADDING, 0, 0));
    }

    private void configureNotification(Notification notification) {
        notification.widthProperty().addListener((obs, ov, nv) -> {
            double translateX = nv.doubleValue();
            notification.setTranslateX(translateX);

            var slideIn = new TranslateTransition(DEFAULT_ANIMATION_DURATION, notification);
            slideIn.setFromX(translateX);
            slideIn.setToX(0);
            slideIn.play();
        });

        notification.setOnClose(e -> dismissNotification(notification));

        var delay = new PauseTransition(DEFAULT_DELAY);
        delay.setOnFinished(e -> dismissNotification(notification));
        delay.play();
    }

    private void dismissNotification(Notification notification) {
        var out = Animations.slideOutRight(notification, DEFAULT_ANIMATION_DURATION);
        out.setOnFinished(f -> notificationBox.getChildren().remove(notification));
        out.playFromStart();
    }
}
