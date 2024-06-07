package ecostruxure.rate.calculator.gui.system.currency;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

public class FinancialData {
    private final ObjectProperty<BigDecimal> originalAmountEUR = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> convertedAmount = new SimpleObjectProperty<>();

    public FinancialData() {
        this(BigDecimal.ZERO);
    }

    public FinancialData(BigDecimal amount) {
        this.originalAmountEUR.set(amount);

        convertedAmount.bind(Bindings.createObjectBinding(() -> {
            BigDecimal rate = CurrencyManager.conversionRateProperty().get();
            BigDecimal originalAmount = originalAmountEUR.get();

            if (rate == null || originalAmount == null) {
                return BigDecimal.ZERO;
            } else {
                return originalAmount.multiply(rate);
            }
        }, originalAmountEUR, CurrencyManager.conversionRateProperty()));
    }

    public BigDecimal amount() {
        return convertedAmount.get();
    }

    public void amount(BigDecimal amount) {
        originalAmountEUR.set(amount);
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return convertedAmount;
    }
}
