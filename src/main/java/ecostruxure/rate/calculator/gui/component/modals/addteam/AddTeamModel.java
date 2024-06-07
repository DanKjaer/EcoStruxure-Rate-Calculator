package ecostruxure.rate.calculator.gui.component.modals.addteam;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddTeamModel {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty nameIsValid = new SimpleBooleanProperty();
    private final ObservableList<AddProfileItemModel> profiles = FXCollections.observableArrayList();
    private final BooleanProperty profilesFetched = new SimpleBooleanProperty();
    private final ObservableList<AddProfileItemModel> selectedProfiles = FXCollections.observableArrayList();
    private final StringProperty searchProperty = new SimpleStringProperty("");
    private final BooleanProperty okToAdd = new SimpleBooleanProperty();

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty nameIsValidProperty() {
        return nameIsValid;
    }

    public ObservableList<AddProfileItemModel> profiles() {
        return profiles;
    }

    public BooleanProperty profilesFetchedProperty() {
        return profilesFetched;
    }

    public ObservableList<AddProfileItemModel> selectedProfiles() {
        return selectedProfiles;
    }

    public BooleanProperty okToAddProperty() {
        return okToAdd;
    }

    public StringProperty searchProperty() {
        return searchProperty;
    }
}
