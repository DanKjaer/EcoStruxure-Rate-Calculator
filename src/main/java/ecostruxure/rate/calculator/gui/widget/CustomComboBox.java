package ecostruxure.rate.calculator.gui.widget;

import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class CustomComboBox {

    public static ComboBox<Integer> rowsPerPageComboBox() {
        var comboBox = new ComboBox<Integer>();
        comboBox.getItems().addAll(10, 25, 50, 100);
        comboBox.getSelectionModel().select(2);
        comboBox.getStyleClass().add(CssClasses.FIX_CURSOR_HOVER);
        comboBox.setPrefWidth(75);
        comboBox.setFocusTraversable(false);

        return comboBox;
    }

    public static ComboBox<String> filterOptions() {
        var filterOptions = new ComboBox<String>();

        filterOptions.itemsProperty().addListener((obs, oldItems, newItems) -> {
            if (newItems != null && !newItems.isEmpty())
                filterOptions.getSelectionModel().select(1);
        });

        filterOptions.itemsProperty().bind(Bindings.createObjectBinding(() ->
                        FXCollections.observableArrayList(LocalizedText.ALL.get(), LocalizedText.ACTIVE.get(), LocalizedText.ARCHIVED.get()),
                LocalizedText.ALL, LocalizedText.ACTIVE, LocalizedText.ARCHIVE));

        filterOptions.setPrefWidth(125);
        filterOptions.getStyleClass().add(CssClasses.FIX_CURSOR_HOVER);

        return filterOptions;
    }

    public static ComboBox<String> geographiesOptions() {
        var filterOptions = new ComboBox<String>();

        filterOptions.itemsProperty().addListener((obs, oldItems, newItems) -> {
            if (newItems != null && !newItems.isEmpty())
                filterOptions.getSelectionModel().select(0);
        });

        filterOptions.itemsProperty().bind(Bindings.createObjectBinding(() ->
                        FXCollections.observableArrayList(LocalizedText.ALL.get(), LocalizedText.GEOGRAPHIES.get(), LocalizedText.COUNTRIES.get()),
                LocalizedText.ALL, LocalizedText.GEOGRAPHIES, LocalizedText.COUNTRIES));

        filterOptions.setPrefWidth(125);
        filterOptions.getStyleClass().add(CssClasses.FIX_CURSOR_HOVER);

        return filterOptions;
    }
}
