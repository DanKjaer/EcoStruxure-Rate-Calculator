package ecostruxure.rate.calculator.gui.system.modal;

import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.event.Event;

import java.util.UUID;

public record ShowModalEvent(UUID id, ModalController controller, Object data) implements Event {
    public ShowModalEvent(ModalController controller) {
        this(UUID.randomUUID(), controller, null);
    }

    public ShowModalEvent(ModalController controller, Object data) {
        this(UUID.randomUUID(), controller, data);
    }

    @Override
    public UUID id() {
        return id;
    }
}
