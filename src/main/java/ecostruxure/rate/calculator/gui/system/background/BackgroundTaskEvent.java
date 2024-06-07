package ecostruxure.rate.calculator.gui.system.background;

import ecostruxure.rate.calculator.gui.system.event.Event;
import javafx.concurrent.Task;

import java.util.UUID;

public record BackgroundTaskEvent<T>(UUID id, Task<T> task) implements Event {
    public BackgroundTaskEvent(Task<T> task) {
        this(UUID.randomUUID(), task);
    }

    @Override
    public UUID id() {
        return id;
    }
}
