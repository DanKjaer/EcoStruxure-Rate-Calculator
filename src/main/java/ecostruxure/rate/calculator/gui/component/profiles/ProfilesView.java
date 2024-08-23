package ecostruxure.rate.calculator.gui.component.profiles;

import atlantafx.base.controls.Spacer;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.ProfileItemModel;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.*;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Buttons;
import ecostruxure.rate.calculator.gui.widget.CustomComboBox;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Informative;
import ecostruxure.rate.calculator.gui.widget.tables.*;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.CurrencyCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.HourCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.PercentageCellFactory;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.MenuItemInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.addKeyEventHandler;
import static ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu.createContextMenu;

public class ProfilesView implements View {
    private final ProfilesModel model;
    private final Runnable onAddProfile;
    private final Consumer<ProfileItemModel> onShowProfile;
    private final Consumer<ProfileItemModel> onArchiveProfile;
    private final Consumer<ProfileItemModel> onUnArchiveProfile;
    private final Consumer<List<ProfileItemModel>> onArchiveProfiles;


    private CustomTableView<ProfileItemModel> customTableView;
    private TableView<ProfileItemModel> tableViewWithPagination;

    private final FilteredList<ProfileItemModel> filteredProfileItemModels;
    private final SortedList<ProfileItemModel> tableItems;

    private static final int SEARCH_FIELD_WIDTH = 200;

    private ComboBox<String> filterOptions;

    public ProfilesView(ProfilesModel model, Runnable onAddProfile, Consumer<ProfileItemModel> onShowProfile,
                        Consumer<ProfileItemModel> onArchiveProfile, Consumer<ProfileItemModel> onUnArchiveProfile, Consumer<List<ProfileItemModel>> onArchiveProfiles) {
        this.model = model;
        this.onAddProfile = onAddProfile;
        this.onShowProfile = onShowProfile;
        this.onArchiveProfile = onArchiveProfile;
        this.onUnArchiveProfile = onUnArchiveProfile;
        this.onArchiveProfiles = onArchiveProfiles;

        this.filteredProfileItemModels = new FilteredList<>(model.profiles());
        this.tableItems = new SortedList<>(filteredProfileItemModels);

        setupSearchFilter();
        setupChangeList();
        setupComboBoxFilters();
        setupListArchivedListener();

    }

    private void setupSearchFilter() {
        model.searchProperty().addListener((obs, ov, nv) -> {
            Predicate<ProfileItemModel> searchPredicate = (ProfileItemModel profileItemModel) -> {
                if (nv == null || nv.isEmpty())
                    return true;

                var lowercase = nv.toLowerCase();
                return profileItemModel.nameProperty().get().toLowerCase().contains(lowercase);
            };

            filteredProfileItemModels.setPredicate(searchPredicate.and(filterPredicate()));
        });
    }

    private void setupListArchivedListener() {
        filteredProfileItemModels.setPredicate(filterPredicate());

        model.profiles().addListener((ListChangeListener.Change<? extends ProfileItemModel> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProfileItemModel profile : change.getAddedSubList()) {
                        profile.archivedProperty().addListener((obs, oldValue, newValue) -> {
                            filteredProfileItemModels.setPredicate(null);
                            filteredProfileItemModels.setPredicate(filterPredicate());
                        });
                    }
                }
            }
        });
    }

    private void setupChangeList() {
        tableItems.addListener((ListChangeListener<ProfileItemModel>) change -> {
            while (change.next() && customTableView != null) {
                if (change.wasAdded() || change.wasRemoved()) {
                    customTableView.updatePaginationTable(0);
                    selectItemById(model.lastSelectedTeamIdProperty().get());
                }
            }
        });
    }

    private Predicate<ProfileItemModel> filterPredicate()
    {
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

    private void setupComboBoxFilters() {
        filterOptions = CustomComboBox.filterOptions();

        filterOptions.setOnAction(e -> {
            filteredProfileItemModels.setPredicate(filterPredicate());
            customTableView.updatePaginationTable(0);
            model.searchProperty().set("");
        });
    }

    private void selectItemById(UUID uuid) {
        tableViewWithPagination.getItems().stream()
                .filter(item -> item.UUIDProperty().get().equals(uuid))
                .findFirst()
                .ifPresent(item -> {
                    tableViewWithPagination.getSelectionModel().select(item);
                    //tableViewWithPagination.scrollTo(item);
                });
    }

    @Override
    public Region build() {
        var results = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setVgrow(results, Priority.ALWAYS);

        var header = new HBox(
                Informative.pageHeader(LocalizedText.PROFILES, LocalizedText.PAGE_PROFILES_DESCRIPTION, Icons.PROFILES),
                new Spacer(),
                Informative.headerStats(LocalizedText.PROFILES, model.numProfiles(), CssClasses.BLUE, true)
        );

        var stats = Informative.statsContainer();
        stats.getChildren().addAll(
                Informative.statsBox(LocalizedText.TOTAL_HOURLY_RATE, BindingsUtils.createCurrencyStringBinding(model.totalHourlyRate()), CssClasses.BLUE, Feather.BAR_CHART),
                Informative.statsBox(LocalizedText.TOTAL_DAY_RATE, BindingsUtils.createCurrencyStringBinding(model.totalDayRate()), CssClasses.GREEN, Feather.BAR_CHART_2),
                Informative.statsBox(LocalizedText.UNIQUE_LOCATIONS, model.uniqueGeographies(), CssClasses.RED, Icons.GEOGRAPHY)
        );

        var content = new VBox(LayoutConstants.STANDARD_SPACING);
        content.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));

        var searchField = Fields.searchFieldGroup(SEARCH_FIELD_WIDTH);
        searchField.textProperty().bindBidirectional(model.searchProperty());
        searchField.getStyleClass().add(CssClasses.INPUT_GROUP_TEXT);

        var comboRows = CustomComboBox.rowsPerPageComboBox();
        comboRows.setOnAction(e -> {
            customTableView.setItemsPerPage(comboRows.getValue());
        });

        var addProfileBtn = Buttons.actionButton(
                LocalizedText.ADD_NEW_PROFILE,
                Icons.ADD,
                event -> onAddProfile.run(),
                Styles.SUCCESS);

        var tableViewHeader = new HBox(comboRows, new Spacer(), new InputGroup(filterOptions, searchField));
        var tableView = createTable(comboRows.getValue());

        var controls = new HBox(LayoutConstants.STANDARD_SPACING, new Spacer(), addProfileBtn);
        controls.setPadding(new Insets(LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING, 0, LayoutConstants.CONTENT_PADDING));

        content.getChildren().addAll(tableViewHeader, tableView);
        content.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, Styles.BORDER_SUBTLE);
        VBox.setMargin(content, new Insets(0, LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING));
        VBox.setVgrow(content, Priority.ALWAYS);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        results.getChildren().addAll(header, stats, controls, content);

        if(!model.profiles().isEmpty()) {
            customTableView.updatePaginationTable(0);
            selectItemById(model.lastSelectedTeamIdProperty().get());
        }
        return results;
    }

    private Pagination createTable(int itemsPerPage) {
        customTableView = new CustomTableView<>(itemsPerPage, tableItems);
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

    private ContextMenu configureContextMenu(TableView<ProfileItemModel> tableView) {
        var show = new MenuItemInfo<>(Icons.SHOW, LocalizedText.SHOW, onShowProfile, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        var dividerMultiSelect = new MenuItemInfo<ProfileItemModel>(true);
        var archive = new MenuItemInfo<>(Icons.DELETE, LocalizedText.ARCHIVE, this::archive, new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        List<MenuItemInfo<ProfileItemModel>> menuItemInfos = List.of(
                show,
                dividerMultiSelect,
                archive
        );

        var contextMenu = createContextMenu(menuItemInfos, tableView);
        addKeyEventHandler(tableView, contextMenu);

        BooleanBinding selectedNotNull = Bindings.isNotNull(tableView.getSelectionModel().selectedItemProperty());
        BooleanBinding selectedArchive = Bindings.createBooleanBinding(() -> {
            var selected = tableView.getSelectionModel().getSelectedItem();
            var items = tableView.getSelectionModel().getSelectedItems();
            return selected != null && items.size() == 1  && !selected.archivedProperty().get();
        }, tableView.getSelectionModel().selectedItemProperty());

        BooleanBinding multipleSelectedBinding = Bindings.createBooleanBinding(() -> {
            ObservableList<ProfileItemModel> selectedItems = tableView.getSelectionModel().getSelectedItems();
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
                ObservableList<ProfileItemModel> profiles = tableView.getSelectionModel().getSelectedItems();
                int filterIndex = filterOptions.getSelectionModel().getSelectedIndex();
                long countNotArchived = profiles.stream()
                        .filter(item -> !item.archivedProperty().get())
                        .count();

                if (countNotArchived > 1 && filterIndex == 0 || countNotArchived > 1 && filterIndex == 1)
                    return LocalizedText.ARCHIVE.get() + " " + countNotArchived + " " + LocalizedText.PROFILES.get();

                return selected != null && selected.archivedProperty().get() ? LocalizedText.UNARCHIVE.get() : LocalizedText.ARCHIVE.get();
            }
        }, selectedNotNull, selectedArchive, multipleSelectedBinding));

        NodeUtils.bindVisibility(show.menuItem(), multipleSelectedBinding);
        NodeUtils.bindVisibility(dividerMultiSelect.separatorMenuItem(), multipleSelectedBinding);

        Runnable doubleClickAction = () -> {
            ProfileItemModel selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onShowProfile.accept(selectedItem);
            }
        };

        customTableView.addRowClickHandler(tableView, contextMenu, doubleClickAction);
        return contextMenu;
    }

    private void archive(ProfileItemModel profileItemModel) {
        var selected = tableViewWithPagination.getSelectionModel().getSelectedItems();
        long countNotArchived = selected.stream()
                .filter(item -> !item.archivedProperty().get())
                .count();

        if (countNotArchived > 1) {
            onArchiveProfiles.accept(selected);
            return;
        }
        if (profileItemModel.archivedProperty().get())
            onUnArchiveProfile.accept(profileItemModel);
        else
            onArchiveProfile.accept(profileItemModel);

    }

    private void configureTableColumns(ContextMenu contextMenu) {
        TableColumn<ProfileItemModel, String> nameColumn = customTableView.createColumn(LocalizedText.NAME, ProfileItemModel::nameProperty);
        nameColumn.setCellFactory(column -> {
            return new TableCell<ProfileItemModel, String>() {
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
                            ProfileItemModel profileItemModel = getTableView().getItems().get(getIndex());
                            nameLabel.textProperty().bind(profileItemModel.nameProperty());
                            archived.textProperty().bind(
                                    Bindings.when(profileItemModel.archivedProperty())
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


        //profilesView - Name, annual_salary, effectiveness, total_hours, effective_work_hours, location,
        // total utilization hours. f.eks. hvis profilen er på 2 teams, og har 30% på hver,
        // skal den sige utilization hours 60. - Omdøb utilization på navnet.

        TableColumn<ProfileItemModel, BigDecimal> annualCostColumn = customTableView.createColumn(LocalizedText.CONTRIBUTED_ANNUAL_COST, ProfileItemModel::annualCostProperty, new CurrencyCellFactory<>());
        TableColumn<ProfileItemModel, BigDecimal> effectivenessColumn = customTableView.createColumn(LocalizedText.EFFECTIVENESS, ProfileItemModel::effectivenessProperty, new PercentageCellFactory<>());
        TableColumn<ProfileItemModel, BigDecimal> annualHoursColumn = customTableView.createColumn(LocalizedText.ANNUAL_HOURS, ProfileItemModel::annualHoursProperty, new HourCellFactory<>());
        TableColumn<ProfileItemModel, BigDecimal> effectiveWorkHoursColumn = customTableView.createColumn(LocalizedText.EFFECTIVE_WORK_HOURS, ProfileItemModel::effectiveWorkHoursProperty, new HourCellFactory<>());
        TableColumn<ProfileItemModel, BigDecimal> allocatedHours = customTableView.createColumn(LocalizedText.ALLOCATED_HOURS, ProfileItemModel::allocatedHoursProperty, new HourCellFactory<>());

        // Fixes senere når jeg ved hvad jeg skal gøre med geography for at få den ind
        //TableColumn<ProfileItemModel, String> locationColumn = customTableView.createColumn(LocalizedText.LOCATION, ProfileItemModel::property);

        //TableColumn<ProfileItemModel, String> teamColumn = customTableView.createColumn(LocalizedText.TEAMS, ProfileItemModel::teamsProperty);

        // Ændres til locationColumn istedet for allocatedHours, når jeg har fikset den column,
        allocatedHours.setResizable(false);
        allocatedHours.setMinWidth(80);
        allocatedHours.setMaxWidth(80);
        allocatedHours.setPrefWidth(80);

        TableColumn<ProfileItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableViewWithPagination, contextMenu);
        optionsColumn.setResizable(false);
        optionsColumn.setEditable(false);
        optionsColumn.setMinWidth(50);
        optionsColumn.setMaxWidth(50);
        optionsColumn.setPrefWidth(50);

        customTableView.addColumnsToPagination(Arrays.asList(nameColumn, annualHoursColumn, effectiveWorkHoursColumn, effectivenessColumn, annualCostColumn, allocatedHours, optionsColumn)); //locationColumn,

        tableViewWithPagination.setEditable(true);
    }
}