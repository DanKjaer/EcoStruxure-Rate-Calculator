package ecostruxure.rate.calculator.gui.util;

import ecostruxure.rate.calculator.gui.common.CurrencyItemModel;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyFormatter;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BindingsUtils {
    public static StringBinding createCurrencyStringBinding(ObjectProperty<BigDecimal> property) {
        return Bindings.createStringBinding(() ->
                        CurrencyFormatter.formatCompactCurrency(property.get(), CurrencyManager.currencySymbolProperty().get()),
                property, CurrencyManager.currencySymbolProperty());
    }

    public static StringBinding createIntegerStringBinding(StringProperty property) {
        return Bindings.createStringBinding(() -> {
            if (property.get() == null || property.get().isEmpty()) {
                return "0";
            }
            try {
                return NumberUtils.formatAsInteger(new BigDecimal(property.get()));
            } catch (NumberFormatException e) {
                return "0";
            }
        }, property);
    }

    public static StringBinding createStringBindingWithDefault(ObjectProperty<CurrencyItemModel> currencyProperty, String defaultValue) {
        return Bindings.createStringBinding(() -> {
            if (currencyProperty.get() == null || currencyProperty.get().currencyCodeProperty() == null) {
                return defaultValue;
            } else {
                return currencyProperty.get().currencyCodeProperty().get();
            }
        }, currencyProperty);
    }

    public static StringBinding localDateTimeBinding(ObjectProperty<LocalDateTime> dateTimeProperty,
                                                     DateTimeFormatter formatter) {
        return Bindings.createStringBinding(() -> {
            LocalDateTime dateTime = dateTimeProperty.get();
            if (dateTime != null) {
                return dateTime.format(formatter);
            } else {
                return "";
            }
        }, dateTimeProperty);
    }

    public static StringBinding formattedDataTimeBinding(ObjectProperty<LocalDateTime> dateTimeProperty, StringProperty textProperty) {
        return Bindings.createStringBinding(() -> {
            LocalDateTime dateTime = dateTimeProperty.get();
            if (dateTime != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm").withLocale(LocalizedText.CURRENT_LOCALE.get());
                return textProperty.get() + dateTime.format(formatter);
            }
            return "";
        }, dateTimeProperty, textProperty);
    }
}
