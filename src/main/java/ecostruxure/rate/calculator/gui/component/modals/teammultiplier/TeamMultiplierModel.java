package ecostruxure.rate.calculator.gui.component.modals.teammultiplier;

import javafx.beans.property.*;

import java.util.UUID;

public class TeamMultiplierModel {
    private static UUID teamId;
    private final StringProperty markup = new SimpleStringProperty("");
    private final BooleanProperty markupIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty markupFetched = new SimpleBooleanProperty(false);
    private final StringProperty grossMargin = new SimpleStringProperty("");
    private final BooleanProperty grossMarginIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty grossMarginFetched = new SimpleBooleanProperty(false);
    private final BooleanProperty okToSave = new SimpleBooleanProperty(false);

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        TeamMultiplierModel.teamId = teamId;
    }

    public StringProperty markupProperty() {
        return markup;
    }

    public BooleanProperty markupIsValidProperty() {
        return markupIsValid;
    }

    public BooleanProperty markupFetchedProperty() {
        return markupFetched;
    }

    public StringProperty grossMarginProperty() {
        return grossMargin;
    }

    public BooleanProperty grossMarginIsValidProperty() {
        return grossMarginIsValid;
    }

    public BooleanProperty grossMarginFetchedProperty() {
        return grossMarginFetched;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSave;
    }
}