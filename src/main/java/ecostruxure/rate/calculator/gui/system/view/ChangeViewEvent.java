package ecostruxure.rate.calculator.gui.system.view;

import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.system.event.Event;

import java.util.UUID;

public record ChangeViewEvent(UUID id, Class<? extends Controller> target, Object data) implements Event {
    public ChangeViewEvent(Class<? extends Controller> target) {
        this(UUID.randomUUID(), target, null);
    }

    public ChangeViewEvent(Class<? extends Controller> target, Object data) {
        this(UUID.randomUUID(), target, data);
    }

    @Override
    public UUID id() {
        return id;
    }
}
