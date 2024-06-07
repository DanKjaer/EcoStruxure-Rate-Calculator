package ecostruxure.rate.calculator.gui.component.geography.country;

import ecostruxure.rate.calculator.gui.component.geography.IGeographyItemModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;

public class CountryItemModel implements IGeographyItemModel {
    private final StringProperty code = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty isCountry = new SimpleBooleanProperty(true);
    private final ObjectProperty<BigDecimal> latitude = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> longitude = new SimpleObjectProperty<>();

    @Override
    public StringProperty idProperty() {
        return code;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public BooleanProperty isCountryProperty() {
        return isCountry;
    }

    public StringProperty codeProperty() {
        return code;
    }

    public ObjectProperty<BigDecimal> latitudeProperty() {
        return latitude;
    }

    public ObjectProperty<BigDecimal> longitudeProperty() {
        return longitude;
    }

}
