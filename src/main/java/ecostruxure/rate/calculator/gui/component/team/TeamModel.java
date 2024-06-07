package ecostruxure.rate.calculator.gui.component.team;

import ecostruxure.rate.calculator.gui.component.geography.IGeographyItemModel;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TeamModel {
    public enum TeamTableType {
        PROFILE,
        HISTORY
    }

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty teamName = new SimpleStringProperty();
    private final ObservableList<ProfileItemModel> profiles = FXCollections.observableArrayList();
    private final ObservableList<IGeographyItemModel> geographies = FXCollections.observableArrayList();
    private final ObservableList<TeamHistoryItemModel> history = FXCollections.observableArrayList();
    private final ObservableList<TeamHistoryProfileItemModel> historyDetails = FXCollections.observableArrayList();
    private final StringProperty numProfiles = new SimpleStringProperty();
    private final StringProperty search = new SimpleStringProperty("");
    private final BooleanProperty profilesFetched = new SimpleBooleanProperty(false);
    private final BooleanProperty archived = new SimpleBooleanProperty();
    private final StringProperty searchGeographies = new SimpleStringProperty("");

    private final FinancialData rawRate = new FinancialData();
    private final FinancialData markupRate = new FinancialData();
    private final FinancialData grossMarginRate = new FinancialData();
    private final StringProperty totalHours = new SimpleStringProperty();

    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<TeamTableType> tableType = new SimpleObjectProperty<>(TeamTableType.PROFILE);
    private final ObjectProperty<LocalDateTime> currentDate = new SimpleObjectProperty<>();
    private final ObjectProperty<TeamHistoryItemModel> selectedHistoryItem = new SimpleObjectProperty<>();
    private final BooleanProperty historySelected = new SimpleBooleanProperty();

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty teamNameProperty() {
        return teamName;
    }

    public ObservableList<ProfileItemModel> profiles() {
        return profiles;
    }

    public ObservableList<TeamHistoryItemModel> history() {
        return history;
    }

    public ObservableList<TeamHistoryProfileItemModel> historyDetails() {
        return historyDetails;
    }

    public StringProperty numProfilesProperty() {
        return numProfiles;
    }

    public StringProperty searchProperty() {
        return search;
    }

    public BooleanProperty profilesFetchedProperty() {
        return profilesFetched;
    }

    public ObservableList<IGeographyItemModel> geographies() {
        return geographies;
    }

    public BooleanProperty archivedProperty() {
        return archived;
    }

    public StringProperty searchGeographiesProperty() {
        return searchGeographies;
    }

    public ObjectProperty<TeamTableType> selectedTableTypeProperty() {
        return tableType;
    }

    public ObjectProperty<BigDecimal> rawRateProperty() {
        return rawRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> markupRateProperty() {
        return markupRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> grossMarginRateProperty() {
        return grossMarginRate.amountProperty();
    }

    public StringProperty totalHoursProperty() {
        return totalHours;
    }

    public void setRawRate(BigDecimal rawRate) {
        this.rawRate.amount(rawRate);
    }

    public void setMarkupRate(BigDecimal markupRate) {
        this.markupRate.amount(markupRate);
    }

    public void setGrossMarginRate(BigDecimal grossMarginRate) {
        this.grossMarginRate.amount(grossMarginRate);
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public ObjectProperty<LocalDateTime> currentDateProperty() {
        return currentDate;
    }

    public ObjectProperty<TeamHistoryItemModel> selectedHistoryItemProperty() {
        return selectedHistoryItem;
    }

    public BooleanProperty historySelectedProperty() {
        return historySelected;
    }
}