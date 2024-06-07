package ecostruxure.rate.calculator.gui.component.profile;


import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProfileModel {
    public enum ProfileTableType {
        TEAM,
        HISTORY
    }

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty profileName = new SimpleStringProperty();
    private final StringProperty location = new SimpleStringProperty();
    private final ProfileSaveModel saveModel = new ProfileSaveModel();
    private final ObservableList<ProfileTeamItemModel> teams = FXCollections.observableArrayList();
    private final ObservableList<ProfileHistoryItemModel> history = FXCollections.observableArrayList();
    private final StringProperty contributedHours = new SimpleStringProperty();
    private final FinancialData totalHourlyRate = new FinancialData();
    private final FinancialData totalDayRate = new FinancialData();
    private final FinancialData totalAnnualCost = new FinancialData();
    private final ObjectProperty<ProfileTableType> selectedTableType = new SimpleObjectProperty<>(ProfileTableType.TEAM);
    private final ObjectProperty<LocalDateTime> currentDate = new SimpleObjectProperty<>();
    private final ObjectProperty<ProfileHistoryItemModel> selectedHistoryItem = new SimpleObjectProperty<>();
    private final BooleanProperty historySelected = new SimpleBooleanProperty();
    private final BooleanProperty okToSave = new SimpleBooleanProperty();
    private final BooleanProperty okToUndo = new SimpleBooleanProperty();
    private final BooleanProperty okToCheckout = new SimpleBooleanProperty();

    private final StringProperty searchTeam = new SimpleStringProperty("");
    private final BooleanProperty archived = new SimpleBooleanProperty();

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty profileName() {
        return profileName;
    }

    public StringProperty location() {
        return location;
    }
    
    public ProfileSaveModel saveModel() {
        return saveModel;
    }
    public ObservableList<ProfileTeamItemModel> teams() {
        return teams;
    }

    public ObservableList<ProfileHistoryItemModel> history() {
        return history;
    }

    public StringProperty contributedHours() {
        return contributedHours;
    }

    public ObjectProperty<BigDecimal> totalHourlyRate() {
        return totalHourlyRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> totalDayRate() {
        return totalDayRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> totalAnnualCost() {
        return totalAnnualCost.amountProperty();
    }

    public ObjectProperty<ProfileTableType> selectedTableTypeProperty() {
        return selectedTableType;
    }

    public ObjectProperty<LocalDateTime> currentDateProperty() {
        return currentDate;
    }

    public ObjectProperty<ProfileHistoryItemModel> selectedHistoryItemProperty() {
        return selectedHistoryItem;
    }

    public BooleanProperty historySelectedProperty() {
        return historySelected;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSave;
    }

    public BooleanProperty okToUndoProperty() {
        return okToUndo;
    }

    public BooleanProperty okToCheckoutProperty() { return okToCheckout; }

    public void setTotalHourlyRate(BigDecimal totalHourlyRate) {
        this.totalHourlyRate.amount(totalHourlyRate);
    }

    public void setTotalDayRate(BigDecimal totalDayRate) {
        this.totalDayRate.amount(totalDayRate);
    }

    public void setTotalAnnualCost(BigDecimal totalAnnualCost) {
        this.totalAnnualCost.amount(totalAnnualCost);
    }

    public StringProperty searchTeamProperty() {
        return searchTeam;
    }

    public BooleanProperty archivedProperty() {
        return archived;
    }
}