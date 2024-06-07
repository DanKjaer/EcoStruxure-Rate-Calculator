package ecostruxure.rate.calculator.gui.widget.tables.tablecell;

import ecostruxure.rate.calculator.gui.util.filter.FixedPositiveDecimalFilter;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.NumberStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class EditableBigDecimalTableCell<T> extends TextFieldTableCell<T, BigDecimal> {
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
    private final int decimalPlaces;
    private final int minValue;
    private final int maxValue;
    private TextField textField;

    public EditableBigDecimalTableCell(int decimalPlaces, int minValue, int maxValue) {
        setEditable(true);
        this.decimalPlaces = decimalPlaces;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public EditableBigDecimalTableCell(int decimalPlaces, int maxValue) {
        this(decimalPlaces, 0, maxValue);
    }

    public EditableBigDecimalTableCell() {
        this(2, 0, 100);
    }

    @Override
    public void startEdit() {
        if(editableProperty().get()){
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.requestFocus();
            }
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(formatPercentage(getItem()));
        setGraphic(null);
    }

    @Override
    public void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            if (textField != null) {
                textField.setText(formatDecimal(item));
                textField.selectAll();
            }
            setText(null);
            setGraphic(textField);
        } else {
            setText(formatPercentage(item));
            setGraphic(null);
        }
    }

    @Override
    public void commitEdit(BigDecimal item) {
        if (isEditing()) {
            super.commitEdit(item);
        } else {
            final TableView<T> table = getTableView();
            if (table != null) {
                TablePosition<T, BigDecimal> position = new TablePosition<>(getTableView(), getTableRow().getIndex(), getTableColumn());
                CellEditEvent<T, BigDecimal> editEvent = new CellEditEvent<>(table, position, TableColumn.editCommitEvent(), item);
                Event.fireEvent(getTableColumn(), editEvent);
            }
            updateItem(item, false);
            if (table != null) {
                table.edit(-1, null);
            }
        }

        setText(item != null ? formatPercentage(item) : null);
        setGraphic(null);
    }

    private void createTextField() {
        textField = new TextField();
        textField.setTextFormatter(new TextFormatter<>(new FixedPositiveDecimalFilter(decimalPlaces, minValue, maxValue)));
        textField.setText(getString());

        textField.setOnAction(evt -> {
            if (textField.getText() != null && !textField.getText().isEmpty()){
                NumberStringConverter nsc = new NumberStringConverter();
                Number n = nsc.fromString(textField.getText());
                commitEdit(BigDecimal.valueOf(n.doubleValue()));
            }
        });

        textField.setOnKeyPressed((ke) -> {
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
                cancelEdit();
            }
        });
    }

    private String getString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return getItem() == null ? "" : nf.format(getItem());
    }

    private String formatDecimal(BigDecimal value) {
        return value == null ? "" : numberFormat.toString();
    }

    private String formatPercentage(BigDecimal value) {
        return value == null ? "" : numberFormat.format(value) + "%";
    }
}