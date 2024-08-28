package ecostruxure.rate.calculator.gui.common;

import javafx.scene.layout.Region;

import java.util.UUID;

public interface Controller {
    void activate(Object data);
    Region view();
}
