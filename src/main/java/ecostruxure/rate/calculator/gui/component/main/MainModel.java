package ecostruxure.rate.calculator.gui.component.main;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;
import org.kordamp.ikonli.Ikon;

import java.util.Locale;

public class MainModel {
    public enum ThemeMode {
        LIGHT,
        DARK
    }

    private final ObjectProperty<Class<? extends Controller>> activeControllerClass = new SimpleObjectProperty<>();
    private final ObjectProperty<Locale> activeLocale = new SimpleObjectProperty<>(Locale.ENGLISH);
    private final ObjectProperty<ThemeMode> activeThemeMode = new SimpleObjectProperty<>(ThemeMode.LIGHT);
    private final ObjectProperty<Pair<Theme, Theme>> activeTheme = new SimpleObjectProperty<>(new Pair<>(new PrimerLight(), new PrimerDark()));
    private final ObjectProperty<Ikon> themeIcon = new SimpleObjectProperty<>(Icons.LIGHT_MODE);
    private final BooleanProperty okToChangeTheme = new SimpleBooleanProperty(false);
    private final BooleanProperty okToChangeLanguage = new SimpleBooleanProperty(false);
    private final BooleanProperty okToChangeCurrency = new SimpleBooleanProperty(false);

    public ObjectProperty<Class<? extends Controller>> activeControllerClassProperty() {
        return activeControllerClass;
    }
    public ObjectProperty<Locale> activeLocaleProperty() {
        return activeLocale;
    }

    public ObjectProperty<ThemeMode> activeThemeModeProperty() {
        return activeThemeMode;
    }

    public ObjectProperty<Pair<Theme, Theme>> activeThemeProperty() {
        return activeTheme;
    }

    public ObjectProperty<Ikon> themeIconProperty() {
        return themeIcon;
    }

    public BooleanProperty okToChangeThemeProperty() {
        return okToChangeTheme;
    }

    public BooleanProperty okToChangeLanguageProperty() {
        return okToChangeLanguage;
    }

    public BooleanProperty okToChangeCurrencyProperty() {
        return okToChangeCurrency;
    }
}
