package ecostruxure.rate.calculator.gui.util;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class NodeUtils {
    public static void bindVisibility(Node node, ObservableValue<Boolean> condition) {
        node.visibleProperty().bind(condition);
        node.managedProperty().bind(condition);
    }

    public static void bindVisibility(MenuItem menuItem, ObservableValue<Boolean> condition) {
        menuItem.visibleProperty().bind(condition);
    }

    public static void bindDisable(MenuItem menuItem, ObservableValue<Boolean> condition) {
        menuItem.disableProperty().bind(condition);
    }

    public static void bindVisibility(SeparatorMenuItem separatorMenuItem, ObservableValue<Boolean> condition) {
        separatorMenuItem.visibleProperty().bind(condition);
    }
}
