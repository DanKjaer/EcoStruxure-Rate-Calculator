package ecostruxure.rate.calculator.gui.widget.tables.contextmenu;

import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import org.kordamp.ikonli.Ikon;

import java.util.function.Consumer;

public class MenuItemInfo<T> {
    private Ikon icon;
    private final StringProperty text;
    private final Consumer<T> action;
    private final KeyCombination keyCombination;
    private final boolean isSeparator;

    private SeparatorMenuItem separatorMenuItem;
    private MenuItem createdItem;

    public MenuItemInfo(Ikon icon, StringProperty text, Consumer<T> action) {
        this.icon = icon;
        this.text = text;
        this.action = action;
        this.keyCombination = null;
        this.isSeparator = false;
        separatorMenuItem = null;
    }

    public MenuItemInfo(Ikon icon, StringProperty text, Consumer<T> action, KeyCombination keyCombination) {
        this.icon = icon;
        this.text = text;
        this.action = action;
        this.keyCombination = keyCombination;
        this.isSeparator = false;
        separatorMenuItem = null;
    }

    // Constructor for SeparatorMenuItem
    public MenuItemInfo(boolean isSeparator) {
        this.icon = null;
        this.text = null;
        this.action = null;
        this.keyCombination = null;
        this.isSeparator = isSeparator;
        separatorMenuItem = null;
    }

    public Ikon icon() {
        return icon;
    }

    public void icon(Ikon ikon) {
        this.icon = icon;
    }
    public StringProperty text() {
        return text;
    }

    public Consumer<T> action() {
        return action;
    }

    public KeyCombination keyCombination() {
        return keyCombination;
    }

    public boolean separator() {
        return isSeparator;
    }

    public SeparatorMenuItem separatorMenuItem() {
        return separatorMenuItem;
    }

    public void separatorMenuItem(SeparatorMenuItem separatorMenuItem) {
        this.separatorMenuItem = separatorMenuItem;
    }

    public MenuItem menuItem() {
        return createdItem;
    }

    public void menuItem(MenuItem item) {
        createdItem = item;
    }
}
