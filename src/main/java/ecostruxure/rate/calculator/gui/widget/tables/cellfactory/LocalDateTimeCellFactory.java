package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeCellFactory<T> implements Callback<TableColumn<T, LocalDateTime>, TableCell<T, LocalDateTime>> {
    private static final String DATE_TIME_PATTERN = "dd. MMM yyyy HH:mm";
    private final ObjectProperty<LocalDateTime> currentDateTime;
    private final ObjectProperty<Locale> localeProperty = LocalizedText.CURRENT_LOCALE;
    private final StringProperty currentText = LocalizedText.CURRENT;
    private DateTimeFormatter formatter;

    public LocalDateTimeCellFactory(ObjectProperty<LocalDateTime> currentDateTime) {
        this.formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).withLocale(localeProperty.get());
        this.currentDateTime = currentDateTime;
    }

    @Override
    public TableCell<T, LocalDateTime> call(TableColumn<T, LocalDateTime> param) {
        return new TableCell<>() {
            {
                currentText.addListener((obs, ov, nv) -> updateText(getItem(), isEmpty()));
                localeProperty.addListener((obs, ov, nv) -> {
                    formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).withLocale(nv);
                    updateAllCells();
                });
            }

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                updateText(item, empty);
            }

            private void updateText(LocalDateTime item, boolean empty) {
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item.equals(currentDateTime.get())) setText(currentText.get());
                    else setText(item.format(formatter));
                }
            }

            private void updateAllCells() {
                getTableView().refresh();
            }
        };
    }
}
