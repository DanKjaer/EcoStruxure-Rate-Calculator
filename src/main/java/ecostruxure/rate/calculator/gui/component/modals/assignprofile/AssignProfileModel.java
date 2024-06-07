package ecostruxure.rate.calculator.gui.component.modals.assignprofile;

import ecostruxure.rate.calculator.gui.component.modals.addteam.AddProfileItemModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AssignProfileModel {
    private final ObservableList<AddProfileItemModel> profiles = FXCollections.observableArrayList();
    private final ObservableList<AddProfileItemModel> originalProfiles = FXCollections.observableArrayList();
    private final IntegerProperty teamId = new SimpleIntegerProperty();
    private final StringProperty teamName = new SimpleStringProperty();
    private final BooleanProperty profilesFetchedProperty = new SimpleBooleanProperty();
    private final StringProperty searchProperty = new SimpleStringProperty("");
    private final BooleanProperty okToSave = new SimpleBooleanProperty();
    private final BooleanProperty saveInProgress = new SimpleBooleanProperty();

    public ObservableList<AddProfileItemModel> profiles() {
        return profiles;
    }

    public ObservableList<AddProfileItemModel> originalProfiles() {
        return originalProfiles;
    }

    public IntegerProperty teamIdProperty() {
        return teamId;
    }

    public StringProperty teamNameProperty() {
        return teamName;
    }

    public BooleanProperty profilesFetchedProperty() {
        return profilesFetchedProperty;
    }

    public StringProperty searchProperty() {
        return searchProperty;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSave;
    }

}
