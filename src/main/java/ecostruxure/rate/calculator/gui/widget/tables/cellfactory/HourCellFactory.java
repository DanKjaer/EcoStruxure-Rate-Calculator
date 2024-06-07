package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import ecostruxure.rate.calculator.gui.util.NumberUtils;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.math.BigDecimal;

public class HourCellFactory<T> implements Callback<TableColumn<T, BigDecimal>, TableCell<T, BigDecimal>> {
    private final StringProperty hourSymbol = LocalizedText.HOUR_SYMBOL_LOWERCASE;

    @Override
    public TableCell<T, BigDecimal> call(TableColumn<T, BigDecimal> param) {

        return new TableCell<>() {
            {
                hourSymbol.addListener((obs, ov, nv) -> updateText(getItem(), isEmpty()));
            }

            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                updateText(item, empty);
            }

            private void updateText(BigDecimal item, boolean empty) {
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(NumberUtils.formatAsInteger(item) + hourSymbol.get());
                }
            }
        };
    }
}
