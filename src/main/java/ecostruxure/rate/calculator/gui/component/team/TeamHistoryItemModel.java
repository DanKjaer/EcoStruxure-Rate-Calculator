package ecostruxure.rate.calculator.gui.component.team;

import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TeamHistoryItemModel {
    private final IntegerProperty teamId = new SimpleIntegerProperty();
    private final IntegerProperty profileId = new SimpleIntegerProperty();
    private final IntegerProperty profileHistoryId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Reason> reason = new SimpleObjectProperty<>();
    private final FinancialData hourlyRate = new FinancialData();
    private final FinancialData dayRate = new FinancialData();
    private final FinancialData annualCost = new FinancialData();
    private final ObjectProperty<BigDecimal> totalHours = new SimpleObjectProperty<>();
    private final ObservableList<TeamHistoryProfileItemModel> details = FXCollections.observableArrayList();

    public IntegerProperty teamIdProperty() {
        return teamId;
    }

    public IntegerProperty profileIdProperty() {
        return profileId;
    }

    public IntegerProperty profileHistoryIdProperty() {
        return profileHistoryId;
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public ObjectProperty<Reason> reasonProperty() {
        return reason;
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

    public ObservableList<TeamHistoryProfileItemModel> details() {
        return details;
    }
}
