package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


public class IsArchivedCellFactory<T> implements Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>> {

    private static ObservableValue<String> isArchivedToStringProperty(Boolean isArchived) {
        return isArchived ? LocalizedText.ARCHIVED : LocalizedText.ACTIVE;
    }

    @Override
    public TableCell<T, Boolean> call(TableColumn<T, Boolean> param) {
        return new TableCell<>() {
            private ObservableValue<String> boundValue;

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    if (boundValue != null) {
                        textProperty().unbind();
                        boundValue = null;
                    }
                    setText(null);
                } else {
                    ObservableValue<String> newBoundValue = isArchivedToStringProperty(item);
                    if (boundValue != newBoundValue) {
                        if (boundValue != null) {
                            textProperty().unbind();
                        }
                        boundValue = newBoundValue;
                        textProperty().bind(boundValue);
                    }
                }
            }
        };
    }
}
