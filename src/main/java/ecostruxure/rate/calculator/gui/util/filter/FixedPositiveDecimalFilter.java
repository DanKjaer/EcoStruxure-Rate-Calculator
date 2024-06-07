package ecostruxure.rate.calculator.gui.util.filter;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

import java.util.function.UnaryOperator;

public class FixedPositiveDecimalFilter implements UnaryOperator<TextFormatter.Change> {
    private final int decimalPlaces;
    private final double minValue;
    private final double maxValue;

    public FixedPositiveDecimalFilter(int decimalPlaces, double minValue, double maxValue) {
        this.decimalPlaces = decimalPlaces;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Constructor for a filter that only allows positive numbers - numbers greater than 0.
     */
    public FixedPositiveDecimalFilter(int decimalPlaces, double maxValue) {
        this(decimalPlaces, 0, maxValue);
    }

    @Override
    public Change apply(Change change) {
        String newText = change.getControlNewText();

        if (!newText.matches("-?\\d*(\\.\\d{0," + decimalPlaces + "})?")) return null;

        try {
            double value = Double.parseDouble(newText);
            if (value < minValue || value > maxValue) return null;
        } catch (NumberFormatException e) {
            if (!newText.isEmpty()) return null;
        }

        int decimalPos = newText.indexOf(".");
        if (change.getText().equals(".") && decimalPos != -1) {
            change.setCaretPosition(decimalPos + 1);
            return change;
        }

        if (decimalPos != -1) {
            String decimalPart = newText.substring(decimalPos + 1);
            if (decimalPart.length() > decimalPlaces) return null;
        }

        return change;
    }
}
