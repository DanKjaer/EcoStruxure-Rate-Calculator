package ecostruxure.rate.calculator.gui.component.modals.verifyprofiles;

import ecostruxure.rate.calculator.gui.component.modals.addteam.AddProfileItemModel;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class VerifyProfilesModel {
    private final ObservableList<AddProfileItemModel> profiles = FXCollections.observableArrayList();
    private final ObservableList<AddProfileItemModel> originalProfiles = FXCollections.observableArrayList();

    private static UUID teamId;
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

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        VerifyProfilesModel.teamId = teamId;
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
