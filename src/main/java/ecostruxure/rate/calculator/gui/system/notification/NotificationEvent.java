package ecostruxure.rate.calculator.gui.system.notification;

import ecostruxure.rate.calculator.gui.system.event.Event;
import javafx.beans.value.ObservableStringValue;

import java.util.UUID;

public record NotificationEvent(UUID id, NotificationType type, ObservableStringValue message) implements Event {
    public NotificationEvent(NotificationType type, ObservableStringValue message) {
        this(UUID.randomUUID(), type, message);
    }

    @Override
    public UUID id() {
        return id;
    }
}
