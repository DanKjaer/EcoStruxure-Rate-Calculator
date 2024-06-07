package ecostruxure.rate.calculator.gui.component.modals.addteam;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class AddProfileItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> setRateUtilization = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> currentRateUtilization = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> setHourUtilization = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> currentHourUtilization = new SimpleObjectProperty<>();

    private final BooleanProperty utilizationPercentageIsValidProperty = new SimpleBooleanProperty();
    private final IntegerProperty teamId = new SimpleIntegerProperty();

    private final StringProperty location = new SimpleStringProperty("");
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final BooleanProperty wasChanged = new SimpleBooleanProperty();

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> setRateUtilizationProperty() {
        return setRateUtilization;
    }

    public ObjectProperty<BigDecimal> currentRateUtilizationProperty() {
        return currentRateUtilization;
    }

    public ObjectProperty<BigDecimal> setHourUtilizationProperty() {
        return setHourUtilization;
    }

    public ObjectProperty<BigDecimal> currentHourUtilizationProperty() {
        return currentHourUtilization;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public IntegerProperty teamIdProperty() {
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
