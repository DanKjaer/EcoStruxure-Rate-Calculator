package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpdatedAtCellFactory<T> implements Callback<TableColumn<T, Timestamp>, TableCell<T, Timestamp>> {
    private static final String DATE_TIME_PATTERN = "dd. MMM yyyy HH:mm";
    private final DateTimeFormatter formatter;

    public UpdatedAtCellFactory() {
        this.formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    }

    @Override
    public TableCell<T, Timestamp> call(TableColumn<T, Timestamp> param) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    LocalDateTime localDateTime = item.toLocalDateTime();
                    setText(localDateTime.format(formatter));
                }
            }
        };
    }
}