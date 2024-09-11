package ecostruxure.rate.calculator.gui.component.team;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.geography.IGeographyItemModel;
import ecostruxure.rate.calculator.gui.component.profile.ProfileTeamItemModel;
import ecostruxure.rate.calculator.gui.component.team.TeamModel.TeamTableType;
import ecostruxure.rate.calculator.gui.util.*;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Buttons;
import ecostruxure.rate.calculator.gui.widget.CustomComboBox;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Informative;
import ecostruxure.rate.calculator.gui.widget.SegmentedButtonBuilder;
import ecostruxure.rate.calculator.gui.widget.tables.*;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.*;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.MenuItemInfo;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import org.controlsfx.control.SegmentedButton;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.addKeyEventHandler;
import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.createContextMenu;

public class TeamView implements View {
    private final TeamModel model;
    private final Runnable onAdjustMultipliers;
    private final Runnable onAssignProfiles;
    private final Consumer<ProfileTeamItemModel> onTeamEditProfile;
    private final Consumer<ProfileTeamItemModel> onTeamRemoveProfile;
    private final Runnable onTeamRefresh;
    private final Runnable onExportTeam;
    private final Runnable onEditTeam;
    private final Runnable onArchiveTeam;
    private final Runnable onUnarchiveTeam;

    private static final int SEARCH_FIELD_WIDTH = 200;
    private static final int NUM_ITEMS_PER_PAGE = 25;

    private CustomTableView<ProfileTeamItemModel> customTableView;
    private TableView<ProfileTeamItemModel> tableViewWithPagination;

    private CustomTableView<IGeographyItemModel> geographyCustomTableView;
    private TableView<IGeographyItemModel> geographyTableViewWithPagination;

    private CustomTableView<TeamHistoryItemModel> customTableViewHistory;
    private TableView<TeamHistoryItemModel> tableViewWithPaginationHistory;

    private CustomTableView<TeamHistoryProfileItemModel> detailTableView;
    private TableView<TeamHistoryProfileItemModel> detailTableViewWithPagination;

    private FilteredList<ProfileTeamItemModel> filteredProfileItems;
    private SortedList<ProfileTeamItemModel> profileItemModels;

    private FilteredList<IGeographyItemModel> filteredGeographyItems;
    private SortedList<IGeographyItemModel> geographyItemModels;

    private FilteredList<TeamHistoryItemModel> filteredProfileHistoryItems;
    private SortedList<TeamHistoryItemModel> historyProfileItemModels;

    private FilteredList<TeamHistoryProfileItemModel> filteredHistoryDetailItems;
    private SortedList<TeamHistoryProfileItemModel> historyItemModels;

    private final Consumer<ProfileTeamItemModel> onShowProfile;

    public TeamView(TeamModel model, Runnable onAdjustMultipliers, Runnable onAssignProfiles, Consumer<ProfileTeamItemModel> onTeamEditProfile,
                    Consumer<ProfileTeamItemModel> onShowProfile, Consumer<ProfileTeamItemModel> onTeamRemoveProfile, Runnable onTeamRefresh, Runnable onExportTeam,
                    Runnable onEditTeam, Runnable onArchiveTeam, Runnable onUnarchiveTeam) {
        this.model = model;
        this.onAdjustMultipliers = onAdjustMultipliers;
        this.onAssignProfiles = onAssignProfiles;
        this.onTeamEditProfile = onTeamEditProfile;
        this.onTeamRemoveProfile = onTeamRemoveProfile;
        this.onTeamRefresh = onTeamRefresh;
        this.onExportTeam = onExportTeam;
        this.onEditTeam = onEditTeam;
        this.onArchiveTeam = onArchiveTeam;
        this.onUnarchiveTeam = onUnarchiveTeam;

        this.filteredProfileItems = new FilteredList<>(model.teamProfilesProperty());
        this.profileItemModels = new SortedList<>(filteredProfileItems);

        this.filteredGeographyItems = new FilteredList<>(model.geographies());
        this.geographyItemModels = new SortedList<>(filteredGeographyItems);

        this.filteredProfileHistoryItems = new FilteredList<>(model.history());
        this.historyProfileItemModels = new SortedList<>(filteredProfileHistoryItems);

        this.filteredHistoryDetailItems = new FilteredList<>(model.historyDetails());
        this.historyItemModels = new SortedList<>(filteredHistoryDetailItems);

        model.profilesFetchedProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                Platform.runLater(() -> {
                    tableViewWithPagination.refresh();
                    customTableView.setData(model.teamProfilesProperty());
                    geographyCustomTableView.setData(model.geographies());
                    customTableViewHistory.setData(model.history());

                    detailTableView.setData(model.historyDetails());
                });
            }
        });
        this.onShowProfile = onShowProfile;

        setupSearchFilter();
        setupGeographySearchFilter();
    }

    private void setupSearchFilter() {
        model.searchProperty().addListener((obs, ov, nv) -> {
            Predicate<ProfileTeamItemModel> searchPredicate = (ProfileTeamItemModel ProfileTeamItemModel) -> {
                if (nv == null || nv.isEmpty())
                    return true;

                var lowercase = nv.toLowerCase();
                return ProfileTeamItemModel.nameProperty().get().toLowerCase().contains(lowercase);
            };

            filteredProfileItems.setPredicate(searchPredicate);
            customTableView.setData(filteredProfileItems);
        });
    }

    private void setupGeographySearchFilter() {
        model.searchGeographiesProperty().addListener((obs, ov, nv) -> {
            Predicate<IGeographyItemModel> searchPredicate = (IGeographyItemModel geographyItemModel) -> {
                if (nv == null || nv.isEmpty())
                    return true;

                var lowercase = nv.toLowerCase();
                return geographyItemModel.nameProperty().get().toLowerCase().contains(lowercase);
            };

            filteredGeographyItems.setPredicate(searchPredicate);
            geographyCustomTableView.setData(filteredGeographyItems);
        });
    }

    @Override
    public Region build() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        NodeUtils.bindVisibility(progressIndicator, model.profilesFetchedProperty().not());

        var results = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setVgrow(results, Priority.ALWAYS);

        var header = new HBox(
                Informative.pageHeader(model.teamNameProperty(), LocalizedText.PAGE_TEAM_DESCRIPTION, Icons.TEAMS),
                new Spacer(),
                Informative.headerStats(LocalizedText.PROFILES, model.numProfilesProperty(), CssClasses.BLUE, true)
        );

        var stats = Informative.statsContainer();
        stats.getChildren().addAll(
                Informative.statsBox(LocalizedText.RAW_HOURLY_RATE, BindingsUtils.createCurrencyStringBinding(model.rawRateProperty()), CssClasses.BLUE, CarbonIcons.CHART_BAR),
                Informative.statsBox(LocalizedText.MARKUP_HOURLY_RATE, BindingsUtils.createCurrencyStringBinding(model.markupRateProperty()), CssClasses.GREEN, CarbonIcons.CHART_BAR_FLOATING),
                Informative.statsBox(LocalizedText.GM_HOURLY_RATE, BindingsUtils.createCurrencyStringBinding(model.grossMarginRateProperty()), CssClasses.ORANGE, CarbonIcons.CHART_BUBBLE),
                Informative.statsBox(LocalizedText.TOTAL_HOURS_ANNUALLY, BindingsUtils.createIntegerStringBinding(model.totalHoursProperty()), CssClasses.RED, Feather.CLOCK)
        );


        var content = new VBox(LayoutConstants.STANDARD_SPACING);
        content.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));


//        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm").withLocale(Locale.ENGLISH);
//
//        StringBinding combinedStringBinding = Bindings.createStringBinding(
//                () -> {
//                    LocalDateTime updatedAt = model.updatedAtProperty().get();
//                    return updatedAt != null ? LocalizedText.LAST_EDIT.get() + updatedAt.format(FORMATTER) : "";
//                },
//                LocalizedText.LAST_EDIT, model.updatedAtProperty()
//        );

        var lastEditLabel = new Label("", new FontIcon(Icons.CLOCK));
        lastEditLabel.textProperty().bind(BindingsUtils.formattedDataTimeBinding(model.updatedAtProperty(), LocalizedText.LAST_EDIT));
        lastEditLabel.getStyleClass().add(Styles.TEXT_SUBTLE);
        lastEditLabel.visibleProperty().bind(model.updatedAtProperty().isNotNull());

        var multiplier = Buttons.actionIconButton(LocalizedText.ADJUST_MULTIPLIERS, Icons.MARKUP, event -> onAdjustMultipliers.run(), Styles.ACCENT);
        var assignProfiles = Buttons.actionIconButton(LocalizedText.ASSIGN_PROFILES, Icons.PERSON_ADD, event -> onAssignProfiles.run(), Styles.ACCENT);
        var toFile = Buttons.actionIconButton(LocalizedText.EXPORT_TO_FILE, Icons.EXPORT, event -> onExportTeam.run(), Styles.ACCENT);
        var archive = Buttons.actionIconButton(LocalizedText.ARCHIVE, Icons.DELETE, this::shouldArchive, Styles.DANGER);
        var edit = Buttons.actionIconButton(LocalizedText.EDIT_TEAM, Icons.EDIT, event -> onEditTeam.run(), Styles.ACCENT);

        multiplier.setFocusTraversable(false);
        assignProfiles.setFocusTraversable(false);
        toFile.setFocusTraversable(false);
        archive.setFocusTraversable(false);
        edit.setFocusTraversable(false);

        multiplier.disableProperty().bind(model.archivedProperty());
        assignProfiles.disableProperty().bind(model.archivedProperty());

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

        /* profiles table view */
        var searchField = Fields.searchFieldWithDelete(SEARCH_FIELD_WIDTH, model.searchProperty());
        searchField.textProperty().bindBidirectional(model.searchProperty());

        var comboRows = CustomComboBox.rowsPerPageComboBox();
        comboRows.setOnAction(e -> {
            customTableView.setItemsPerPage(comboRows.getValue());
            customTableViewHistory.setItemsPerPage(comboRows.getValue());
        });

        var tableViewHeaderBottom = new HBox(LayoutConstants.STANDARD_SPACING, comboRows, createSegmentedButton(), new Spacer(), searchField);
        var tableViewHeader = new VBox(LayoutConstants.STANDARD_SPACING, tableViewHeaderBottom);

        var profileTableView = profileCreateTable(comboRows.getValue());
        NodeUtils.bindVisibility(profileTableView, model.profilesFetchedProperty());

        var historyTableView = profileCreateHistoryTable(comboRows.getValue());
        var tableViewStack = new StackPane(profileTableView, historyTableView);

        var buttonContainer = new HBox(LayoutConstants.STANDARD_SPACING, lastEditLabel, new Spacer(), assignProfiles, multiplier, new Separator(Orientation.VERTICAL), toFile, edit, archive);
        buttonContainer.setAlignment(Pos.BOTTOM_LEFT);
        /* geographies table view */
        var searchFieldGeography = Fields.searchFieldWithDelete(SEARCH_FIELD_WIDTH, model.searchGeographiesProperty());
        searchFieldGeography.textProperty().bindBidirectional(model.searchGeographiesProperty());

        var geographyTableViewHeaderBottom = new HBox(new Spacer(), searchFieldGeography);
        var geographyTableViewHeader = new VBox(LayoutConstants.STANDARD_SPACING, geographyTableViewHeaderBottom);

        var geographyTableView = geographyCreateTable();
        NodeUtils.bindVisibility(geographyTableView, model.profilesFetchedProperty());

        var detailTableView = historyDetailTable(25);

        var detailsStack = new StackPane(geographyTableView, detailTableView);

        VBox profileTable = new VBox(LayoutConstants.STANDARD_SPACING, tableViewHeader, tableViewStack);
        VBox geographyTable = new VBox(LayoutConstants.STANDARD_SPACING, geographyTableViewHeader, detailsStack);

        HBox tables = new HBox(LayoutConstants.STANDARD_SPACING, profileTable, geographyTable);
        NodeUtils.bindVisibility(profileTableView, model.selectedTableTypeProperty().isEqualTo(TeamTableType.PROFILE));
        NodeUtils.bindVisibility(geographyTableView, model.selectedTableTypeProperty().isEqualTo(TeamTableType.PROFILE));
        NodeUtils.bindVisibility(historyTableView, model.selectedTableTypeProperty().isEqualTo(TeamTableType.HISTORY));
        NodeUtils.bindVisibility(detailTableView, model.selectedTableTypeProperty().isEqualTo(TeamTableType.HISTORY));
        HBox.setHgrow(profileTable, Priority.ALWAYS);

        detailTableView.setPrefWidth(800);

        var box = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setMargin(box, new Insets(LayoutConstants.CONTENT_PADDING));

        profileTable.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));
        geographyTable.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));

        geographyTable.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            buttonContainer.setPadding(new Insets(0, newValue.intValue() + (LayoutConstants.STANDARD_PADDING * 2) + 1, 0, 0));
        });

        box.getChildren().addAll(buttonContainer, tables);
        VBox.setVgrow(box, Priority.ALWAYS);

        profileTable.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, Styles.BORDER_SUBTLE);
        geographyTable.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, Styles.BORDER_SUBTLE);

        VBox.setMargin(content, new Insets(LayoutConstants.CONTENT_PADDING));
        VBox.setVgrow(content, Priority.ALWAYS);
        VBox.setVgrow(tables, Priority.ALWAYS);
        VBox.setVgrow(historyTableView, Priority.ALWAYS);
        VBox.setVgrow(tableViewStack, Priority.ALWAYS);
        VBox.setVgrow(detailsStack, Priority.ALWAYS);

        results.getChildren().addAll(header, stats, box);
        return new StackPane(results, progressIndicator);
    }

    private void shouldArchive(ActionEvent actionEvent) {
        if (model.archivedProperty().get())
            onUnarchiveTeam.run();
        else
            onArchiveTeam.run();
    }

    private Pagination profileCreateTable(int itemsPerPage) {
        customTableView = new CustomTableView<>(itemsPerPage, profileItemModels);
        tableViewWithPagination = customTableView.setupPagination(customTableView.createTableView());

        profileConfigureTableColumns();
        profileConfigureContextMenu(tableViewWithPagination);

        tableViewWithPagination.getStyleClass().add(Styles.STRIPED);
        profileItemModels.comparatorProperty().bind(tableViewWithPagination.comparatorProperty());

        Pagination pagination = customTableView.getPagination();
        VBox.setVgrow(tableViewWithPagination, Priority.ALWAYS);
        VBox.setVgrow(pagination, Priority.ALWAYS);

        return pagination;
    }

    private void profileConfigureTableColumns() {
        TableColumn<ProfileTeamItemModel, String> nameColumn = customTableView.createColumn(LocalizedText.NAME, ProfileTeamItemModel::nameProperty);
        nameColumn.setCellFactory(column -> {
            return new TableCell<ProfileTeamItemModel, String>() {
                private final VBox container = new VBox();
                private final Label nameLabel = new Label("");
                private final Label locationLabel = new Label("");
                {
                    locationLabel.getStyleClass().add(Styles.TEXT_SUBTLE);
                    container.getChildren().addAll(nameLabel, locationLabel);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        ProfileTeamItemModel profileItem = getTableView().getItems().get(getIndex());
                        nameLabel.textProperty().bind(profileItem.nameProperty());
                        //locationLabel.textProperty().bind(profileItem.locationProperty());

                        setGraphic(container);
                    }
                }
            };
        });

        TableColumn<ProfileTeamItemModel, BigDecimal> hourAllocationColumn = customTableView.createColumn(LocalizedText.HOUR_ALLOCATION, ProfileTeamItemModel::hourAllocationProperty, new PercentageCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> annualHourAllocationColumn = customTableView.createColumn(LocalizedText.HOURS_ON_TEAM, ProfileTeamItemModel::allocatedHoursOnTeamProperty, new HourCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> costAllocationColumn = customTableView.createColumn(LocalizedText.COST_ALLOCATION, ProfileTeamItemModel::costAllocationProperty, new PercentageCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> allocatedCostOnTeamColumn = customTableView.createColumn(LocalizedText.COST_ON_TEAM, ProfileTeamItemModel::allocatedCostOnTeamProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileTeamItemModel, BigDecimal> dayRateColumn = customTableView.createColumn(LocalizedText.DAY_RATE, ProfileTeamItemModel::dayRateProperty, new CurrencyCellFactory<>());

        customTableView.addColumnsToPagination(Arrays.asList(nameColumn, hourAllocationColumn, annualHourAllocationColumn, costAllocationColumn, allocatedCostOnTeamColumn, dayRateColumn));
    }

    private void profileConfigureContextMenu(TableView<ProfileTeamItemModel> tableView) {
        var edit = new MenuItemInfo<ProfileTeamItemModel>(Icons.EDIT, LocalizedText.EDIT, onTeamEditProfile, new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        var remove = new MenuItemInfo<ProfileTeamItemModel>(Icons.DELETE, LocalizedText.REMOVE, onTeamRemoveProfile, new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        var dividerToHide = new MenuItemInfo<ProfileTeamItemModel>(true);

        List<MenuItemInfo<ProfileTeamItemModel>> menuItemInfos = List.of(
                new MenuItemInfo<ProfileTeamItemModel>(Icons.PROFILES, LocalizedText.SHOW, onShowProfile, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)),
                edit,
                new MenuItemInfo<>(true),
                new MenuItemInfo<ProfileTeamItemModel>(Icons.REFRESH, LocalizedText.REFRESH, this::refreshTable, new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN)),
                dividerToHide,
                remove
        );

        var contextMenu = createContextMenu(menuItemInfos, tableView);
        addKeyEventHandler(tableView, contextMenu);

        NodeUtils.bindVisibility(edit.menuItem(), Bindings.not(model.archivedProperty()));
        NodeUtils.bindVisibility(remove.menuItem(), Bindings.not(model.archivedProperty()));
        NodeUtils.bindVisibility(dividerToHide.separatorMenuItem(), Bindings.not(model.archivedProperty()));

        Runnable doubleClickAction = () -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onShowProfile.accept(tableView.getSelectionModel().getSelectedItem());
            }
        };

        customTableView.addRowClickHandler(tableViewWithPagination, contextMenu, doubleClickAction);
        TableColumn<ProfileTeamItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableView, contextMenu);
        customTableView.addColumnToPagination(optionsColumn);
        optionsColumn.setMaxWidth(50);
        optionsColumn.setMinWidth(50);
    }

    private void refreshTable(ProfileTeamItemModel ProfileTeamItemModel) {
        onTeamRefresh.run();
    }

    private Pagination geographyCreateTable() {
        geographyCustomTableView = new CustomTableView<>(NUM_ITEMS_PER_PAGE, geographyItemModels);
        geographyTableViewWithPagination = geographyCustomTableView.setupPagination(geographyCustomTableView.createTableView());

        geographyConfigureTableColumns();
        //geographyConfigureContextMenu(geographyTableViewWithPagination);

        geographyTableViewWithPagination.getStyleClass().add(Styles.STRIPED);
        geographyItemModels.comparatorProperty().bind(geographyTableViewWithPagination.comparatorProperty());

        Pagination pagination = geographyCustomTableView.getPagination();
        VBox.setVgrow(geographyTableViewWithPagination, Priority.ALWAYS);
        VBox.setVgrow(pagination, Priority.ALWAYS);

        return pagination;
    }

    private void geographyConfigureTableColumns() {
        TableColumn<IGeographyItemModel, String> geographyColumn = geographyCustomTableView.createColumn(LocalizedText.GEOGRAPHY, IGeographyItemModel::nameProperty);

        geographyCustomTableView.addColumnsToPagination(Collections.singletonList(geographyColumn));
    }

    private void geographyConfigureContextMenu(TableView<IGeographyItemModel> tableView) {
        List<MenuItemInfo<IGeographyItemModel>> menuItemInfos = List.of(
                new MenuItemInfo<>(Icons.SHOW, LocalizedText.SHOW, System.out::println),
                new MenuItemInfo<>(Icons.DELETE, LocalizedText.DELETE, System.out::println),
                new MenuItemInfo<>(true),
                new MenuItemInfo<>(Icons.MARKUP, LocalizedText.ADJUST_MULTIPLIERS, System.out::println),
                new MenuItemInfo<>(Icons.COPY, LocalizedText.COPY, System.out::println, new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN))
        );

        var contextMenu = createContextMenu(menuItemInfos, tableView);

        TableColumn<IGeographyItemModel, Void> optionsColumn = geographyCustomTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableView, contextMenu);
        geographyCustomTableView.addColumnToPagination(optionsColumn);
        optionsColumn.setMaxWidth(50);
        optionsColumn.setMinWidth(50);
    }

    private SegmentedButton createSegmentedButton() {
        return new SegmentedButtonBuilder<TeamTableType>()
                .withSelectedProperty(model.selectedTableTypeProperty())
                .addButton(LocalizedText.PROFILES, TeamTableType.PROFILE)
                .addButton(LocalizedText.HISTORY, TeamTableType.HISTORY)
                .build();
    }

    private Pagination profileCreateHistoryTable(int itemsPerPage) {
        customTableViewHistory = new CustomTableView<>(itemsPerPage, historyProfileItemModels);
        tableViewWithPaginationHistory = customTableViewHistory.setupPagination(customTableViewHistory.createTableView());

        profileHistoryConfigureTableColumns();

        tableViewWithPaginationHistory.getStyleClass().add(Styles.STRIPED);
        historyProfileItemModels.comparatorProperty().bind(tableViewWithPaginationHistory.comparatorProperty());

        Pagination pagination = customTableViewHistory.getPagination();
        VBox.setVgrow(tableViewWithPaginationHistory, Priority.ALWAYS);
        VBox.setVgrow(pagination, Priority.ALWAYS);

        return pagination;
    }

    private void profileHistoryConfigureTableColumns() {
        TableColumn<TeamHistoryItemModel, LocalDateTime> dateColumn = customTableViewHistory.createColumn(LocalizedText.DATE, TeamHistoryItemModel::updatedAtProperty, new LocalDateTimeCellFactory<>(model.currentDateProperty()));
        TableColumn<TeamHistoryItemModel, Reason> reasonColumn = customTableViewHistory.createColumn(LocalizedText.REASON, TeamHistoryItemModel::reasonProperty, new ReasonCellFactory<>());
        TableColumn<TeamHistoryItemModel, BigDecimal> hours = customTableViewHistory.createColumn(LocalizedText.HOURS, TeamHistoryItemModel::totalHoursProperty, new HourCellFactory<>());
        TableColumn<TeamHistoryItemModel, BigDecimal> hourlyRate = customTableViewHistory.createColumn(LocalizedText.HOURLY_RATE, TeamHistoryItemModel::hourlyRateProperty, new CurrencyCellFactory<>());
        TableColumn<TeamHistoryItemModel, BigDecimal> dayRate = customTableViewHistory.createColumn(LocalizedText.DAY_RATE, TeamHistoryItemModel::dayRateProperty, new CurrencyCellFactory<>());

        List<MenuItemInfo<TeamHistoryItemModel>> menuItemInfos = List.of(new MenuItemInfo<>(Icons.PROFILES, LocalizedText.SHOW, teamHistoryItemModel -> {
        }));
        ContextMenu contextMenu = CustomContextMenu.createContextMenu(menuItemInfos, tableViewWithPaginationHistory);

        Runnable doubleClickAction = () -> {
            var history = tableViewWithPaginationHistory.getSelectionModel().getSelectedItem();
            if (history != null) {
                model.selectedHistoryItemProperty().set(history);
            }
        };

        tableViewWithPaginationHistory.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                detailTableView.setData(nv.details());
            } else {
                detailTableView.getData().clear();
            }
        });

//        customTableViewHistory.addRowClickHandler(tableViewWithPaginationHistory, contextMenu, doubleClickAction);

        TableColumn<TeamHistoryItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableViewWithPaginationHistory, contextMenu);

        optionsColumn.setMaxWidth(50);
        optionsColumn.setMinWidth(50);

        customTableViewHistory.addColumnsToPagination(Arrays.asList(dateColumn, reasonColumn, hours, hourlyRate, dayRate));
    }

    private Pagination historyDetailTable(int itemsPerPage) {
        detailTableView = new CustomTableView<>(itemsPerPage, historyItemModels);
        detailTableViewWithPagination = detailTableView.setupPagination(detailTableView.createTableView());

        historyDetailTableColumns();

        detailTableViewWithPagination.getStyleClass().add(Styles.STRIPED);
        historyItemModels.comparatorProperty().bind(detailTableViewWithPagination.comparatorProperty());

        Pagination pagination = detailTableView.getPagination();
        VBox.setVgrow(detailTableViewWithPagination, Priority.ALWAYS);
        VBox.setVgrow(pagination, Priority.ALWAYS);

        return pagination;
    }

    private void historyDetailTableColumns() {
        TableColumn<TeamHistoryProfileItemModel, String> nameColumn = detailTableView.createColumn(LocalizedText.NAME, TeamHistoryProfileItemModel::nameProperty);
        TableColumn<TeamHistoryProfileItemModel, BigDecimal> hourAllocationColumn = detailTableView.createColumn(LocalizedText.HOUR_ALLOCATION, TeamHistoryProfileItemModel::hourAllocationProperty, new PercentageCellFactory<>());
        TableColumn<TeamHistoryProfileItemModel, BigDecimal> contributedHours = detailTableView.createColumn(LocalizedText.HOURS, TeamHistoryProfileItemModel::totalHoursProperty, new HourCellFactory<>());
        TableColumn<TeamHistoryProfileItemModel, BigDecimal> costAllocationColumn = detailTableView.createColumn(LocalizedText.COST_ALLOCATION, TeamHistoryProfileItemModel::costAllocationProperty, new PercentageCellFactory<>());
        TableColumn<TeamHistoryProfileItemModel, BigDecimal> hourlyRateColumn = detailTableView.createColumn(LocalizedText.HOURLY_RATE, TeamHistoryProfileItemModel::hourlyRateProperty, new CurrencyCellFactory<>());
        TableColumn<TeamHistoryProfileItemModel, BigDecimal> dayRateColumn = detailTableView.createColumn(LocalizedText.DAY_RATE, TeamHistoryProfileItemModel::dayRateProperty, new CurrencyCellFactory<>());

        detailTableViewWithPagination.getStyleClass().add(Styles.STRIPED);

        List<MenuItemInfo<TeamHistoryProfileItemModel>> menuItemInfos = List.of(new MenuItemInfo<>(Icons.PROFILES, LocalizedText.SHOW, teamHistoryItemModel -> {
        }));
        ContextMenu contextMenu = CustomContextMenu.createContextMenu(menuItemInfos, detailTableViewWithPagination);

        Runnable doubleClickAction = () -> {
        };

//        detailTableView.addRowClickHandler(detailTableViewWithPagination, contextMenu, doubleClickAction);

        TableColumn<TeamHistoryProfileItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), detailTableViewWithPagination, contextMenu);

        optionsColumn.setMaxWidth(50);
        optionsColumn.setMinWidth(50);

        detailTableView.addColumnsToPagination(Arrays.asList(nameColumn, hourAllocationColumn, contributedHours, costAllocationColumn, hourlyRateColumn, dayRateColumn));
    }
}