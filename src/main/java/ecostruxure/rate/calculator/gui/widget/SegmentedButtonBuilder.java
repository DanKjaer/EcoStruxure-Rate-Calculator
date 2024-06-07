package ecostruxure.rate.calculator.gui.widget;

import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.SegmentedButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentedButtonBuilder<T extends Enum<T>> {
    private final List<ToggleButton> buttons = new ArrayList<>();
    private final Map<ToggleButton, T> buttonMappings = new HashMap<>();
    private ObjectProperty<T> selectedProperty;

    public SegmentedButtonBuilder<T> withSelectedProperty(ObjectProperty<T> selectedProperty) {
        this.selectedProperty = selectedProperty;
        return this;
    }

    public SegmentedButtonBuilder<T> addButton(ObservableStringValue text, T enumValue) {
        var button = new ToggleButton();
        button.textProperty().bind(text);

        button.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                selectedProperty.set(buttonMappings.get(button));
                button.getStyleClass().remove(CssClasses.ACTIONABLE);
            } else {
                button.getStyleClass().add(CssClasses.ACTIONABLE);
            }
        });

        button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (button.isSelected()) event.consume();
        });

        buttons.add(button);
        buttonMappings.put(button, enumValue);
        return this;
    }

    public SegmentedButton build() {
        var segmentedButton = new SegmentedButton(FXCollections.observableArrayList(buttons));
        selectedProperty.addListener((obs, ov, nv) -> {
            for (Map.Entry<ToggleButton, T> entry : buttonMappings.entrySet()) {
                entry.getKey().setSelected(entry.getValue().equals(nv));
            }
        });

        if (!buttonMappings.isEmpty()) {
            buttons.getFirst().setSelected(true);
            for (int i = 1; i < buttons.size(); i++) {
                buttons.get(i).getStyleClass().add(CssClasses.ACTIONABLE);
            }
        }
        return segmentedButton;
    }
}
