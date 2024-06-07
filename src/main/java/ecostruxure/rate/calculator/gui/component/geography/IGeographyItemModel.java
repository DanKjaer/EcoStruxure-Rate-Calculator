package ecostruxure.rate.calculator.gui.component.geography;

import ecostruxure.rate.calculator.gui.component.geography.country.CountryItemModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public interface IGeographyItemModel {
    StringProperty idProperty();
    StringProperty nameProperty();
    BooleanProperty isCountryProperty();
}
