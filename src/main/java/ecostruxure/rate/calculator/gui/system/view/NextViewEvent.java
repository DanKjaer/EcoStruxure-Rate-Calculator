package ecostruxure.rate.calculator.gui.system.view;

import ecostruxure.rate.calculator.gui.system.event.Event;

import java.util.UUID;

public record NextViewEvent(UUID id) implements Event {
    public NextViewEvent() {
        this(UUID.randomUUID());
    }

    @Override
    public UUID id() {
        return id;
    }
}
