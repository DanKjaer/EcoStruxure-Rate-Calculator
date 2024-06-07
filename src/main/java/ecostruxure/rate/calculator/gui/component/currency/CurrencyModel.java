package ecostruxure.rate.calculator.gui.component.currency;

import ecostruxure.rate.calculator.gui.common.CurrencyItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CurrencyModel {
    private final ObservableList<CurrencyItemModel> currencies = FXCollections.observableArrayList();

    public ObservableList<CurrencyItemModel> currencies() {
        return currencies;
    }
}