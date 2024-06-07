package ecostruxure.rate.calculator.gui.component.geography.geograph;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.gui.component.geography.IGeographyItemModel;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class GeographyItemModel implements IGeographyItemModel {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty isCountry = new SimpleBooleanProperty();
    private final BooleanProperty predefined = new SimpleBooleanProperty();

    private final List<Country> countryList = new ArrayList<>();

    @Override
    public StringProperty idProperty() {
        return id;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public BooleanProperty isCountryProperty() {
        return isCountry;
    }

    public BooleanProperty predefinedProperty() {
        return predefined;
    }

    public List<Country> countryList() {
        return countryList;
    }
}
