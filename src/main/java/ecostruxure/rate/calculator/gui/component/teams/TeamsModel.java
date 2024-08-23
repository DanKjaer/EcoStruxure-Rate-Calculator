package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.be.enums.RateType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class TeamsModel {
    private final ObservableList<TeamItemModel> teams = FXCollections.observableArrayList();
    private final ObservableList<TeamItemModel> teamsToArchive = FXCollections.observableArrayList();
    private final ObjectProperty<TeamItemModel> teamToArchive = new SimpleObjectProperty<>();
    private final StringProperty numTeams = new SimpleStringProperty();
    private final StringProperty search = new SimpleStringProperty("");
    private final ObjectProperty<RateType> selectedRateType = new SimpleObjectProperty<>(RateType.HOURLY);
    private final ObjectProperty<UUID> lastSelectedTeamId = new SimpleObjectProperty<>();
    private final BooleanProperty teamsFetched = new SimpleBooleanProperty();
    private final BooleanProperty shouldReloadTable = new SimpleBooleanProperty();

    private final StringProperty rawColumnName = new SimpleStringProperty("");
    private final StringProperty markupColumnName = new SimpleStringProperty("");
    private final StringProperty grossMarginColumnName = new SimpleStringProperty("");

    public ObservableList<TeamItemModel> teams() {
        return teams;
    }

    public ObservableList<TeamItemModel> teamsToArchive() {
        return teamsToArchive;
    }

    public ObjectProperty<TeamItemModel> teamToArchiveProperty() {
        return teamToArchive;
    }

    public StringProperty numTeamsProperty() {
        return numTeams;
    }

    public StringProperty searchProperty() {
        return search;
    }

    public ObjectProperty<RateType> selectedRateTypeProperty() {
        return selectedRateType;
    }

    public ObjectProperty<UUID> lastSelectedTeamIdProperty() {
        return lastSelectedTeamId;
    }

    public BooleanProperty teamsFetchedProperty() {
        return teamsFetched;
    }

    public BooleanProperty shouldReloadTableProperty() {
        return shouldReloadTable;
    }

    public StringProperty rawColumnNameProperty() {
        return rawColumnName;
    }

    public StringProperty markupColumnNameProperty() {
        return markupColumnName;
    }

    public StringProperty grossMarginColumnNameProperty() {
        return grossMarginColumnName;
    }
}