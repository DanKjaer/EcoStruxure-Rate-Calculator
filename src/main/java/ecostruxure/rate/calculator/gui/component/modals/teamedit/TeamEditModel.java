package ecostruxure.rate.calculator.gui.component.modals.teamedit;

import javafx.beans.property.*;

import java.util.UUID;

public class TeamEditModel {
    private static UUID teamId;
    private final StringProperty teamName = new SimpleStringProperty();
    private final StringProperty newName = new SimpleStringProperty();
    private final BooleanProperty newNameValid = new SimpleBooleanProperty();
    private final BooleanProperty teamFetchedProperty = new SimpleBooleanProperty();
    private final BooleanProperty okToSaveProperty = new SimpleBooleanProperty();

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        TeamEditModel.teamId = teamId;
    }

    public StringProperty teamNameProperty() {
        return teamName;
    }

    public StringProperty newNameProperty() {
        return newName;
    }

    public BooleanProperty newNameValidProperty() {
        return newNameValid;
    }

    public BooleanProperty teamFetchedProperty() {
        return teamFetchedProperty;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSaveProperty;
    }
}
