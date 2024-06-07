package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import javafx.beans.property.*;

public class TeamEditProfileModel {
    private final IntegerProperty profileId = new SimpleIntegerProperty();
    private final IntegerProperty teamId = new SimpleIntegerProperty();
    private final StringProperty profileName = new SimpleStringProperty("");

    private final StringProperty utilizationRate = new SimpleStringProperty("");
    private final BooleanProperty utilizationRateIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty utilizationRateFetched = new SimpleBooleanProperty(false);
    private final StringProperty utilizationHours = new SimpleStringProperty("");
    private final BooleanProperty utilizationHoursIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty utilizationHoursFetched = new SimpleBooleanProperty(false);

    private final StringProperty originalUtilizationRate = new SimpleStringProperty("");
    private final StringProperty originalUtilizationHours = new SimpleStringProperty("");
    private final BooleanProperty okToSave = new SimpleBooleanProperty();

    public IntegerProperty profileIdProperty() {
        return profileId;
    }

    public IntegerProperty teamIdProperty() {
        return teamId;
    }

    public StringProperty profileNameProperty() {
        return profileName;
    }

    public StringProperty utilizationRateProperty() {
        return utilizationRate;
    }

    public BooleanProperty utilizationRateIsValidProperty() {
        return utilizationRateIsValid;
    }

    public BooleanProperty utilizationRateFetchedProperty() {
        return utilizationRateFetched;
    }

    public StringProperty utilizationHoursProperty() {
        return utilizationHours;
    }

    public BooleanProperty utilizationHoursIsValidProperty() {
        return utilizationHoursIsValid;
    }

    public BooleanProperty utilizationHoursFetchedProperty() {
        return utilizationHoursFetched;
    }

    public StringProperty originalUtilizationRateProperty() {
        return originalUtilizationRate;
    }

    public StringProperty originalUtilizationHoursProperty() {
        return originalUtilizationHours;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSave;
    }


}
