package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import ecostruxure.rate.calculator.be.TeamHistory.Reason;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


public class ReasonCellFactory<T> implements Callback<TableColumn<T, Reason>, TableCell<T, Reason>> {

    private static ObservableValue<String> reasonToStringProperty(Reason reason) {
        return switch (reason) {
            case TEAM_CREATED -> LocalizedText.REASON_TEAM_CREATED;
            case ASSIGNED_PROFILE -> LocalizedText.REASON_ASSIGNED_PROFILE;
            case REMOVED_PROFILE -> LocalizedText.REASON_REMOVED_PROFILE;
            case UPDATED_PROFILE -> LocalizedText.REASON_UPDATED_PROFILE;
            case UTILIZATION_CHANGE -> LocalizedText.REASON_UTILIZATION_CHANGE;
        };
    }

    @Override
    public TableCell<T, Reason> call(TableColumn<T, Reason> param) {
        return new TableCell<>() {
            private ObservableValue<String> boundValue;

            @Override
            protected void updateItem(Reason item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    if (boundValue != null) {
                        textProperty().unbind();
                        boundValue = null;
                    }
                    setText(null);
                } else {
                    ObservableValue<String> newBoundValue = reasonToStringProperty(item);
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