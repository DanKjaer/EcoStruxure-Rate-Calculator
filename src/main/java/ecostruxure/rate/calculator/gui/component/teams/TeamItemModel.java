package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class TeamItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty("");
    private final FinancialData rawRate = new FinancialData(BigDecimal.ZERO);
    private final FinancialData markupRate = new FinancialData(BigDecimal.ZERO);
    private final FinancialData grossMarginRate = new FinancialData(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> markup = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> grossMargin = new SimpleObjectProperty<>();
    private final BooleanProperty archived = new SimpleBooleanProperty();

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> rawRateProperty() {
        return rawRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> markupRateProperty() {
        return markupRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> grossMarginRateProperty() {
        return grossMarginRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> markupProperty() {
        return markup;
    }

    public ObjectProperty<BigDecimal> grossMarginProperty() {
        return grossMargin;
    }

    public BooleanProperty archivedProperty() {
        return archived;
    }

    public void setRawRate(BigDecimal rawRate) {
        this.rawRate.amount(rawRate);
    }

    public void setMarkupRate(BigDecimal markupRate) {
        this.markupRate.amount(markupRate);
    }

    public void setGrossMarginRate(BigDecimal grossMarginRate) {
        this.grossMarginRate.amount(grossMarginRate);
    }
}
