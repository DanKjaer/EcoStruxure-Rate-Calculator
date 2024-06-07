package ecostruxure.rate.calculator.gui.component.modals.teamedit;

import javafx.beans.property.*;

public class TeamEditModel {
    private final IntegerProperty teamId = new SimpleIntegerProperty();
    private final StringProperty teamName = new SimpleStringProperty();
    private final StringProperty newName = new SimpleStringProperty();
    private final BooleanProperty newNameValid = new SimpleBooleanProperty();
    private final BooleanProperty teamFetchedProperty = new SimpleBooleanProperty();
    private final BooleanProperty okToSaveProperty = new SimpleBooleanProperty();

    public IntegerProperty teamIdProperty() {
        return teamId;
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
