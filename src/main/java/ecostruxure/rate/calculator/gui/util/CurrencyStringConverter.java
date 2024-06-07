package ecostruxure.rate.calculator.gui.util;

import ecostruxure.rate.calculator.gui.common.CurrencyItemModel;
import javafx.util.StringConverter;

public class CurrencyStringConverter extends StringConverter<CurrencyItemModel> {
    @Override
    public String toString(CurrencyItemModel item) {
        if (item == null) return "";
        return item.currencyCodeProperty().get().toUpperCase() + " (EUR: " + item.eurConversionRateProperty().get() + ", USD: " + item.usdConversionRateProperty().get() + ")";
    }

    @Override
    public CurrencyItemModel fromString(String string) {
        return null;
    }
}
