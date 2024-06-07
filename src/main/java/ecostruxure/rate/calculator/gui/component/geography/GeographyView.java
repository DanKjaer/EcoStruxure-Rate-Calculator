package ecostruxure.rate.calculator.gui.component.geography;

import atlantafx.base.controls.Spacer;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.geography.country.CountryItemModel;
import ecostruxure.rate.calculator.gui.component.geography.geograph.GeographyItemModel;
import ecostruxure.rate.calculator.gui.component.teams.TeamItemModel;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.*;
import ecostruxure.rate.calculator.gui.widget.tables.CustomTableView;
import ecostruxure.rate.calculator.gui.widget.world.MapWidget;
import ecostruxure.rate.calculator.gui.widget.world.WorldWidget;
import eu.hansolo.fx.countries.Country;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GeographyView implements View {
    private static final StringProperty search = new SimpleStringProperty("");
    private static final int SEARCH_FIELD_WIDTH = 200;

    private final GeographyModel model;
    private final FilteredList<IGeographyItemModel> filteredGeographyItemModels;
    private final SortedList<IGeographyItemModel> geographyItemModels;

    private final Runnable onAddGeography;

    private final ToggleButton all = new ToggleButton();
    private final ToggleButton countries = new ToggleButton();
    private final ToggleButton geographies = new ToggleButton();

    private CustomTableView<IGeographyItemModel> customTableView;
    private ComboBox<String> filterOptions;

    public GeographyView(GeographyModel model, Runnable onAddGeography) {
        this.model = model;

        this.filteredGeographyItemModels = new FilteredList<>(model.all());
        this.geographyItemModels = new SortedList<>(filteredGeographyItemModels);

        this.onAddGeography = onAddGeography;

        setupSearchFilter();
        setupChangeList();
        setupComboBoxFilters();
    }

    private Predicate<IGeographyItemModel> filterPredicate()
    {
        String selected = filterOptions.getSelectionModel().getSelectedItem();
        String countries = LocalizedText.COUNTRIES.get();
        String geographies = LocalizedText.GEOGRAPHIES.get();

        if (geographies.equals(selected))
            return item -> !item.isCountryProperty().get();
        else if (countries.equals(selected))
            return item -> item.isCountryProperty().get();
        else
            return item -> true;
    }

    private void setupComboBoxFilters() {
        filterOptions = CustomComboBox.geographiesOptions();

        filterOptions.setOnAction(e -> {
            String selected = filterOptions.getSelectionModel().getSelectedItem();
            filteredGeographyItemModels.setPredicate(filterPredicate());
            customTableView.updatePaginationTable(0);
            search.set("");
        });
    }

    private void setupSearchFilter() {
        this.searchProperty().addListener((obs, ov, nv) -> {
            Predicate<IGeographyItemModel> searchPredicate = (IGeographyItemModel iGeographyItemModel) -> {
                if (nv == null)
                    return true;

                var lowercase = nv.toLowerCase();
                var name = iGeographyItemModel.nameProperty().get().toLowerCase();

                Predicate<IGeographyItemModel> geographiesPredicate = (item) -> {
                    // hvis item er en instance af geographitemModel
                    if (item instanceof GeographyItemModel) {
                        var geographName = item.nameProperty().get().toLowerCase();
                        if (geographName.contains(lowercase))
                            return true;
                    }

                    /* tjek om nogle af lande navnene fra geographItemModel indeholder søgeordet
                    return item.countries().stream()
                            .map(CountryItemModel::nameProperty)
                            .map(StringProperty::get)
                            .anyMatch(countryName -> countryName.toLowerCase().contains(lowercase));*/
                    return false;
                };

                Predicate<IGeographyItemModel> countriesPredicate = (item) -> (item instanceof CountryItemModel) && name.contains(lowercase);

                if (geographies.isSelected())
                    return geographiesPredicate.test(iGeographyItemModel);
                 else if (countries.isSelected())
                    return countriesPredicate.test(iGeographyItemModel);
                 else
                    return name.contains(lowercase);
            };

            filteredGeographyItemModels.setPredicate(searchPredicate);
        });
    }

    private void setupChangeList() {
        filteredGeographyItemModels.addListener((ListChangeListener<IGeographyItemModel>) change -> {
            if (change.next()) {
                if (change.wasAdded()) {
                    customTableView.updatePaginationTable(0);
                }
            }
        });
    }

    private void setupButtonFilters() {
        all.textProperty().bind(LocalizedText.ALL);
        geographies.textProperty().bind(LocalizedText.GEOGRAPHIES);
        countries.textProperty().bind(LocalizedText.COUNTRIES);

        all.setOnAction(e -> {
            Predicate<IGeographyItemModel> allPredicate = item -> true;
            filteredGeographyItemModels.setPredicate(allPredicate);
            customTableView.updatePaginationTable(0);
            search.set("");
        });

        geographies.setOnAction(e -> {
            Predicate<IGeographyItemModel> isCountryPredicate = item -> !item.isCountryProperty().get();
            filteredGeographyItemModels.setPredicate(isCountryPredicate);
            customTableView.updatePaginationTable(0);
            search.set("");
        });

        countries.setOnAction(e -> {
            Predicate<IGeographyItemModel> isNotcountry = item -> item.isCountryProperty().get();
            filteredGeographyItemModels.setPredicate(isNotcountry);
            customTableView.updatePaginationTable(0);
            search.set("");
        });
    }

    @Override
    public Region build() {
        var results = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setVgrow(results, Priority.ALWAYS);
        var header = new HBox(
                Informative.pageHeader(LocalizedText.GEOGRAPHY, LocalizedText.PAGE_GEOGRAPHY_DESCRIPTION, Icons.GEOGRAPHY),
                new Spacer(),
                Informative.headerStats(LocalizedText.GEOGRAPHIES, model.numGeographies(), CssClasses.ORANGE, false),
                Informative.headerSeparator(),
                Informative.headerStats(LocalizedText.COUNTRIES, model.numCountries(), CssClasses.GREEN, true)
                );

        var stats = Informative.statsContainer();
        stats.getChildren().addAll(
                //         Informative.statsBoxNew(LocalizedText.TOTAL_HOURLY_RATE, BindingsUtils.createCurrencyStringBinding(model.totalHourlyRate()), CssClasses.BLUE, Feather.BAR_CHART),
                //         Informative.statsBoxNew(LocalizedText.TOTAL_DAY_RATE, BindingsUtils.createCurrencyStringBinding(model.totalDayRate()), CssClasses.GREEN, Feather.BAR_CHART_2),
                //         Informative.statsBoxNew(LocalizedText.UNIQUE_TEAMS, model.uniqueTeams(), CssClasses.ORANGE, Icons.TEAMS),
                //         Informative.statsBoxNew(LocalizedText.UNIQUE_LOCATIONS, model.uniqueGeographies(), CssClasses.RED, Icons.GEOGRAPHY)
        );

        var content = new VBox(LayoutConstants.STANDARD_SPACING);
        content.setPadding(new Insets(LayoutConstants.STANDARD_PADDING ));

        var addGeographyBtn = Buttons.actionButton(
                LocalizedText.ADD_NEW_GEOGRAPHY,
                Icons.ADD,
                event -> onAddGeography.run(),
                Styles.SUCCESS);

        var comboRows = CustomComboBox.rowsPerPageComboBox();
        comboRows.setOnAction(e -> {
            customTableView.setItemsPerPage(comboRows.getValue());
        });
        var searchField = Fields.searchFieldGroup(SEARCH_FIELD_WIDTH);
        searchField.textProperty().bindBidirectional(search);
        searchField.getStyleClass().add(CssClasses.INPUT_GROUP_TEXT);

        all.setSelected(true);

        var tableViewHeaderBottom = new HBox(LayoutConstants.STANDARD_SPACING, comboRows, new Spacer(), new InputGroup(filterOptions, searchField));
        var tableViewHeader = new VBox(LayoutConstants.STANDARD_SPACING, tableViewHeaderBottom);

        customTableView = new CustomTableView<>(comboRows.getValue(), geographyItemModels);

        TableColumn<IGeographyItemModel, String> nameColumn = customTableView.createColumn(LocalizedText.NAME, IGeographyItemModel::nameProperty);
        TableView<IGeographyItemModel> tableViewWithPagination = customTableView.setupPagination(customTableView.createTableView());
        geographyItemModels.comparatorProperty().bind(tableViewWithPagination.comparatorProperty());

//        ContextMenu contextMenu = createContextMenu(menuItemInfos, tableViewWithPagination);

//        TableColumn<IGeographyItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableViewWithPagination, contextMenu);

      //  countriesColumn.setMinWidth(100);
//        optionsColumn.setMaxWidth(50);
//        optionsColumn.setMinWidth(50);

        tableViewWithPagination.getStyleClass().add(Styles.STRIPED);

        customTableView.addColumnsToPagination(Arrays.asList(nameColumn));

        Pagination pagination = customTableView.getPagination();

        VBox.setVgrow(tableViewWithPagination, Priority.ALWAYS);
        VBox.setVgrow(pagination, Priority.ALWAYS);

        setupButtonFilters();

        content.getChildren().addAll(tableViewHeader, pagination);
        content.getStyleClass().addAll(Styles.BG_DEFAULT, CssClasses.ROUNDED_HALF, Styles.BORDER_SUBTLE);

        var worldStack = new MapWidget();
        var world = worldStack.worldWidget();

        Runnable onSelected = () -> {
            IGeographyItemModel item = tableViewWithPagination.getSelectionModel().getSelectedItem();
            if (item != null) {
                if (item instanceof CountryItemModel) {
                    Optional<Country> hansoloCountry = Country.fromIso2(((CountryItemModel) item).codeProperty().get());
                    hansoloCountry.ifPresent(country -> world.highlightGeography(List.of(country)));
                }

                if (item instanceof GeographyItemModel) {
                    List<ecostruxure.rate.calculator.be.Country> countries = ((GeographyItemModel) item).countryList();
                    List<Country> hansoloCountries = new ArrayList<>();
                    for (ecostruxure.rate.calculator.be.Country country : countries) {
                        Optional<Country> optionalCountry = Country.fromIso2(country.code());
                        optionalCountry.ifPresent(hansoloCountries::add);
                    }
                    world.highlightGeography(hansoloCountries);
                }

            }
        };

        customTableView.addRowClickHandler(tableViewWithPagination, null, onSelected, () -> {});


        VBox.setVgrow(world, Priority.ALWAYS);
        var controls = new HBox(LayoutConstants.STANDARD_SPACING, new Spacer(), addGeographyBtn);
        controls.setPadding(new Insets(0, LayoutConstants.CONTENT_PADDING, 0, LayoutConstants.CONTENT_PADDING));

        var contentBox = new HBox(LayoutConstants.STANDARD_SPACING, worldStack, content);
        HBox.setHgrow(content, Priority.ALWAYS);
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        VBox.setMargin(contentBox, new Insets(0, LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING, LayoutConstants.CONTENT_PADDING));

        HBox.setHgrow(worldStack, Priority.NEVER);
        results.getChildren().addAll(header, stats, controls, contentBox);

        return results;
    }

    public StringProperty searchProperty() {
        return search;
    }
}
