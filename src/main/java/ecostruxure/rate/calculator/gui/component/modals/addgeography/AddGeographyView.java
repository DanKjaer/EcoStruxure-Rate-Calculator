package ecostruxure.rate.calculator.gui.component.modals.addgeography;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.property.ValidationProperty;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import ecostruxure.rate.calculator.gui.widget.listview.CheckImageListCell;
import eu.hansolo.fx.countries.flag.Flag;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class AddGeographyView implements View {
    private final AddGeographyModel model;
    private final Runnable onCheckIfGeographyExists;
    private final Consumer<List<Node>> tabOrderConsumer;

    public AddGeographyView(AddGeographyModel model, Runnable onCheckIfGeographyExists, Consumer<List<Node>> tabOrderConsumer) {
        this.model = model;
        this.onCheckIfGeographyExists = onCheckIfGeographyExists;
        this.tabOrderConsumer = tabOrderConsumer;
    }

    @Override
    public Region build() {

        var nameField = Fields.boundValidatedTextField(model.nameProperty(), model.nameIsValidProperty(), 100);
        ToggleButton toggleShowSelected = new ToggleButton();
        toggleShowSelected.getStyleClass().add("show-selected-button");
        var nameToggleBox = new HBox(LayoutConstants.STANDARD_SPACING, nameField, toggleShowSelected);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        var nameBox = new VBox(Labels.bound(LocalizedText.GEOGRAPHY_NAME), nameToggleBox);

        var searchField = Fields.searchFieldWithDelete(Integer.MAX_VALUE, model.searchProperty());
        searchField.textProperty().bindBidirectional(model.searchProperty());
        VBox.setVgrow(searchField, Priority.NEVER);

        var selectedCountry = Labels.bound(model.selectedGeographyNameProperty());
        ValidationProperty validationProperty = new ValidationProperty(selectedCountry, Styles.STATE_SUCCESS, Styles.STATE_DANGER, model.validGeographyProperty().get());
        model.validGeographyProperty().addListener((obs, ov, nv) -> {
            validationProperty.set(nv);
       });

        var listView = new ListView<>(model.filteredCountries());
        listView.setCellFactory(lv -> new CheckImageListCell<>(
                CountryInfo::selectedProperty,
                country -> Flag.iso2(country.codeProperty().get()).getImage(),
                CountryInfo::nameProperty
        ));
        HBox.setHgrow(listView, Priority.ALWAYS);

        toggleShowSelected.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv) listView.setItems(model.selectedCountries());
            else listView.setItems(model.filteredCountries());
        });

        toggleShowSelected.textProperty().bind(
                Bindings.createStringBinding(() -> toggleShowSelected.isSelected() ? "Show All" : "Show Selected (" + model.selectedCountries().size() + ")", toggleShowSelected.selectedProperty(),
                model.selectedCountries())
        );

        listView.getItems().forEach(item -> item.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv && !model.selectedCountries().contains(item)) model.selectedCountries().add(item);
            else if (!nv) model.selectedCountries().remove(item);
        }));
        listView.getStyleClass().add("check-image-list-view");
        listView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (listView.getSelectionModel().getSelectedItem() != null) {
                    listView.getSelectionModel().getSelectedItem().selectedProperty().set(!listView.getSelectionModel().getSelectedItem().selectedProperty().get());
                    event.consume();
                }
            }
        });

        model.selectedCountries().addListener((ListChangeListener<? super CountryInfo>) change -> onCheckIfGeographyExists.run());


        VBox.setVgrow(listView, Priority.ALWAYS);

        tabOrderConsumer.accept(List.of(nameField, toggleShowSelected, searchField, listView));
        return new VBox(LayoutConstants.STANDARD_SPACING, nameBox, searchField, listView, selectedCountry);
    }
}