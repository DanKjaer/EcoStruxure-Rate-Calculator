package ecostruxure.rate.calculator.gui.component.modals.addgeography;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class AddGeographyModel {
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty search = new SimpleStringProperty("");
    private final StringProperty selectedGeographyName = new SimpleStringProperty("");
    private final ObservableList<CountryInfo> countries = FXCollections.observableArrayList();
    private final FilteredList<CountryInfo> filteredCountries = new FilteredList<>(countries, p -> true);
    private final ObservableList<CountryInfo> selectedCountries = FXCollections.observableArrayList();

    private final BooleanProperty nameIsValid = new SimpleBooleanProperty(false);
    private final BooleanProperty alreadyExists = new SimpleBooleanProperty(false);
    private final BooleanProperty validGeography = new SimpleBooleanProperty(false);
    private final BooleanProperty okToCreate = new SimpleBooleanProperty(false);

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty searchProperty() {
        return search;
    }

    public StringProperty selectedGeographyNameProperty() {
        return selectedGeographyName;
    }

    public ObservableList<CountryInfo> countries() {
        return countries;
    }

    public FilteredList<CountryInfo> filteredCountries() {
        return filteredCountries;
    }

    public ObservableList<CountryInfo> selectedCountries() {
        return selectedCountries;
    }

    public BooleanProperty nameIsValidProperty() {
        return nameIsValid;
    }

    public BooleanProperty alreadyExistsProperty() {
        return alreadyExists;
    }

    public BooleanProperty validGeographyProperty() {
        return validGeography;
    }

    public BooleanProperty okToCreateProperty() {
        return okToCreate;
    }
}