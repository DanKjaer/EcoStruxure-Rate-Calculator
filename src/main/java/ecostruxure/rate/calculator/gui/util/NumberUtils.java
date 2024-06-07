package ecostruxure.rate.calculator.gui.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {
    public static String formatAsInteger(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return String.format("%,d", Math.round(value.doubleValue()));
    }
}