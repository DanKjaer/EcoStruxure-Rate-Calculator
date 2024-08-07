package ecostruxure.rate.calculator.gui.system.currency;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

/**
 * FinancialData er en klasse til at holde finansielle data, som beløb i euro samt det konverterede beløb.
 */
public class FinancialData {
    // Property til at holde det originale beløb i EUR
    private final ObjectProperty<BigDecimal> originalAmountEUR = new SimpleObjectProperty<>();
    // Property til at holde det konverterede beløb
    private final ObjectProperty<BigDecimal> convertedAmount = new SimpleObjectProperty<>();

    // Default constructor til at initialize beløbet til 0
    public FinancialData() {
        this(BigDecimal.ZERO);
    }

    // Constructor til at initialize beløbet til et specifikt beløb
    public FinancialData(BigDecimal amount) {
        // Sætter det originale beløb i euro
        this.originalAmountEUR.set(amount);
        // Binder det konverterede beløb til en udregning baseret på conversion raten
        convertedAmount.bind(Bindings.createObjectBinding(() -> {
            // Henter conversion raten
            BigDecimal rate = CurrencyManager.conversionRateProperty().get();
            // Henter det originale beløb i euro
            BigDecimal originalAmount = originalAmountEUR.get();

            // Hvis conversion raten eller det originale beløb er null, returner 0
            if (rate == null || originalAmount == null) {
                return BigDecimal.ZERO;
            } else {
                // Returner det konverterede beløb
                return originalAmount.multiply(rate);
            }
        }, originalAmountEUR, CurrencyManager.conversionRateProperty()));
    }
    // Metode til at få det konverterede beløb
    public BigDecimal amount() {
        return convertedAmount.get();
    }
    // Metode til at sætte det originale beløb
    public void amount(BigDecimal amount) {
        originalAmountEUR.set(amount);
    }
    // Metode til at få det konverterede beløb som en ObjectProperty
    public ObjectProperty<BigDecimal> amountProperty() {
        return convertedAmount;
    }
}
