package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.be.enums.RateType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TeamsModel {
    private final ObservableList<TeamItemModel> teams = FXCollections.observableArrayList();
    private final ObservableList<TeamItemModel> teamsToArchive = FXCollections.observableArrayList();
    private final ObjectProperty<TeamItemModel> teamToArchive = new SimpleObjectProperty<>();
    private final StringProperty numTeams = new SimpleStringProperty();
    private final StringProperty search = new SimpleStringProperty("");
    private final ObjectProperty<RateType> selectedRateType = new SimpleObjectProperty<>(RateType.HOURLY);
    private final IntegerProperty lastSelectedTeamId = new SimpleIntegerProperty();
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

    public IntegerProperty lastSelectedTeamIdProperty() {
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