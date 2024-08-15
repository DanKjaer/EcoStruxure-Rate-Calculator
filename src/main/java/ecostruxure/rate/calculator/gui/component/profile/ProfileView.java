package ecostruxure.rate.calculator.gui.component.profile;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.profile.ProfileModel.ProfileTableType;
import ecostruxure.rate.calculator.gui.util.*;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.*;
import ecostruxure.rate.calculator.gui.widget.tables.*;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.CurrencyCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.HourCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.LocalDateTimeCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.PercentageCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.MenuItemInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.SegmentedButton;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.createContextMenu;

public class ProfileView implements View {
    private final ProfileModel model;
    private final Consumer<ProfileTeamItemModel> onShowTeam;
    private final Consumer<Runnable> onSaveProfile;
    private final Consumer<ProfileHistoryItemModel> onSelectHistoryItem;
    private final Consumer<Runnable> onUndoChanges;
    private final Consumer<Runnable> onCheckout;
    private final Runnable onArchiveProfile;
    private final Runnable onUnArchiveProfile;

    private final FilteredList<ProfileTeamItemModel> filteredProfileTeams;
    private final SortedList<ProfileTeamItemModel> tableItems;

    private static final int SEARCH_FIELD_WIDTH = 200;

    public ProfileView(ProfileModel model,
                       Consumer<ProfileTeamItemModel> onShowTeam,
                       Consumer<Runnable> onSaveProfile,
                       Consumer<ProfileHistoryItemModel> onSelectHistoryItem,
                       Consumer<Runnable> onUndoChanges,
                       Consumer<Runnable> onCheckout,
                       Runnable onArchiveProfile,
                       Runnable onUnArchiveProfile) {
        this.model = model;
        this.onShowTeam = onShowTeam;
        this.onSaveProfile = onSaveProfile;
        this.onSelectHistoryItem = onSelectHistoryItem;
        this.onUndoChanges = onUndoChanges;
        this.onCheckout = onCheckout;
        this.onArchiveProfile = onArchiveProfile;
        this.onUnArchiveProfile = onUnArchiveProfile;

        this.filteredProfileTeams = new FilteredList<>(model.teams());
        this.tableItems = new SortedList<>(filteredProfileTeams);

        setupSearchFilter();
    }

    private void setupSearchFilter() {
        model.searchTeamProperty().addListener((obs, ov, nv) -> {
            Predicate<ProfileTeamItemModel> searchPredicate = (ProfileTeamItemModel profileTeamItemModel) -> {
                if (nv == null || nv.isEmpty())
                    return true;

                var lowercase = nv.toLowerCase();
                return profileTeamItemModel.nameProperty().get().toLowerCase().contains(lowercase);
            };

            // sætter begge predicates for at kunne respektere valget i comboboxen
            filteredProfileTeams.setPredicate(searchPredicate);
        });
    }

    private ScrollPane profileScrollPane(Node content){
//        VBox wrapper = new VBox(content);
//        Region spacer = new Region();
//        spacer.setMinHeight(100);
//        wrapper.getChildren().add(spacer);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

    @Override
    public Region build() {
        var results = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setVgrow(results, Priority.ALWAYS);

        var content = new HBox(LayoutConstants.STANDARD_SPACING);

        var saveBtn = createSaveButton();
        var undoBtn = createUndoButton();
        var checkoutBtn = createCheckoutButton();

        NodeUtils.bindVisibility(undoBtn, model.historySelectedProperty().not());
        NodeUtils.bindVisibility(saveBtn, model.historySelectedProperty().not());
        NodeUtils.bindVisibility(checkoutBtn, model.historySelectedProperty());

        var profileBox = new VBox(LayoutConstants.STANDARD_SPACING, createProfileBoxTitle(), new ProfileSaveView(model.saveModel()).build(), new HBox(LayoutConstants.STANDARD_SPACING, new Spacer(), undoBtn, saveBtn, checkoutBtn));
        profileBox.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, CssClasses.PADDING_DEFAULT, Styles.BORDER_SUBTLE);

        var historyTable = createHistoryTable();
        var teamsTable = createTeamsTable();

        NodeUtils.bindVisibility(historyTable, model.selectedTableTypeProperty().isEqualTo(ProfileTableType.HISTORY));
        NodeUtils.bindVisibility(teamsTable, model.selectedTableTypeProperty().isEqualTo(ProfileTableType.TEAM));
        var tableViewStack = new StackPane(teamsTable, historyTable);

        var searchField = Fields.searchFieldWithDelete(SEARCH_FIELD_WIDTH, model.searchTeamProperty());
        searchField.textProperty().bindBidirectional(model.searchTeamProperty());
        searchField.getStyleClass().add(CssClasses.INPUT_GROUP_TEXT);
        NodeUtils.bindVisibility(searchField, model.selectedTableTypeProperty().isEqualTo(ProfileTableType.TEAM));

        var archive = Buttons.actionIconButton(LocalizedText.ARCHIVE, Icons.DELETE, this::shouldArchive, Styles.DANGER);
        archive.graphicProperty().bind(Bindings.when(model.archivedProperty()).then(new FontIcon(Icons.SHOW)).otherwise(new FontIcon(Icons.DELETE)));
        archive.textProperty().bind(Bindings.when(model.archivedProperty()).then(LocalizedText.UNARCHIVE).otherwise(LocalizedText.ARCHIVE));
        model.archivedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!archive.getStyleClass().contains(Styles.SUCCESS)) {
                    archive.getStyleClass().add(Styles.SUCCESS);
                }
                archive.getStyleClass().remove(Styles.DANGER);
            } else {
                if (!archive.getStyleClass().contains(Styles.DANGER)) {
                    archive.getStyleClass().add(Styles.DANGER);
                }
                archive.getStyleClass().remove(Styles.SUCCESS);
            }
        });

        var controls = new HBox(LayoutConstants.STANDARD_SPACING, createSegmentedButton(), new Spacer(), searchField, archive);

        var tableViewBox = new VBox(LayoutConstants.STANDARD_SPACING, controls, tableViewStack);
        tableViewBox.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, CssClasses.PADDING_DEFAULT, Styles.BORDER_SUBTLE);

        content.getChildren().addAll(tableViewBox, profileBox);
        VBox.setMargin(content, new Insets(LayoutConstants.CONTENT_PADDING));
        HBox.setHgrow(tableViewBox, Priority.ALWAYS);
        VBox.setVgrow(profileBox, Priority.ALWAYS);
        VBox.setVgrow(tableViewStack, Priority.ALWAYS);
        VBox.setVgrow(content, Priority.ALWAYS);

        results.getChildren().addAll(createHeader(), createStats(), content);

        return profileScrollPane(results);
    }

    private void shouldArchive(ActionEvent actionEvent) {
        if (model.archivedProperty().get())
            onUnArchiveProfile.run();
        else
            onArchiveProfile.run();
    }

    private Node createProfileBoxTitle() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm").withLocale(Locale.ENGLISH);
        StringBinding titleBinding = Bindings.createStringBinding(() -> {
            if (model.historySelectedProperty().get() && model.selectedHistoryItemProperty().get() != null) {
                var updatedAt = model.selectedHistoryItemProperty().get().updatedAtProperty().get();
                return "History - " + updatedAt.format(formatter);
            } else {
                return "Current";
            }
        }, model.historySelectedProperty(), model.selectedHistoryItemProperty());

        return Labels.title4(titleBinding);
    }

    private Node createCheckoutButton() {
        var checkoutBtn = new Button(null);
        checkoutBtn.textProperty().bind(LocalizedText.CHECKOUT);
        checkoutBtn.getStyleClass().addAll(Styles.ACCENT, CssClasses.ACTIONABLE);
        checkoutBtn.disableProperty().bind(model.okToCheckoutProperty().not());
        checkoutBtn.setOnAction(event -> {
            checkoutBtn.disableProperty().unbind();
            checkoutBtn.setDisable(true);
            onCheckout.accept(() -> checkoutBtn.disableProperty().bind(model.okToCheckoutProperty().not()));
        });
        return checkoutBtn;
    }

    private Node createSaveButton() {
        var saveBtn = new Button(null);
        saveBtn.textProperty().bind(LocalizedText.SAVE);
        saveBtn.getStyleClass().addAll(Styles.SUCCESS, CssClasses.ACTIONABLE);
        saveBtn.disableProperty().bind(model.okToSaveProperty().not());
        saveBtn.setOnAction(event -> {
            saveBtn.disableProperty().unbind();
            saveBtn.setDisable(true);
            onSaveProfile.accept(() -> saveBtn.disableProperty().bind(model.okToSaveProperty().not()));
        });
        return saveBtn;
    }

    private Node createUndoButton() {
        var undoBtn = new Button(null);
        undoBtn.textProperty().bind(LocalizedText.UNDO);
        undoBtn.getStyleClass().addAll(Styles.DANGER, CssClasses.ACTIONABLE);
        undoBtn.disableProperty().bind(model.okToUndoProperty().not());
        undoBtn.setOnAction(event -> {
            undoBtn.disableProperty().unbind();
            undoBtn.setDisable(true);
            onUndoChanges.accept(() -> undoBtn.disableProperty().bind(model.okToUndoProperty().not()));
        });

        return undoBtn;
    }

    private Region createHeader() {
        var header = new HBox(LayoutConstants.STANDARD_SPACING);

        var pageHeader = Informative.pageHeader(
                model.profileName(),
                model.location(),
                Icons.PROFILES
        );

        var teamsStats = Informative.headerStats(
                LocalizedText.TEAMS,
                Bindings.size(model.teams()).asString(),
                CssClasses.BLUE,
                true
        );

        header.getChildren().addAll(pageHeader, new Spacer(), teamsStats);
        return header;
    }

    private Region createStats() {
        var stats = Informative.statsContainer();
        stats.getChildren().addAll(
                Informative.statsBox(LocalizedText.CONTRIBUTED_HOURLY_RATE, BindingsUtils.createCurrencyStringBinding(model.totalHourlyRate()), CssClasses.BLUE, Feather.BAR_CHART),
                Informative.statsBox(LocalizedText.CONTRIBUTED_DAY_RATE, BindingsUtils.createCurrencyStringBinding(model.totalDayRate()), CssClasses.GREEN, Feather.BAR_CHART_2),
                Informative.statsBox(LocalizedText.CONTRIBUTED_ANNUAL_COST, BindingsUtils.createCurrencyStringBinding(model.totalAnnualCost()), CssClasses.ORANGE, Feather.PIE_CHART),
                Informative.statsBox(LocalizedText.ANNUAL_HOURS, BindingsUtils.createIntegerStringBinding(model.contributedHours()), CssClasses.RED, Feather.CLOCK)
        );

        return stats;
    }

    private SegmentedButton createSegmentedButton() {
        return new SegmentedButtonBuilder<ProfileTableType>()
                .withSelectedProperty(model.selectedTableTypeProperty())
                .addButton(LocalizedText.TEAMS, ProfileTableType.TEAM)
                .addButton(LocalizedText.HISTORY, ProfileTableType.HISTORY)
                .build();
    }

    private Region createTeamsTable() {
        CustomTableView<ProfileTeamItemModel> customTableView = new CustomTableView<>();
        TableColumn<ProfileTeamItemModel, String> nameColumn = customTableView.createColumn(LocalizedText.NAME, ProfileTeamItemModel::nameProperty);

        TableColumn<ProfileTeamItemModel, BigDecimal> utilizationHours = customTableView.createColumn(LocalizedText.UTILIZATION_HOURS, ProfileTeamItemModel::utilizationHoursProperty, new PercentageCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> annualTotalHours = customTableView.createColumn(LocalizedText.ANNUAL_TOTAL_HOURS, ProfileTeamItemModel::annualTotalHoursProperty, new HourCellFactory<>());

        TableColumn<ProfileTeamItemModel, BigDecimal> utilization = customTableView.createColumn(LocalizedText.UTILIZATION_RATE, ProfileTeamItemModel::utilizationCostProperty, new PercentageCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> hourlyRate = customTableView.createColumn(LocalizedText.HOURLY_RATE, ProfileTeamItemModel::hourlyRateProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> dayRate = customTableView.createColumn(LocalizedText.DAY_RATE, ProfileTeamItemModel::dayRateProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> annualCost = customTableView.createColumn(LocalizedText.ANNUAL_COST, ProfileTeamItemModel::annualCostProperty, new CurrencyCellFactory<>());


        TableView<ProfileTeamItemModel> tableView = customTableView.createCustomTableView(Arrays.asList(nameColumn, utilizationHours, annualTotalHours, utilization, hourlyRate, dayRate, annualCost));
        tableView.getStyleClass().add(Styles.STRIPED);

        tableItems.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(filteredProfileTeams);

        List<MenuItemInfo<ProfileTeamItemModel>> menuItemInfos = List.of(new MenuItemInfo<>(Icons.SHOW, LocalizedText.SHOW, onShowTeam));
        ContextMenu contextMenu = createContextMenu(menuItemInfos, tableView);


        Runnable doubleClickAction = () -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onShowTeam.accept(tableView.getSelectionModel().getSelectedItem());
            }
        };

        customTableView.addRowClickHandler(tableView, contextMenu, doubleClickAction);

        TableColumn<ProfileTeamItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableView, contextMenu);

        optionsColumn.setMaxWidth(50);
        optionsColumn.setMinWidth(50);
        tableView.getColumns().add(optionsColumn);

        return tableView;
    }

    private Region createHistoryTable() {
        CustomTableView<ProfileHistoryItemModel> customTableView = new CustomTableView<>();
        TableColumn<ProfileHistoryItemModel, LocalDateTime> dateColumn = customTableView.createColumn(LocalizedText.DATE, ProfileHistoryItemModel::updatedAtProperty, new LocalDateTimeCellFactory<>(model.currentDateProperty()));
        TableColumn<ProfileHistoryItemModel, BigDecimal> hourlyRateColumn = customTableView.createColumn(LocalizedText.HOURLY_RATE, ProfileHistoryItemModel::hourlyRateProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileHistoryItemModel, BigDecimal> dayRateColumn = customTableView.createColumn(LocalizedText.DAY_RATE, ProfileHistoryItemModel::dayRateProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileHistoryItemModel, BigDecimal> annualCostColumn = customTableView.createColumn(LocalizedText.ANNUAL_COST, ProfileHistoryItemModel::annualCostProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileHistoryItemModel, BigDecimal> annualHoursColumn = customTableView.createColumn(LocalizedText.HOURS, ProfileHistoryItemModel::totalHoursProperty, new HourCellFactory<>());

        TableView<ProfileHistoryItemModel> tableView = customTableView.createCustomTableView(Arrays.asList(dateColumn, annualHoursColumn, hourlyRateColumn, dayRateColumn, annualCostColumn));
        tableView.getStyleClass().add(Styles.STRIPED);

        var sortedHistory = new SortedList<>(model.history());
        sortedHistory.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedHistory);

        List<MenuItemInfo<ProfileHistoryItemModel>> menuItemInfos = List.of(new MenuItemInfo<>(Icons.TEAMS, LocalizedText.SHOW, profileHistoryItemModel -> {}));
        ContextMenu contextMenu = CustomContextMenu.createContextMenu(menuItemInfos, tableView);


        Runnable doubleClickAction = () -> {
            var history = tableView.getSelectionModel().getSelectedItem();
            if (history != null) {
                model.selectedHistoryItemProperty().set(history);
                onSelectHistoryItem.accept(history);
            }
        };

        customTableView.addRowClickHandler(tableView, contextMenu, doubleClickAction);

        TableColumn<ProfileHistoryItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableView, contextMenu);

        optionsColumn.setMaxWidth(50);
        optionsColumn.setMinWidth(50);
        tableView.getColumns().add(optionsColumn);
        return tableView;
    }


}