package ecostruxure.rate.calculator.gui.component.modals.verifyprofiles;

import ecostruxure.rate.calculator.gui.component.modals.addteam.AddProfileItemModel;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VerifyProfilesModel {
    private final ObservableList<AddProfileItemModel> profiles = FXCollections.observableArrayList();
    private final ObservableList<AddProfileItemModel> originalProfiles = FXCollections.observableArrayList();

    private final IntegerProperty teamId = new SimpleIntegerProperty();
    private final StringProperty teamName = new SimpleStringProperty();
    private final BooleanProperty okToUnArchive = new SimpleBooleanProperty();
    private final BooleanProperty profilesFetched = new SimpleBooleanProperty();
    private final StringProperty search = new SimpleStringProperty("");

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

    public BooleanProperty okToUnArchiveProperty() {
        return okToUnArchive;
    }

    public BooleanProperty profilesFetchedProperty() {
        return profilesFetched;
    }

    public StringProperty searchProperty() {
        return search;
    }
}
