package ecostruxure.rate.calculator.gui.component.profiles;

import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.system.currency.FinancialData;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;

public class ProfilesModel {
    private final ObservableList<ProfileItemModel> profiles = FXCollections.observableArrayList();
    private final ObservableList<ProfileItemModel> profilesToArchive = FXCollections.observableArrayList();
    private final ObjectProperty<ProfileItemModel> profileToArchive = new SimpleObjectProperty<>();
    private final StringProperty numProfiles = new SimpleStringProperty();
    private final FinancialData totalHourlyRate = new FinancialData();
    private final FinancialData totalDayRate = new FinancialData();
    private final FinancialData totalAnnualRate = new FinancialData();
    private final StringProperty uniqueGeographies = new SimpleStringProperty();
    private final StringProperty search = new SimpleStringProperty("");
    private final IntegerProperty lastSelectedTeamId = new SimpleIntegerProperty();


    public ObservableList<ProfileItemModel> profiles() {
        return profiles;
    }

    public ObservableList<ProfileItemModel> profilesToArchive() {
        return profilesToArchive;
    }

    public ObjectProperty<ProfileItemModel> profileToArchive() {
        return profileToArchive;
    }

    public StringProperty numProfiles() {
        return numProfiles;
    }

    public ObjectProperty<BigDecimal> totalHourlyRate() {
        return totalHourlyRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> totalDayRate() {
        return totalDayRate.amountProperty();
    }

    public ObjectProperty<BigDecimal> totalAnnualRate() {
        return totalAnnualRate.amountProperty();
    }

    public StringProperty uniqueGeographies() {
        return uniqueGeographies;
    }

    public StringProperty searchProperty() {
        return search;
    }

    public IntegerProperty lastSelectedTeamIdProperty() {
        return lastSelectedTeamId;
    }

    public void setTotalHourlyRate(BigDecimal totalHourlyRate) {
        this.totalHourlyRate.amount(totalHourlyRate);
    }

    public void setTotalDayRate(BigDecimal totalDayRate) {
        this.totalDayRate.amount(totalDayRate);
    }

    public void setTotalAnnualRate(BigDecimal totalAnnualRate) {
        this.totalAnnualRate.amount(totalAnnualRate);
    }
}