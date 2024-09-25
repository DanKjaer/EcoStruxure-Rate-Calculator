package ecostruxure.rate.calculator.gui.component.modals.addteam;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.UUID;

public class AddProfileItemModel {
    private final ObjectProperty<UUID> uuid = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> annualCostAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> annualHourAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> setCostAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> currentCostAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> setHourAllocation = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> currentHourAllocation = new SimpleObjectProperty<>();

    private final BooleanProperty utilizationPercentageIsValidProperty = new SimpleBooleanProperty();
    private final ObjectProperty<UUID> teamId = new SimpleObjectProperty<>();

    private final StringProperty location = new SimpleStringProperty("");
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final BooleanProperty wasChanged = new SimpleBooleanProperty();



    public ObjectProperty<UUID> UUIDProperty() {
        return uuid;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> annualCostAllocationProperty() {
        return annualCostAllocation;
    }

    public ObjectProperty<BigDecimal> annualHourAllocationProperty() {
        return annualHourAllocation;
    }

    public ObjectProperty<BigDecimal> setCostAllocationProperty() {
        return setCostAllocation;
    }

    public ObjectProperty<BigDecimal> currentCostAllocationProperty() {
        return currentCostAllocation;
    }

    public ObjectProperty<BigDecimal> setHourAllocationProperty() {
        return setHourAllocation;
    }

    public ObjectProperty<BigDecimal> currentHourAllocationProperty() {
        return currentHourAllocation;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public ObjectProperty<UUID> teamIdProperty() {
        return teamId;
    }

    public BooleanProperty utilizationPercentageIsValidProperty() {
        return utilizationPercentageIsValidProperty;
    }

    public BooleanProperty wasChangedProperty() {
        return wasChanged;
    }

    @Override
    public String toString() {
        return name.get() + " - " + location.get();
    }
}
