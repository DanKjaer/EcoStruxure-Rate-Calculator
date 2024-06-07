package ecostruxure.rate.calculator.gui.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CurrencyItemModel {
    private final StringProperty currencyCode = new SimpleStringProperty();
    private final StringProperty eurConversionRate = new SimpleStringProperty();
    private final StringProperty usdConversionRate = new SimpleStringProperty();

    public StringProperty currencyCodeProperty() {
        return currencyCode;
    }

    public StringProperty eurConversionRateProperty() {
        return eurConversionRate;
    }

    public StringProperty usdConversionRateProperty() {
        return usdConversionRate;
    }

    @Override
    public String toString() {
        return currencyCode.get().toUpperCase();
    }
}
