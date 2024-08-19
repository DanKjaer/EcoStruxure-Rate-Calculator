package ecostruxure.rate.calculator.gui.component.profile;

import ecostruxure.rate.calculator.be.enums.ResourceType;
import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProfileHistoryItemModel {
    private static UUID id;
    private final ObjectProperty<ResourceType> resourceType = new SimpleObjectProperty<>();
    private final FinancialData annualSalary = new FinancialData();
    private final ObjectProperty<BigDecimal> effectiveness = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveWorkHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> hoursPerDay = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final FinancialData hourlyRate = new FinancialData();
    private final FinancialData dayRate = new FinancialData();
    private final FinancialData annualCost = new FinancialData();

    public UUID getUUID() {
        return id;
    }

    public void setIdProperty(UUID uuid) {
        ProfileHistoryItemModel.id = uuid;
    }

    public ObjectProperty<ResourceType> resourceTypeProperty() {
        return resourceType;
    }

    public ObjectProperty<BigDecimal> annualSalaryProperty() {
        return annualSalary.amountProperty();
    }

    public ObjectProperty<BigDecimal> effectivenessProperty() {
        return effectiveness;
    }

    public ObjectProperty<BigDecimal> totalHoursProperty() {
        return totalHours;
    }

    public ObjectProperty<BigDecimal> effectiveWorkHoursProperty() {
        return effectiveWorkHours;
    }
    public ObjectProperty<BigDecimal> hoursPerDayProperty() {
        return hoursPerDay;
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
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

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary.amount(annualSalary);
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
}
