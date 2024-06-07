package ecostruxure.rate.calculator.gui.widget.tables.contextmenu;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class CustomContextMenu {

    /**
     * Laver en Context Menu med given menu items og bruger rowData for at få datatype fra TableView
     *
     * @param menuItems - De menu items der skal vises i contextMenu
     * @param tableView - TableView der skal bindes sig på
     * @return - Den oprettet ContextMenu udefra given data
     */
    public static <T> ContextMenu createContextMenu(List<MenuItemInfo<T>> menuItems, TableView<T> tableView) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.getStyleClass().add(CssClasses.STATS_SHADOW);

        if (menuItems != null) {
            for (MenuItemInfo<T> menuItemInfo : menuItems) {

                // Hvis er en seperator, så tilføj den som en.
                if (menuItemInfo.separator()) {
                    var separator = new SeparatorMenuItem();
                    contextMenu.getItems().add(separator);
                    menuItemInfo.separatorMenuItem(separator);
                    continue;
                }

                // Opretter en ny MenuItem med information fra menuItemInfo
                var menuItem = createItem(menuItemInfo);
                menuItem.setMnemonicParsing(true);
                menuItem.getStyleClass().addAll(Styles.TEXT);
                menuItemInfo.menuItem(menuItem);

                menuItem.setOnAction(event -> {
                    // Få fat i rowData på den valgte tableView
                    T rowData = tableView.getSelectionModel().getSelectedItem();
                    // Pass den row til metoden som har været sendt med
                    if (rowData != null)
                        menuItemInfo.action().accept(rowData);
                });

                // Tilføj menuItem til contextMenu
                contextMenu.getItems().add(menuItem);
            }
        }

        return contextMenu;
    }

    private static MenuItem createItem(MenuItemInfo<?> menuItem) {
        var item = new MenuItem();

        item.textProperty().bind(menuItem.text());

        if (menuItem.icon() != null) {
            var icon = new FontIcon(menuItem.icon());
            icon.getStyleClass().add("menu-icon");
            item.setGraphic(icon);
        }

        if (menuItem.keyCombination() != null) {
            item.setAccelerator(menuItem.keyCombination());
        }

        return item;
    }

    public static void addKeyEventHandler(Node element, ContextMenu contextMenu) {
        element.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            for (MenuItem item : contextMenu.getItems()) {
                if (!item.isVisible() || item.isDisable())
                    continue;

                if (item.getAccelerator() != null && item.getAccelerator().match(event)) {
                    item.fire();
                    event.consume();
                    break;
                }
            }
        });
    }
}
