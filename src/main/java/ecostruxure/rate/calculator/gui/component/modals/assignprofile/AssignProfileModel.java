package ecostruxure.rate.calculator.gui.component.modals.assignprofile;

import ecostruxure.rate.calculator.gui.component.modals.addteam.AddProfileItemModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class AssignProfileModel {
    private final ObservableList<AddProfileItemModel> profiles = FXCollections.observableArrayList();
    private final ObservableList<AddProfileItemModel> originalProfiles = FXCollections.observableArrayList();
    private static UUID teamId;
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

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        AssignProfileModel.teamId = teamId;
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
