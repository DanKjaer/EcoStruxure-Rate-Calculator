package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class TeamItemModel {
    private final ObjectProperty<UUID> teamId = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> markup = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> grossMargin = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> hourlyRate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> dayRate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalAllocatedCost = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalAllocatedHours = new SimpleObjectProperty<>();
    private final BooleanProperty archived = new SimpleBooleanProperty();
    private final ObjectProperty<Timestamp> updatedAt = new SimpleObjectProperty<>();

    //Ikke sikker på hvad de 3 rates er
    private final FinancialData rawRate = new FinancialData(BigDecimal.ZERO);
    private final FinancialData markupRate = new FinancialData(BigDecimal.ZERO);
    private final FinancialData grossMarginRate = new FinancialData(BigDecimal.ZERO);

    public ObjectProperty<UUID> teamIdProperty() {
        return teamId;
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

    public ObjectProperty<BigDecimal> hourlyRateProperty() {
        return hourlyRate;
    }

    public ObjectProperty<BigDecimal> dayRateProperty() {
        return dayRate;
    }

    public ObjectProperty<BigDecimal> totalAllocatedCostProperty() {
        return totalAllocatedCost;
    }

    public ObjectProperty<BigDecimal> totalAllocatedHoursProperty() {
        return totalAllocatedHours;
    }

    public ObjectProperty<Timestamp> updatedAtProperty() {
        return updatedAt;
    }
}
