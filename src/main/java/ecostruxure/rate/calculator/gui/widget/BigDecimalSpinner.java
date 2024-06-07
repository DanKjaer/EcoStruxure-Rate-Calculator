package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.filter.FixedPositiveDecimalFilter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;

public class BigDecimalSpinner extends HBox {
    private final ObjectProperty<BigDecimal> value = new SimpleObjectProperty<>();
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal step;
    private CustomTextField textField;

    private Timeline incrementTimeline;
    private Timeline decrementTimeline;

    public BigDecimalSpinner(BigDecimal initialValue, BigDecimal min, BigDecimal max, BigDecimal step) {
        this.value.set(initialValue);
        this.min = min;
        this.max = max;
        this.step = step;

        textField = new CustomTextField();
        textField.setMaxWidth(75);
        textField.setMinWidth(75);
        textField.setMinHeight(35);
        textField.setMaxHeight(35);
        textField.setAlignment(Pos.CENTER);

        var filter = new FixedPositiveDecimalFilter(2, max.doubleValue());
        textField.setTextFormatter(new TextFormatter<>(filter));


        textField.textProperty().bindBidirectional(value, new BigDecimalStringConverter());

        HBox buttonsBox = new HBox(textField);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        buttonsBox.setSpacing(2);

        this.getChildren().add(buttonsBox);

        value.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.compareTo(max) > 0)
                    value.set(max);
                else if (newValue.compareTo(min) < 0)
                    value.set(min);
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // focus lost
                validateAndSetValue(textField.getText());
            }
        });
    }

    private void setupButton(Button button, Runnable action, Runnable onPress, Runnable onRelease) {
        button.setOnAction(e -> action.run());
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> onPress.run());
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> onRelease.run());
    }

    private void startIncrementing() {
        if (incrementTimeline == null) {
            incrementTimeline = new Timeline(new KeyFrame(Duration.millis(150), e -> increment()));
            incrementTimeline.setCycleCount(Timeline.INDEFINITE);
        }
        incrementTimeline.playFromStart();
    }

    private void stopIncrementing() {
        if (incrementTimeline != null) {
            incrementTimeline.stop();
        }
    }

    private void startDecrementing() {
        if (decrementTimeline == null) {
            decrementTimeline = new Timeline(new KeyFrame(Duration.millis(150), e -> decrement()));
            decrementTimeline.setCycleCount(Timeline.INDEFINITE);
        }
        decrementTimeline.playFromStart();
    }

    private void stopDecrementing() {
        if (decrementTimeline != null)
            decrementTimeline.stop();
    }

    public BigDecimal getValue() {
        return value.get();
    }

    public void setValue(BigDecimal newValue) {
        value.set(newValue);
    }

    public ObjectProperty<BigDecimal> valueProperty() {
        return value;
    }

    public void setMinimum(BigDecimal min) {
        if (max != null && min.compareTo(max) >= 0)
            return;
            //throw new IllegalArgumentException("Minimum value must be less than maximum value");

        this.min = min;
        if (value.get().compareTo(min) < 0)
            value.set(min);
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getStep() {
        return step;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMaximum(BigDecimal max) {
        if (min != null && max.compareTo(min) < 0)
            return;
            //throw new IllegalArgumentException("Maximum value must be greater than minimum value");

        this.max = max;
        if (value.get().compareTo(max) > 0)
            value.set(max);

        var filter = new FixedPositiveDecimalFilter(2, max.doubleValue());
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    private void increment() {
        BigDecimal newValue = value.get().add(step);
        if (newValue.compareTo(max) <= 0)
            value.set(newValue);
        else
            value.set(max);
    }

    private void decrement() {
        BigDecimal newValue = value.get().subtract(step);
        if (newValue.compareTo(min) >= 0)
            value.set(newValue);
        else
            value.set(min);
    }

    private void validateAndSetValue(String input) {
        try {
            BigDecimal newValue = new BigDecimal(input);
            if (newValue.compareTo(max) > 0) {
                textField.setText(max.toString());
                value.set(max);
            } else if (newValue.compareTo(min) < 0) {
                textField.setText(min.toString());
                value.set(min);
            } else {
                value.set(newValue);
            }
        } catch (NumberFormatException e) {
            // If input is invalid, revert to the last valid value
            textField.setText(value.get().toString());
        }
    }

    public CustomTextField getTextField() {
        return this.textField;
    }

    private class BigDecimalStringConverter extends StringConverter<BigDecimal> {
        @Override
        public String toString(BigDecimal object) {
            return object == null ? "" : object.toString();
        }

        @Override
        public BigDecimal fromString(String string) {
            try {
                BigDecimal value = string == null || string.trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(string);
                if (value.compareTo(max) > 0) {
                    return max;
                } else if (value.compareTo(min) < 0) {
                    return min;
                }
                return value;
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
    }
}