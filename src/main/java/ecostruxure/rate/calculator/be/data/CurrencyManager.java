package ecostruxure.rate.calculator.be.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.kordamp.ikonli.Ikon;

import java.math.BigDecimal;

public class CurrencyManager {
    private static final String EUR_SYMBOL = "€";
    private static final String USD_SYMBOL = "$";

    public enum CurrencyType {
        EUR, USD
    }

    private static final ObjectProperty<CurrencyType> currencyType = new SimpleObjectProperty<>(CurrencyType.EUR);
    private static final ObjectProperty<BigDecimal> conversionRate = new SimpleObjectProperty<>(BigDecimal.ONE);
    private static final ObjectProperty<String> currencySymbol = new SimpleObjectProperty<>(EUR_SYMBOL);

    public static void switchToEUR() {
        currencyType.set(CurrencyType.EUR);
        conversionRate.set(BigDecimal.ONE);
        currencySymbol.set(EUR_SYMBOL);
    }

    public static void switchToUSD(BigDecimal toEurRate) {
        currencyType.set(CurrencyType.USD);
        conversionRate.set(toEurRate);
        currencySymbol.set(USD_SYMBOL);
    }

    public static ObjectProperty<CurrencyType> currencyTypeProperty() {
        return currencyType;
    }

    public static ObjectProperty<BigDecimal> conversionRateProperty() {
        return conversionRate;
    }

    public static ObjectProperty<String> currencySymbolProperty() {
        return currencySymbol;
    }
}
