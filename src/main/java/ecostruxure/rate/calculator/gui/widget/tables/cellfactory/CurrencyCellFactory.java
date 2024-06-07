package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import ecostruxure.rate.calculator.gui.system.currency.CurrencyFormatter;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager;
import javafx.beans.InvalidationListener;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.math.BigDecimal;

public class CurrencyCellFactory<T> implements Callback<TableColumn<T, BigDecimal>, TableCell<T, BigDecimal>> {

    @Override
    public TableCell<T, BigDecimal> call(TableColumn<T, BigDecimal> param) {
        return new TableCell<>() {
            private final InvalidationListener currencyChangeListener = obs -> updateText();

            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                CurrencyManager.currencySymbolProperty().removeListener(currencyChangeListener);

                if (empty || item == null) {
                    setText(null);
                } else {
                    updateText();
                    CurrencyManager.currencySymbolProperty().addListener(currencyChangeListener);
                }
            }

            private void updateText() {
                BigDecimal item = getItem();
                if (item != null) {
                    setText(CurrencyFormatter.formatCurrency(item));
                }
            }
        };
    }
}