package ecostruxure.rate.calculator.gui.system.modal;

import atlantafx.base.controls.ModalPane;
import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Wrapper for ModalPane to fix animations only happening once during its lifetime.
 */
public class ModalOverlay extends ModalPane {
    public ModalOverlay() {
        super();
        // Animate when clicking bg to close
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node target = event.getTarget() instanceof Node ? (Node) event.getTarget() : null;

            if (target instanceof StackPane && target.getStyleClass().contains("scrollable-content")) {
                event.consume();
                hide();
            }
        });
    }

    @Override
    public void show(Node node) {
        Animation inAnimation = inTransitionFactory.get().apply(node);
        super.show(node);
        inAnimation.play();
    }

    @Override
    public void hide() {
        if (getContent() == null) return;
        Animation outAnimation = outTransitionFactory.get().apply(getContent());
        outAnimation.setOnFinished(evt -> super.hide(true));
        outAnimation.play();
    }

    @Override
    public void hide(boolean clear) {
        hide();
    }
}