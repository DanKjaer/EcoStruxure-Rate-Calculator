package ecostruxure.rate.calculator.gui.component.geography;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GeographyModel {
    private final ObservableList<IGeographyItemModel> all = FXCollections.observableArrayList();

    private final ObservableList<IGeographyItemModel> geographies = FXCollections.observableArrayList();
    private final ObservableList<IGeographyItemModel> countries = FXCollections.observableArrayList();

    private final StringProperty numGeographies = new SimpleStringProperty();
    private final StringProperty numCountries = new SimpleStringProperty();

    public ObservableList<IGeographyItemModel> all() {
        return all;
    }

    public ObservableList<IGeographyItemModel> geographies() {
        return geographies;
    }

    public ObservableList<IGeographyItemModel> countries() {
        return countries;
    }

    public StringProperty numGeographies() {
        return numGeographies;
    }

    public StringProperty numCountries() {
        return numCountries;
    }
}
