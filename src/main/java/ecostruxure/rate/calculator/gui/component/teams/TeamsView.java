package ecostruxure.rate.calculator.gui.component.teams;

import atlantafx.base.controls.Spacer;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.be.enums.RateType;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.*;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.*;
import ecostruxure.rate.calculator.gui.widget.tables.*;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.*;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.MenuItemInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.controlsfx.control.SegmentedButton;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.addKeyEventHandler;
import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.createContextMenu;

public class TeamsView implements View {
    private static final int SEARCH_FIELD_WIDTH = 200;

    private final TeamsModel model;
    private final Consumer<TeamItemModel> onAdjustMultipliers;
    private final BiConsumer<TeamItemModel, BigDecimal> onAdjustMarkup;
    private final Consumer<TeamItemModel> onAssignProfiles;
    private final Consumer<TeamItemModel> onEditTeam;
    private final Consumer<TeamItemModel> onShowTeam;
    private final Runnable addTeam;
    private final Consumer<TeamItemModel> onArchiveTeam;
    private final Consumer<TeamItemModel> onUnArchiveTeam;
    private final Consumer<TeamItemModel> onExportTeam;
    private final Runnable onChangeRateType;
    private final Runnable onRefresh;
    private final Consumer<List<TeamItemModel>> archiveTeams;
    private final Consumer<List<TeamItemModel>> onExportTeams;

    private final FilteredList<TeamItemModel> filteredTeamItemModels;
    private final SortedList<TeamItemModel> tableItems;

    private CustomTableView<TeamItemModel> customTableView;
    private TableView<TeamItemModel> tableViewWithPagination;

    private ComboBox<String> filterOptions;

    public TeamsView(TeamsModel model, Consumer<TeamItemModel> onShowTeam, Consumer<TeamItemModel> onAdjustMultipliers, BiConsumer<TeamItemModel, BigDecimal> onAdjustMarkup,
                     Consumer<TeamItemModel> onAssignProfiles, Consumer<TeamItemModel> onEditTeam,
                     Runnable addTeam, Consumer<TeamItemModel> onArchiveTeam, Consumer<TeamItemModel> onUnArchiveTeam, Runnable onRefresh,
                     Consumer<TeamItemModel> onExportTeam, Runnable onChangeRateType, Consumer<List<TeamItemModel>> archiveTeams, Consumer<List<TeamItemModel>> onExportTeams) {
        this.model = model;
        this.onShowTeam = onShowTeam;
        this.onAdjustMultipliers = onAdjustMultipliers;
        this.onAdjustMarkup = onAdjustMarkup;
        this.onAssignProfiles = onAssignProfiles;
        this.onEditTeam = onEditTeam;
        this.addTeam = addTeam;
        this.onArchiveTeam = onArchiveTeam;
        this.onUnArchiveTeam = onUnArchiveTeam;
        this.onRefresh = onRefresh;
        this.onExportTeam = onExportTeam;
        this.onChangeRateType = onChangeRateType;
        this.archiveTeams = archiveTeams;
        this.onExportTeams = onExportTeams;

        this.filteredTeamItemModels = new FilteredList<>(model.teams());
        this.tableItems = new SortedList<>(filteredTeamItemModels);

        setupSearchFilter();
        setupChangeList();
        setupComboBoxFilters();
        setupListArchivedListener();
    }

    private Predicate<TeamItemModel> filterPredicate() {
        String selected = filterOptions.getSelectionModel().getSelectedItem();
        String active = LocalizedText.ACTIVE.get();
        String archived = LocalizedText.ARCHIVED.get();

        if (active.equals(selected))
            return item -> !item.archivedProperty().get();
        else if (archived.equals(selected))
            return item -> item.archivedProperty().get();
        else
            return item -> true;
    }

    private void setupSearchFilter() {
        model.searchProperty().addListener((obs, ov, nv) -> {
            Predicate<TeamItemModel> searchPredicate = (TeamItemModel teamItemModel) -> {
                if (nv == null || nv.isEmpty())
                    return true;

                var lowercase = nv.toLowerCase();
                return teamItemModel.nameProperty().get().toLowerCase().contains(lowercase);
            };

            // sætter begge predicates for at kunne respektere valget i comboboxen
            filteredTeamItemModels.setPredicate(filterPredicate().and(searchPredicate));
        });
    }

    private void setupChangeList() {
        tableItems.addListener((ListChangeListener<TeamItemModel>) change -> {
            while (change.next() && customTableView != null) {
                if (change.wasAdded() || change.wasRemoved()) {
                    customTableView.updatePaginationTable(0);
                    selectItemById(model.lastSelectedTeamIdProperty().get());
                }
            }
        });
    }

    private void setupListArchivedListener() {
        filteredTeamItemModels.setPredicate(filterPredicate());

        model.teams().addListener((ListChangeListener.Change<? extends TeamItemModel> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (TeamItemModel team : change.getAddedSubList()) {
                        team.archivedProperty().addListener((obs, oldValue, newValue) -> {
                            filteredTeamItemModels.setPredicate(null);
                            filteredTeamItemModels.setPredicate(filterPredicate());
                        });
                    }
                }
            }
        });
    }

    private int cachedSelect = 0;

    private void setupComboBoxFilters() {
        filterOptions = CustomComboBox.filterOptions();

        filterOptions.setOnAction(e -> {
            filteredTeamItemModels.setPredicate(filterPredicate());
            customTableView.updatePaginationTable(0);
            model.searchProperty().set("");
            cachedSelect = filterOptions.getSelectionModel().getSelectedIndex();
        });
    }

    @Override
    public Region build() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        NodeUtils.bindVisibility(progressIndicator, model.teamsFetchedProperty().not());

        var results = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setVgrow(results, Priority.ALWAYS);

        var header = new HBox(
                Informative.pageHeader(LocalizedText.TEAMS, LocalizedText.PAGE_TEAMS_DESCRIPTION, Icons.TEAMS),
                new Spacer(),
                Informative.headerStats(LocalizedText.TEAMS, model.numTeamsProperty(), CssClasses.BLUE, true)
        );

        var content = new VBox(LayoutConstants.STANDARD_SPACING);
        content.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));

        var addTeamBtn = Buttons.actionButton(
                LocalizedText.ADD_NEW_TEAM,
                Icons.ADD,
                event -> addTeam.run(),
                Styles.SUCCESS);

        var searchField = Fields.searchFieldGroup(SEARCH_FIELD_WIDTH);
        searchField.textProperty().bindBidirectional(model.searchProperty());
        searchField.getStyleClass().add(CssClasses.INPUT_GROUP_TEXT);

        var comboRows = CustomComboBox.rowsPerPageComboBox();
        comboRows.setOnAction(e -> {
            customTableView.setItemsPerPage(comboRows.getValue());
        });

        var tableViewHeaderBottom = new HBox(LayoutConstants.STANDARD_SPACING, comboRows, createSegmentedButton(), new Spacer(), new InputGroup(filterOptions, searchField));
        var tableViewHeader = new VBox(LayoutConstants.STANDARD_SPACING, tableViewHeaderBottom);

        var controls = new HBox(LayoutConstants.STANDARD_SPACING, new Spacer(), addTeamBtn);
        controls.setPadding(new Insets(0, LayoutConstants.CONTENT_PADDING, 0, LayoutConstants.CONTENT_PADDING));

        var tableView = createTable(comboRows.getValue());
        content.getChildren().addAll(tableViewHeader, tableView);
        content.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, Styles.BORDER_SUBTLE);
        VBox.setMargin(content, new Insets(0, LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING));
        VBox.setVgrow(content, Priority.ALWAYS);

//        NodeUtils.bindVisibility(tableView, model.teamsFetchedProperty());

        results.getChildren().addAll(header, controls, content);
        return new StackPane(results, progressIndicator);
    }

    private void selectItemById(UUID id) {
        tableViewWithPagination.getItems().stream()
                .filter(item -> item.teamIdProperty().get() == id)
                .findFirst()
                .ifPresent(item -> {
                    tableViewWithPagination.getSelectionModel().select(item);
                    //tableViewWithPagination.scrollTo(item);
                });
    }

    private SegmentedButton createSegmentedButton() {
        return new SegmentedButtonBuilder<RateType>()
                .withSelectedProperty(model.selectedRateTypeProperty())
                .addButton(LocalizedText.HOURLY_RATE, RateType.HOURLY)
                .addButton(LocalizedText.DAY_RATE, RateType.DAY)
                .addButton(LocalizedText.ANNUAL_COST, RateType.ANNUAL)
                .build();
    }

    private Pagination createTable(int rowsPerPage) {
        customTableView = new CustomTableView<>(rowsPerPage, tableItems);
        tableViewWithPagination = customTableView.setupPagination(customTableView.createTableView());
        tableViewWithPagination.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        var contextMenu = configureContextMenu(tableViewWithPagination);
        configureTableColumns(contextMenu);

        tableViewWithPagination.getStyleClass().add(Styles.STRIPED);
        tableItems.comparatorProperty().bind(tableViewWithPagination.comparatorProperty());

        Pagination pagination = customTableView.getPagination();
        VBox.setVgrow(tableViewWithPagination, Priority.ALWAYS);
        VBox.setVgrow(pagination, Priority.ALWAYS);

        return pagination;
    }


    private void configureTableColumns(ContextMenu contextMenu) {
        TableColumn<TeamItemModel, String> nameColumn = customTableView.createColumn(LocalizedText.NAME, TeamItemModel::nameProperty);
        nameColumn.setCellFactory(column -> {
            return new TableCell<TeamItemModel, String>() {
                private final VBox container = new VBox();
                private final Label nameLabel = new Label("");
                private final Label archived = new Label("");

                {
                    archived.getStyleClass().add(Styles.TEXT_SUBTLE);
                    container.getChildren().addAll(nameLabel, archived);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        if (filterOptions.getSelectionModel().getSelectedIndex() == 0) {
                            TeamItemModel teamItemModel = getTableView().getItems().get(getIndex());
                            nameLabel.textProperty().bind(teamItemModel.nameProperty());
                            archived.textProperty().bind(
                                    Bindings.when(teamItemModel.archivedProperty())
                                            .then(LocalizedText.ARCHIVED.get())
                                            .otherwise(LocalizedText.ACTIVE.get())
                            );

                            setGraphic(container);
                        } else {
                            setGraphic(new Label(item));
                        }
                    }
                }
            };
        });

        //TableColumn<TeamItemModel, BigDecimal> rawRateColumn = customTableView.createColumn(model.rawColumnNameProperty(), TeamItemModel::rawRateProperty, new CurrencyCellFactory<>());
        //TableColumn<TeamItemModel, BigDecimal> markupRateColumn = customTableView.createColumn(model.markupColumnNameProperty(), TeamItemModel::markupRateProperty, new CurrencyCellFactory<>());
        //TableColumn<TeamItemModel, BigDecimal> grossMarginRateColumn = customTableView.createColumn(model.grossMarginColumnNameProperty(), TeamItemModel::grossMarginRateProperty, new CurrencyCellFactory<>());

        TableColumn<TeamItemModel, BigDecimal> markupColumn = customTableView.createColumn(LocalizedText.MARKUP, TeamItemModel::markupProperty, new PercentageCellFactory<>());
        TableColumn<TeamItemModel, BigDecimal> grossMarginColumn = customTableView.createColumn(LocalizedText.GROSS_MARGIN_ABBREVIATION, TeamItemModel::grossMarginProperty, new PercentageCellFactory<>());
        TableColumn<TeamItemModel, Boolean> isArchivedColumn = customTableView.createColumn(LocalizedText.ARCHIVED, TeamItemModel::archivedProperty, new IsArchivedCellFactory<>());
        TableColumn<TeamItemModel, Timestamp> updatedAtColumn = customTableView.createColumn(LocalizedText.UPDATED_AT, TeamItemModel::updatedAtProperty, new UpdatedAtCellFactory<>());
        TableColumn<TeamItemModel, BigDecimal> hourlyRateColumn = customTableView.createColumn(LocalizedText.HOURLY_RATE, TeamItemModel::hourlyRateProperty, new CurrencyCellFactory<>());
        TableColumn<TeamItemModel, BigDecimal> dayRateColumn = customTableView.createColumn(LocalizedText.DAY_RATE, TeamItemModel::dayRateProperty, new CurrencyCellFactory<>());
        TableColumn<TeamItemModel, BigDecimal> totalAllocatedCostColumn = customTableView.createColumn(LocalizedText.TOTAL_ANNUAL_COST, TeamItemModel::totalAllocatedCostProperty, new CurrencyCellFactory<>());
        TableColumn<TeamItemModel, BigDecimal> totalAllocatedHoursColumn = customTableView.createColumn(LocalizedText.TOTAL_HOURS_ANNUALLY, TeamItemModel::totalAllocatedHoursProperty, new HourCellFactory<>());
        markupColumn.setVisible(true);
        markupColumn.setResizable(false);
        markupColumn.setMinWidth(80);
        markupColumn.setMaxWidth(80);
        markupColumn.setPrefWidth(80);

        grossMarginColumn.setVisible(true);
        grossMarginColumn.setResizable(false);
        grossMarginColumn.setMinWidth(80);
        grossMarginColumn.setMaxWidth(80);
        grossMarginColumn.setPrefWidth(80);

        TableColumn<TeamItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableViewWithPagination, contextMenu);
        optionsColumn.setResizable(false);
        optionsColumn.setEditable(false);
        optionsColumn.setMinWidth(50);
        optionsColumn.setMaxWidth(50);
        optionsColumn.setPrefWidth(50);

        customTableView.addColumnsToPagination(Arrays.asList(nameColumn, markupColumn,
                grossMarginColumn, isArchivedColumn, updatedAtColumn, hourlyRateColumn,
                dayRateColumn, totalAllocatedCostColumn, totalAllocatedHoursColumn, optionsColumn));

        model.selectedRateTypeProperty().addListener((obs, ov, nv) -> onChangeRateType.run());

        tableViewWithPagination.setEditable(true);
    }

    private ContextMenu configureContextMenu(TableView<TeamItemModel> tableView) {
        var show = new MenuItemInfo<>(Icons.SHOW, LocalizedText.SHOW, onShowTeam, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        var edit = new MenuItemInfo<>(Icons.EDIT, LocalizedText.EDIT, onEditTeam, new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        var dividerMultiSelect = new MenuItemInfo<TeamItemModel>(true);
        var assignProfiles = new MenuItemInfo<>(Icons.PERSON_ADD, LocalizedText.ASSIGN_PROFILES, onAssignProfiles, new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        var multipliers = new MenuItemInfo<>(Icons.MARKUP, LocalizedText.ADJUST_MULTIPLIERS, onAdjustMultipliers, new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
        var dividerUnArchived = new MenuItemInfo<TeamItemModel>(true);
        var export = new MenuItemInfo<>(Icons.EXPORT, LocalizedText.EXPORT, this::export, new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN));
        var archive = new MenuItemInfo<>(Icons.DELETE, LocalizedText.ARCHIVE, this::archive, new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));

        List<MenuItemInfo<TeamItemModel>> menuItemInfos = List.of(
                show,
                edit,
                dividerUnArchived,
                assignProfiles,
                multipliers,
                dividerMultiSelect,
                export,
                new MenuItemInfo<>(Icons.REFRESH, LocalizedText.REFRESH, this::refresh, new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN)),
                new MenuItemInfo<>(true),
                archive
        );

        var contextMenu = createContextMenu(menuItemInfos, tableView);
        addKeyEventHandler(tableView, contextMenu);

        BooleanBinding selectedNotNull = Bindings.isNotNull(tableView.getSelectionModel().selectedItemProperty());
        BooleanBinding selectedArchive = Bindings.createBooleanBinding(() -> {
            var selected = tableView.getSelectionModel().getSelectedItem();
            var items = tableView.getSelectionModel().getSelectedItems();
            return selected != null && items.size() == 1 && !selected.archivedProperty().get();
        }, tableView.getSelectionModel().selectedItemProperty());

        BooleanBinding multipleSelectedBinding = Bindings.createBooleanBinding(() -> {
            ObservableList<TeamItemModel> selectedItems = tableView.getSelectionModel().getSelectedItems();
            return selectedItems.size() == 1;
        }, tableView.getSelectionModel().getSelectedItems());

        archive.menuItem().graphicProperty().bind(Bindings.createObjectBinding(() -> {
            var selected = tableView.getSelectionModel().getSelectedItem();
            var items = tableView.getSelectionModel().getSelectedItems();
            var newIcon = selected != null && items.size() == 1 && selected.archivedProperty().get() ? new FontIcon(Icons.UNARCHIVE) : new FontIcon(Icons.DELETE);
            newIcon.getStyleClass().add("menu-icon");

            return newIcon;
        }, tableView.getSelectionModel().selectedItemProperty()));

        archive.menuItem().textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                var selected = tableView.getSelectionModel().getSelectedItem();
                ObservableList<TeamItemModel> teams = tableView.getSelectionModel().getSelectedItems();
                int filterIndex = filterOptions.getSelectionModel().getSelectedIndex();
                long countNotArchived = teams.stream()
                        .filter(item -> !item.archivedProperty().get())
                        .count();

                if (countNotArchived > 1 && filterIndex == 0 || countNotArchived > 1 && filterIndex == 1)
                    return LocalizedText.ARCHIVE.get() + " " + countNotArchived + " " + LocalizedText.TEAMS.get();

                return selected != null && selected.archivedProperty().get() ? LocalizedText.UNARCHIVE.get() : LocalizedText.ARCHIVE.get();
            }
        }, selectedNotNull, selectedArchive, multipleSelectedBinding));

        export.menuItem().textProperty().bind(Bindings.createStringBinding(() -> {
            var selected = tableView.getSelectionModel().getSelectedItem();
            int selectedTeam = tableView.getSelectionModel().getSelectedItems().size();

            if (selectedTeam > 1)
                return LocalizedText.EXPORT.get() + " " + selectedTeam + " " + LocalizedText.TEAMS.get() + " " + LocalizedText.TO_FILE.get();

            return LocalizedText.EXPORT_TO_FILE.get();
        }, selectedNotNull, selectedArchive, multipleSelectedBinding));

        NodeUtils.bindVisibility(dividerMultiSelect.separatorMenuItem(), multipleSelectedBinding);
        NodeUtils.bindVisibility(edit.menuItem(), multipleSelectedBinding);
        NodeUtils.bindVisibility(show.menuItem(), multipleSelectedBinding);

        NodeUtils.bindVisibility(assignProfiles.menuItem(), selectedNotNull.and(multipleSelectedBinding).and(selectedArchive));
        NodeUtils.bindVisibility(multipliers.menuItem(), selectedNotNull.and(multipleSelectedBinding).and(selectedArchive));
        NodeUtils.bindVisibility(dividerUnArchived.separatorMenuItem(), selectedNotNull.and(multipleSelectedBinding).and(selectedArchive));

        assignProfiles.menuItem().visibleProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                if (contextMenu.getScene() != null) {
                    contextMenu.hide();
                    contextMenu.show(tableView, Side.BOTTOM, 0, 0);
                    contextMenu.hide();
                }
            }
        });

        Runnable doubleClickAction = () -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                model.lastSelectedTeamIdProperty().set(tableView.getSelectionModel().getSelectedItem().teamIdProperty().get());
                System.out.println("Se her ~~~" + tableView.getSelectionModel().getSelectedItem().teamIdProperty());
                onShowTeam.accept(tableView.getSelectionModel().getSelectedItem());
            }
        };

        customTableView.addRowClickHandler(tableView, contextMenu, doubleClickAction);

        return contextMenu;
    }

    private void refresh(TeamItemModel teamItemModel) {
        onRefresh.run();
    }

    private void export(TeamItemModel teamItemModel) {
        var selected = tableViewWithPagination.getSelectionModel().getSelectedItems();

        if (selected.size() > 1) {
            onExportTeams.accept(tableViewWithPagination.getSelectionModel().getSelectedItems());
        } else {
            onExportTeam.accept(teamItemModel);
        }
    }

    private void archive(TeamItemModel teamItemModel) {
        var selected = tableViewWithPagination.getSelectionModel().getSelectedItems();
        long countNotArchived = selected.stream()
                .filter(item -> !item.archivedProperty().get())
                .count();

        if (countNotArchived > 1) {
            archiveTeams.accept(selected);
            return;
        }
        if (teamItemModel.archivedProperty().get())
            onUnArchiveTeam.accept(teamItemModel);
        else
            onArchiveTeam.accept(teamItemModel);
    }
}