package ecostruxure.rate.calculator.gui.system.modal;

import ecostruxure.rate.calculator.gui.system.event.Event;

import java.util.UUID;

public record HideModalEvent(UUID id) implements Event {
    public HideModalEvent() {
        this(UUID.randomUUID());
    }

    @Override
    public UUID id() {
        return id;
    }
}
