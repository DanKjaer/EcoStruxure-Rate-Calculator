package ecostruxure.rate.calculator.gui.component.profile;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class ProfileTeamItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> utilizationCost = new SimpleObjectProperty<>();
    private final FinancialData hourlyRate = new FinancialData();
    private final FinancialData dayRate = new FinancialData();
    private final FinancialData annualCost = new FinancialData();
    private final ObjectProperty<BigDecimal> utilizationHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> annualTotalHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveWorkHours = new SimpleObjectProperty<>();

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> utilizationCostProperty() {
        return utilizationCost;
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

    public ObjectProperty<BigDecimal> utilizationHoursProperty() {
        return utilizationHours;
    }

    public ObjectProperty<BigDecimal> annualTotalHoursProperty() {
        return annualTotalHours;
    }

    public ObjectProperty<BigDecimal> effectiveWorkHoursProperty(){
        return effectiveWorkHours;
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

    public void setEffectiveWorkHours(BigDecimal effectiveWorkHours){
        this.effectiveWorkHours.set(effectiveWorkHours);
    }
}
