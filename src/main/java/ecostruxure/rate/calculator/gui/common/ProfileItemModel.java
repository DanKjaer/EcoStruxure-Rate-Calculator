package ecostruxure.rate.calculator.gui.common;

import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;



public class ProfileItemModel {
    private static UUID uuid;
    private final StringProperty name = new SimpleStringProperty("");
    private final FinancialData annualCost = new FinancialData(BigDecimal.ZERO);
    private final StringProperty currency = new SimpleStringProperty("");
    private final IntegerProperty countryId = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> hoursPerDay = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveness = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> annualHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> effectiveWorkHours = new SimpleObjectProperty<>();
    private final ObjectProperty<Timestamp> updatedAt = new SimpleObjectProperty<>();
    private final BooleanProperty resourceType = new SimpleBooleanProperty();
    private final BooleanProperty archived = new SimpleBooleanProperty();

    //måske slette de 3 nederste her?
    private final ObjectProperty<BigDecimal> allocatedHours = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> allocatedCost = new SimpleObjectProperty<>();
    private final StringProperty location = new SimpleStringProperty("");

    public void setIdProperty(UUID uuid) {
        ProfileItemModel.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public StringProperty nameProperty() {
        return name;
    }


    public ObjectProperty<BigDecimal> annualCostProperty() {
        return annualCost.amountProperty();
    }


    public ObjectProperty<BigDecimal> annualHoursProperty() {
        return annualHours;
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

    public ObjectProperty<BigDecimal> allocatedHoursProperty() {
        return allocatedHours;
    }

    public void setAllocatedHours(BigDecimal allocatedHours) {
        this.allocatedHours.set(allocatedHours);
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

    public static UUID getUuid() {
        return uuid;
    }

    public static void setUuid(UUID uuid) {
        ProfileItemModel.uuid = uuid;
    }

    public String getName() {
        return name.get();
    }

    public FinancialData getAnnualCost() {
        return annualCost;
    }

    public String getCurrency() {
        return currency.get();
    }

    public StringProperty currencyProperty() {
        return currency;
    }

    public int getCountryId() {
        return countryId.get();
    }

    public IntegerProperty countryIdProperty() {
        return countryId;
    }

    public Boolean getResourceType() {
        return resourceType.get();
    }

    public BooleanProperty resourceTypeProperty() {
        return resourceType;
    }

    public BigDecimal getHoursPerDay() {
        return hoursPerDay.get();
    }

    public ObjectProperty<BigDecimal> hoursPerDayProperty() {
        return hoursPerDay;
    }

    public BigDecimal getEffectiveness() {
        return effectiveness.get();
    }

    public BigDecimal getAnnualHours() {
        return annualHours.get();
    }

    public BigDecimal getEffectiveWorkHours() {
        return effectiveWorkHours.get();
    }

    public Timestamp getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<Timestamp> updatedAtProperty() {
        return updatedAt;
    }

    public boolean isArchived() {
        return archived.get();
    }

    public StringProperty locationProperty() {
        return location;
    }
}

//    private final StringProperty teams = new SimpleStringProperty("");


//    private final FinancialData dayRate = new FinancialData(BigDecimal.ZERO);
    //    private final FinancialData hourlyRate = new FinancialData(BigDecimal.ZERO);
//    private final ObjectProperty<BigDecimal> costAllocation = new SimpleObjectProperty<>();
//    private final ObjectProperty<BigDecimal> hourAllocation = new SimpleObjectProperty<>();
//    private final ObjectProperty<BigDecimal> currentHourAllocation = new SimpleObjectProperty<>();


//    public ObjectProperty<BigDecimal> currentHourAllocationProperty() {
//        return currentHourAllocation;
//    }
//
//    public void setCurrentHourAllocation(BigDecimal currentHourAllocation) {
//        this.currentHourAllocation.set(currentHourAllocation);
//    }

//    public void setHourlyRate(BigDecimal hourlyRate) {
//        this.hourlyRate.amount(hourlyRate);
//    }
//
//    public void setDayRate(BigDecimal dayRate) {
//        this.dayRate.amount(dayRate);
//    }

//    public ObjectProperty<BigDecimal> hourlyRateProperty() {
//        return hourlyRate.amountProperty();
//    }
//
//    public ObjectProperty<BigDecimal> dayRateProperty() {
//        return dayRate.amountProperty();
//    }

    //    public ObjectProperty<BigDecimal> costAllocationProperty() {
//        return costAllocation;
//    }
//
//    public ObjectProperty<BigDecimal> hourAllocationProperty() {
//        return hourAllocation;
//    }

    //    public StringProperty teamsProperty() {
//        return teams;
//    }
//

//
//




