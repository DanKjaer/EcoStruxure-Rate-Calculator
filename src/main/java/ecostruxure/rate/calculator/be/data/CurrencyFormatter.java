package ecostruxure.rate.calculator.be.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyFormatter {
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat largeValueFormat = new DecimalFormat("#,##0.0", symbols);
    private static final DecimalFormat smallValueFormat = new DecimalFormat("#,##0", symbols);

    public static String formatCurrency(BigDecimal amount) {
        return CurrencyManager.currencySymbolProperty().get() + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String formatCompactCurrency(BigDecimal amount, String currencySymbol) {
        if (amount == null) {
            return currencySymbol + "0";
        }

        double doubleValue = amount.doubleValue();
        String formattedValue;
        if (doubleValue >= 1_000_000_000) {
            formattedValue = largeValueFormat.format(doubleValue / 1_000_000_000) + "B"; // Billions
        } else if (doubleValue >= 1_000_000) {
            formattedValue = largeValueFormat.format(doubleValue / 1_000_000) + "M"; // Millions
        } else if (doubleValue >= 1_000) {
            formattedValue = largeValueFormat.format(doubleValue / 1_000) + "K"; // Thousands
        } else {
            formattedValue = smallValueFormat.format(doubleValue);
        }
        return currencySymbol + formattedValue;
    }
}
