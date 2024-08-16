package ecostruxure.rate.calculator.gui.common;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class ProfileItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty("");
    private final FinancialData dayRate = new FinancialData(BigDecimal.ZERO);
    private final FinancialData hourlyRate = new FinancialData(BigDecimal.ZERO);
    private final FinancialData annualCost = new FinancialData(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> hours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveWorkHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveness = new SimpleObjectProperty<>();
    private final StringProperty teams = new SimpleStringProperty("");
    private final StringProperty location = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> costAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> hourAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> currentHourAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> AllocatedHours = new SimpleObjectProperty<>();
    private final BooleanProperty archived = new SimpleBooleanProperty();


    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
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

    public StringProperty teamsProperty() {
        return teams;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public ObjectProperty<BigDecimal> costAllocationProperty() {
        return costAllocation;
    }

    public ObjectProperty<BigDecimal> hourAllocationProperty() {
        return hourAllocation;
    }

    public ObjectProperty<BigDecimal> allocatedHoursProperty() {
        return AllocatedHours;
    }

    public ObjectProperty<BigDecimal> hoursProperty() {
        return hours;
    }

    public ObjectProperty<BigDecimal> effectiveWorkHoursProperty() {
        return effectiveWorkHours;
    }

    public ObjectProperty<BigDecimal> effectivenessProperty() {
        return effectiveness;
    }

    public BooleanProperty archivedProperty() {
        return archived;
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

    public void setEffectiveness(BigDecimal effectiveness) {
        this.effectiveness.set(effectiveness);
    }

    public void setAllocatedHours(BigDecimal allocatedHours) {
        this.AllocatedHours.set(allocatedHours);
    }

    public ObjectProperty<BigDecimal> currentHourAllocationProperty() {
        return currentHourAllocation;
    }

    public void setCurrentHourAllocation(BigDecimal currentHourAllocation) {
        this.currentHourAllocation.set(currentHourAllocation);
    }
}
