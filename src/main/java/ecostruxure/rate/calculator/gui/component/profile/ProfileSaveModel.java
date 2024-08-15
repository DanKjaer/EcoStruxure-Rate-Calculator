package ecostruxure.rate.calculator.gui.component.profile;

import ecostruxure.rate.calculator.be.enums.CurrencyEnum;
import ecostruxure.rate.calculator.be.enums.ResourceType;
import ecostruxure.rate.calculator.gui.component.modals.addprofile.AddProfileGeographyItemModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProfileSaveModel {
    private final ObservableList<AddProfileGeographyItemModel> locations = FXCollections.observableArrayList();

    private final StringProperty name = new SimpleStringProperty("");
    private final ObjectProperty<AddProfileGeographyItemModel> selectedGeography = new SimpleObjectProperty<>();
    private final ObjectProperty<ResourceType> selectedResourceType = new SimpleObjectProperty<>(ResourceType.OVERHEAD);
    private final StringProperty annualSalary = new SimpleStringProperty("");
    private final StringProperty annualTotalHours = new SimpleStringProperty("");
    private final StringProperty effectiveWorkHours = new SimpleStringProperty("");
    private final StringProperty effectiveness = new SimpleStringProperty("");
    private final StringProperty hoursPerDay = new SimpleStringProperty("8");

    private final ObjectProperty<CurrencyEnum> currency = new SimpleObjectProperty<>(CurrencyEnum.USD);

    private final BooleanProperty nameIsValid  = new SimpleBooleanProperty(false);
    private final BooleanProperty selectedGeographyIsValid = new SimpleBooleanProperty(selectedGeography.get() != null);
    private final BooleanProperty annualSalaryIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty annualTotalHoursIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty effectiveWorkHoursIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty effectivenessIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty hoursPerDayIsValid = new SimpleBooleanProperty(true);
    private final BooleanProperty disableFields = new SimpleBooleanProperty(false);

    public StringProperty nameProperty() {
        return name;
    }

    public ObservableList<AddProfileGeographyItemModel> locations() {
        return locations;
    }

    public ObjectProperty<AddProfileGeographyItemModel> selectedGeographyProperty() {
        return selectedGeography;
    }

    public ObjectProperty<ResourceType> selectedResourceTypeProperty() {
        return selectedResourceType;
    }

    public StringProperty hoursPerDayProperty() {
        return hoursPerDay;
    }

    public StringProperty annualSalaryProperty() {
        return annualSalary;
    }

    public StringProperty annualTotalHoursProperty() {
        return annualTotalHours;
    }

    public StringProperty effectiveWorkHoursProperty() {
        return effectiveWorkHours;
    }

    public StringProperty effectivenessProperty() {
        return effectiveness;
    }

    public ObjectProperty<CurrencyEnum> currencyProperty() {
        return currency;
    }

    public BooleanProperty nameIsValidProperty() {
        return nameIsValid;
    }

    public BooleanProperty selectedGeographyIsValidProperty() {
        return selectedGeographyIsValid;
    }

    public BooleanProperty annualSalaryIsValidProperty() {
        return annualSalaryIsValid;
    }

    public BooleanProperty annualTotalHoursIsValidProperty() {
        return annualTotalHoursIsValid;
    }

    public BooleanProperty effectiveWorkHoursIsValidProperty(){
        return effectiveWorkHoursIsValid;
    }

    public BooleanProperty effectivenessIsValidProperty() {
        return effectivenessIsValid;
    }

    public BooleanProperty hoursPerDayIsValidProperty() {
        return hoursPerDayIsValid;
    }

    public BooleanProperty disableFieldsProperty() {
        return disableFields;
    }
}