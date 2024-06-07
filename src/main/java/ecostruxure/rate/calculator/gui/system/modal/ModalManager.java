package ecostruxure.rate.calculator.gui.system.modal;

import ecostruxure.rate.calculator.gui.system.event.EventBus;

public class ModalManager {
    private final ModalOverlay overlay = new ModalOverlay();

    public ModalManager(EventBus eventBus) {
        eventBus.subscribe(ShowModalEvent.class, event -> {
            if (event.data() != null) event.controller().activate(event.data());
            overlay.show(new ModalDialog(eventBus, event.controller()));
        });
        eventBus.subscribe(HideModalEvent.class, event -> overlay.hide());
    }

    public ModalOverlay overlay() {
        return overlay;
    }
}
