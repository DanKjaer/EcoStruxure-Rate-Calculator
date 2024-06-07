package ecostruxure.rate.calculator.gui.component.team;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class TeamHistoryProfileItemModel {
    private final IntegerProperty profileId = new SimpleIntegerProperty();
    private final IntegerProperty profileHistoryId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> utilizationRate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> utilizationHours = new SimpleObjectProperty<>();
    private final FinancialData hourlyRate = new FinancialData();
    private final FinancialData dayRate = new FinancialData();
    private final FinancialData annualCost = new FinancialData();
    private final ObjectProperty<BigDecimal> totalHours = new SimpleObjectProperty<>();

    public IntegerProperty profileIdProperty() {
        return profileId;
    }

    public IntegerProperty profileHistoryIdProperty() {
        return profileHistoryId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> utilizationRateProperty() {
        return utilizationRate;
    }

    public ObjectProperty<BigDecimal> utilizationHoursProperty() {
        return utilizationHours;
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
                ", utilizationRate=" + utilizationRate.get() +
                ", utilizationHours=" + utilizationHours.get() +
                '}';
    }
}
