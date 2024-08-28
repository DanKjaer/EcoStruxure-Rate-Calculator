package ecostruxure.rate.calculator.gui.component.profile;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.UUID;

public class ProfileTeamItemModel {
    private final ObjectProperty<UUID> teamId = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> profileId = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final FinancialData hourlyRate = new FinancialData();
    private final FinancialData dayRate = new FinancialData();
    private final FinancialData annualCost = new FinancialData();
    private final ObjectProperty<BigDecimal> annualTotalCost = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> costAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> hourAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> annualTotalHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveWorkHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> allocatedCostOnTeam = new SimpleObjectProperty<>();

    public ObjectProperty<UUID> teamIdProperty() {
        return teamId;
    }

    public ObjectProperty<UUID> profileIdProperty() {
        return profileId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> costAllocationProperty() {
        return costAllocation;
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

    public ObjectProperty<BigDecimal> annualTotalCostProperty() {
        return annualTotalCost;
    }

    public ObjectProperty<BigDecimal> hourAllocationProperty() {
        return hourAllocation;
    }

    public ObjectProperty<BigDecimal> annualTotalHoursProperty() {
        return annualTotalHours;
    }

    public ObjectProperty<BigDecimal> effectiveWorkHoursProperty() {
        return effectiveWorkHours;
    }

    public ObjectProperty<BigDecimal> allocatedCostOnTeamProperty() {
        return allocatedCostOnTeam;
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

    public void setEffectiveWorkHours(BigDecimal effectiveWorkHours) {
        this.effectiveWorkHours.set(effectiveWorkHours);
    }
}
