package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import javafx.beans.property.*;

public class TeamEditProfileModel {
    private final IntegerProperty profileId = new SimpleIntegerProperty();
    private final IntegerProperty teamId = new SimpleIntegerProperty();
    private final StringProperty profileName = new SimpleStringProperty("");

    private final StringProperty costAllocation = new SimpleStringProperty("");
    private final BooleanProperty costAllocationIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty costAllocationFetched = new SimpleBooleanProperty(false);
    private final StringProperty hourAllocation = new SimpleStringProperty("");
    private final BooleanProperty hourAllocationIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty hourAllocationFetched = new SimpleBooleanProperty(false);

    private final StringProperty originalCostAllocation = new SimpleStringProperty("");
    private final StringProperty originalhourAllocation = new SimpleStringProperty("");
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

    public StringProperty costAllocationProperty() {
        return costAllocation;
    }

    public BooleanProperty costAllocationIsValidProperty() {
        return costAllocationIsValid;
    }

    public BooleanProperty costAllocationFetchedProperty() {
        return costAllocationFetched;
    }

    public StringProperty hourAllocationProperty() {
        return hourAllocation;
    }

    public BooleanProperty hourAllocationIsValidProperty() {
        return hourAllocationIsValid;
    }

    public BooleanProperty hourAllocationFetchedProperty() {
        return hourAllocationFetched;
    }

    public StringProperty originalCostAllocationProperty() {
        return originalCostAllocation;
    }

    public StringProperty originalhourAllocationProperty() {
        return originalhourAllocation;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSave;
    }


}
