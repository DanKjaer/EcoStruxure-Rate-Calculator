package ecostruxure.rate.calculator.gui.widget.tables.cellfactory;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import javafx.scene.control.*;
import javafx.geometry.Side;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;


public class ButtonCellFactory<T> implements Callback<TableColumn<T, Void>, TableCell<T, Void>> {

    private final TableView<T> tableView;
    private final ContextMenu contextMenu;

    public ButtonCellFactory(TableView<T> tableView, ContextMenu contextMenu) {
        this.tableView = tableView;
        this.contextMenu = contextMenu;
    }

    @Override
    public TableCell<T, Void> call(TableColumn<T, Void> param) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Opretter de 3 dots knap
                    Button button = new Button(null, new FontIcon(Icons.DOTS));
                    button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT, CssClasses.ACTIONABLE);
                    //button.setPadding(new Insets(10, 0, 10, 0));

                    // På action gør:
                    button.setOnAction(event -> {
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(getIndex());
                        // Vis contextMenu hvis den ikke viser sig, ellers skjul.
                        if (!contextMenu.isShowing()) contextMenu.show(button, Side.BOTTOM, 0, 0);
                        else contextMenu.hide();
                    });

                    setGraphic(button);
                }
            }
        };
    }
}
