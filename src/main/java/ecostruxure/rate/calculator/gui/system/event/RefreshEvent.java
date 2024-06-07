package ecostruxure.rate.calculator.gui.system.event;

import ecostruxure.rate.calculator.gui.common.Controller;

import java.util.UUID;

public record RefreshEvent(UUID id, Class<? extends Controller> target) implements Event {
    public RefreshEvent(Class<? extends Controller> target) {
        this(UUID.randomUUID(), target);
    }

    @Override
    public UUID id() {
        return id;
    }
}
