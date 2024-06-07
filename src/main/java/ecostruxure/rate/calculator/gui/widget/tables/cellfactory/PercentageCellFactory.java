package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PercentageCellFactory<T, S> implements Callback<TableColumn<T, S>, TableCell<T, S>> {
    public PercentageCellFactory() {
    }

    @Override
    public TableCell<T, S> call(TableColumn<T, S> param) {
        return new TableCell<>() {
            @Override
            protected void updateItem(S item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString() + "%");
                }
            }
        };
    }
}
