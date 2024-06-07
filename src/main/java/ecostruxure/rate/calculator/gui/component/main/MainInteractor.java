package ecostruxure.rate.calculator.gui.component.main;

import atlantafx.base.theme.*;
import ecostruxure.rate.calculator.bll.MissingCurrenciesException;
import ecostruxure.rate.calculator.bll.service.CurrencyService;
import ecostruxure.rate.calculator.gui.common.FetchError;
import ecostruxure.rate.calculator.gui.component.main.MainModel.ThemeMode;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager.CurrencyType;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.util.AppConfig;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainInteractor {
    private final MainModel model;
    private ThemeMode theme;
    private Locale locale;
    private ResourceBundle languageBundle;
    private CurrencyService currencyService;
    private CurrencyType targetCurrency = CurrencyType.EUR;
    private BigDecimal rate = BigDecimal.ONE;
    private Pair<Theme, Theme> savedTheme;

    public MainInteractor(MainModel model, Runnable onFetchError) {
        this.model = model;

        try {
            currencyService = new CurrencyService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean fetchSettings() {
        return fetchTheme() && fetchLanguage();
    }

    public boolean changeThemeMode() {
        try {
            var properties = AppConfig.load(AppConfig.SETTINGS_FILE);
            var currentTheme = model.activeThemeModeProperty().get();
            var theme = currentTheme == ThemeMode.LIGHT ? ThemeMode.DARK : ThemeMode.LIGHT;

            properties.setProperty(AppConfig.USER_THEME_MODE, theme == ThemeMode.LIGHT ? AppConfig.USER_THEME_LIGHT : AppConfig.USER_THEME_DARK);
            AppConfig.save(AppConfig.SETTINGS_FILE, properties);

            this.theme = theme;

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean changeLanguage() {
        try {
            var properties = AppConfig.load(AppConfig.SETTINGS_FILE);
            var currentLocale = model.activeLocaleProperty().get();
            var locale = currentLocale.equals(Locale.ENGLISH) ? Locale.of("da") : Locale.ENGLISH;

            properties.setProperty(AppConfig.USER_LANGUAGE, locale.getLanguage());
            AppConfig.save(AppConfig.SETTINGS_FILE, properties);

            this.locale = locale;
            languageBundle = ResourceBundle.getBundle(AppConfig.MESSAGES_BUNDLE, locale);

            LocalizedText.CURRENT_LOCALE.set(locale);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean changeCurrency() {
        try {
            if (CurrencyManager.currencyTypeProperty().get() == CurrencyType.EUR) {
                rate = currencyService.get("USD").eurConversionRate();
                targetCurrency = CurrencyType.USD;
            } else {
                rate = BigDecimal.ONE;
                targetCurrency = CurrencyType.EUR;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public FetchError importCurrencies(File file) {
        try {
            currencyService.importCurrenciesFromCSV(file.getAbsolutePath());
            targetCurrency = CurrencyManager.currencyTypeProperty().get();
            if (targetCurrency == CurrencyType.USD) rate = currencyService.get("USD").eurConversionRate();
            else rate = BigDecimal.ONE;
            return new FetchError(true, LocalizedText.SUCCESS_IMPORT_CURRENCIES);
        } catch (Exception e) {
            if (e instanceof MissingCurrenciesException) {
                return new FetchError(false, LocalizedText.ERROR_MISSING_CURRENCIES);
            }
            return new FetchError(false, LocalizedText.ERROR_IMPORT_CURRENCIES);
        }
    }

    public void updateSettings() {
        model.activeThemeProperty().set(savedTheme);
        updateTheme();
        updateLanguage();
        updateCurrency();
    }

    public void updateTheme() {
        model.activeThemeModeProperty().set(theme);
        model.okToChangeThemeProperty().set(true);
    }

    public void updateLanguage() {
        model.activeLocaleProperty().set(locale);
        LocalizedText.loadBundle(languageBundle);
        model.okToChangeLanguageProperty().set(true);
    }

    public void updateCurrency() {
        if (targetCurrency == CurrencyType.EUR) CurrencyManager.switchToEUR();
        else CurrencyManager.switchToUSD(rate);
        model.okToChangeCurrencyProperty().set(true);
    }

    private boolean fetchTheme() {
        try {
            var savedThemeMode = AppConfig.load(AppConfig.SETTINGS_FILE).get(AppConfig.USER_THEME_MODE);
            if (savedThemeMode == null) return false;

            if (savedThemeMode.equals(AppConfig.USER_THEME_LIGHT)) theme = ThemeMode.LIGHT;
            else theme = ThemeMode.DARK;

            var savedTheme = AppConfig.load(AppConfig.SETTINGS_FILE).get(AppConfig.USER_THEME);
            if (savedTheme == null) return false;

            if (savedTheme.equals(AppConfig.USER_THEME_PRIMER)) {
                this.savedTheme = new Pair<>(new PrimerLight(), new PrimerDark());
            } else if (savedTheme.equals(AppConfig.USER_THEME_NORD)) {
                this.savedTheme = new Pair<>(new NordLight(), new NordDark());
            } else if (savedTheme.equals(AppConfig.USER_THEME_CUPERTINO)) {
                this.savedTheme = new Pair<>(new CupertinoLight(), new CupertinoDark());
            } else {
                this.savedTheme = new Pair<>(new PrimerLight(), new PrimerDark());
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean fetchLanguage() {
        try {
            var savedLanguage = AppConfig.load(AppConfig.SETTINGS_FILE).get(AppConfig.USER_LANGUAGE);
            if (savedLanguage == null) return false;

            locale = Locale.of((String) savedLanguage);
            languageBundle = ResourceBundle.getBundle(AppConfig.MESSAGES_BUNDLE, locale);

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
