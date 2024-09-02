package ecostruxure.rate.calculator.gui.component.team;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.UUID;

public class TeamHistoryProfileItemModel {
    private final ObjectProperty<UUID> profileId = new SimpleObjectProperty();
    private final ObjectProperty<UUID> profileHistoryId = new SimpleObjectProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> costAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> hourAllocation = new SimpleObjectProperty<>();
    private final FinancialData hourlyRate = new FinancialData();
    private final FinancialData dayRate = new FinancialData();
    private final FinancialData annualCost = new FinancialData();
    private final ObjectProperty<BigDecimal> totalHours = new SimpleObjectProperty<>();

    public ObjectProperty<UUID> profileIdProperty() {
        return profileId;
    }

    public ObjectProperty<UUID> profileHistoryIdProperty() {
        return profileHistoryId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> costAllocationProperty() {
        return costAllocation;
    }

    public ObjectProperty<BigDecimal> hourAllocationProperty() {
        return hourAllocation;
    }

    public ObjectProperty<BigDecimal> hourlyRateProperty() {
        return hourlyRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> dayRateProperty() {
        return dayRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> annualCostProperty() {
        return annualCost.amountProperty();
    }

    public ObjectProperty<BigDecimal> totalHoursProperty() {
        return totalHours;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate.amount(hourlyRate);
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate.amount(dayRate);
    }

    public void setAnnualCost(BigDecimal annualCost) {
        this.annualCost.amount(annualCost);
    }

    @Override
    public String toString() {
        return "TeamHistoryProfileItemModel{" +
                "profileId=" + profileId.get() +
                ", profileHistoryId=" + profileHistoryId.get() +
                ", costAllocation=" + costAllocation.get() +
                ", hourAllocation=" + hourAllocation.get() +
                '}';
    }
}
