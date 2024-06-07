package ecostruxure.rate.calculator.gui.component.main;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.*;
import ecostruxure.rate.calculator.gui.component.currency.CurrencyController;
import ecostruxure.rate.calculator.gui.component.geography.GeographyController;
import ecostruxure.rate.calculator.gui.component.main.MainModel.ThemeMode;
import ecostruxure.rate.calculator.gui.component.profile.ProfileController;
import ecostruxure.rate.calculator.gui.component.profiles.ProfilesController;
import ecostruxure.rate.calculator.gui.component.team.TeamController;
import ecostruxure.rate.calculator.gui.component.teams.TeamsController;
import ecostruxure.rate.calculator.gui.system.currency.CurrencyManager;
import ecostruxure.rate.calculator.gui.system.modal.ModalOverlay;
import ecostruxure.rate.calculator.gui.system.notification.NotificationPane;
import ecostruxure.rate.calculator.gui.util.*;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Dialog;
import ecostruxure.rate.calculator.gui.widget.Navigation;
import ecostruxure.rate.calculator.gui.widget.atlanta.SamplerTheme;
import ecostruxure.rate.calculator.gui.widget.ThemeThumbnail;
import ecostruxure.rate.calculator.util.AppConfig;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MainView implements View {
    private static final double TOP_BAR_BORDER_WIDTH = 0.5;
    private static final int TOP_BAR_HEIGHT = 72;
    private static final int SIDE_BAR_WIDTH = 250;
    private static final int SEARCH_FIELD_WIDTH = 265;

    private final Scene scene;
    private final MainModel model;
    private final ModalOverlay modalOverlay;
    private final NotificationPane notificationPane;
    private final Node dropTarget;
    private final Node themeChooserDialog;

    private final Region geographyView;
    private final Region profilesView;
    private final Region profileView;
    private final Region teamsView;
    private final Region teamView;
    private final Region currencyView;

    private final Runnable onShowGeography;
    private final Runnable onShowProfiles;
    private final Runnable onShowTeams;
    private final Runnable onShowCurrency;
    private final Consumer<Runnable> onChangeTheme;
    private final Consumer<Runnable> onChangeLanguage;
    private final Consumer<Runnable> onChangeCurrency;

    private final TilePane themeThumbnails = new TilePane(20, 20);
    private final ToggleGroup thumbnailsGroup = new ToggleGroup();
    private boolean grabThemesOnce;

    private final ThemeThumbnail primerLight = createThemeThumbnail(new SamplerTheme(new PrimerLight()));
    private final ThemeThumbnail primerDark = createThemeThumbnail(new SamplerTheme(new PrimerDark()));
    private final ThemeThumbnail nordLight = createThemeThumbnail(new SamplerTheme(new NordLight()));
    private final ThemeThumbnail nordDark = createThemeThumbnail(new SamplerTheme(new NordDark()));
    private final ThemeThumbnail cupertinoLight = createThemeThumbnail(new SamplerTheme(new CupertinoLight()));
    private final ThemeThumbnail cupertinoDark = createThemeThumbnail(new SamplerTheme(new CupertinoDark()));


    public MainView(Scene scene,
                    MainModel model,
                    ModalOverlay modalOverlay, NotificationPane notificationPane, Node dropTarget,
                    MainRegions regions,
                    MainActions actions) {
        this.scene = scene;
        this.model = model;
        this.modalOverlay = modalOverlay;
        this.notificationPane = notificationPane;
        this.dropTarget = dropTarget;

        this.geographyView = regions.geographyView();
        this.profilesView = regions.profilesView();
        this.profileView = regions.profileView();
        this.teamsView = regions.teamsView();
        this.teamView = regions.teamView();
        this.currencyView = regions.currencyView();

        this.onShowGeography = actions.onShowGeography();
        this.onShowProfiles = actions.onShowProfiles();
        this.onShowTeams = actions.onShowTeams();
        this.onShowCurrency = actions.onShowCurrency();
        this.onChangeTheme = actions.onChangeTheme();
        this.onChangeLanguage = actions.onChangeLanguage();
        this.onChangeCurrency = actions.onChangeCurrency();

        this.themeChooserDialog = themeChooserDialog();
    }

    @Override
    public Region build() {
        var results = new StackPane();
        results.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/css/style.css")).toExternalForm());
        results.getStyleClass().add(Styles.BG_SUBTLE);

        var main = new BorderPane();
        main.getStyleClass().add("main");
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        main.pseudoClassStateChanged(CssClasses.LIGHT, true);
        setupThemeListener(main);

        main.setLeft(sidebar());

        var right = new BorderPane();
        right.setTop(topbar());

        right.setCenter(new StackPane(content()));

        main.setCenter(right);

        results.getChildren().addAll(main, modalOverlay, notificationPane, dropTarget);

        notificationPane.setViewOrder(-100); // For at tvinge at notifikationer er foran overlay
        notificationPane.yPosition(TOP_BAR_HEIGHT + TOP_BAR_BORDER_WIDTH);

        StackPane.setAlignment(main, Pos.TOP_LEFT);
        return results;
    }

    private void setupThemeListener(Node node) {
        model.activeThemeModeProperty().addListener((obs, ov, nv) -> {
            if (nv == ThemeMode.LIGHT) {
                node.pseudoClassStateChanged(CssClasses.LIGHT, true);
                node.pseudoClassStateChanged(CssClasses.DARK, false);
                model.themeIconProperty().set(Icons.LIGHT_MODE);
                Application.setUserAgentStylesheet(model.activeThemeProperty().get().getKey().getUserAgentStylesheet());
            } else {
                node.pseudoClassStateChanged(CssClasses.DARK, true);
                node.pseudoClassStateChanged(CssClasses.LIGHT, false);
                model.themeIconProperty().set(Icons.DARK_MODE);
                Application.setUserAgentStylesheet(model.activeThemeProperty().get().getValue().getUserAgentStylesheet());
            }

            updateThumbnails();
        });

        model.activeThemeProperty().addListener((obs, ov, nv) -> {
            var light = nv.getKey();
            var dark = nv.getValue();

            try {
                var properties = AppConfig.load(AppConfig.SETTINGS_FILE);

                if (light instanceof PrimerLight) {
                    properties.setProperty(AppConfig.USER_THEME, AppConfig.USER_THEME_PRIMER);
                } else if (light instanceof NordLight) {
                    properties.setProperty(AppConfig.USER_THEME, AppConfig.USER_THEME_NORD);
                } else {
                    properties.setProperty(AppConfig.USER_THEME, AppConfig.USER_THEME_CUPERTINO);
                }

                if (model.activeThemeModeProperty().get().equals(ThemeMode.LIGHT)) {
                    properties.setProperty(AppConfig.USER_THEME_MODE, AppConfig.USER_THEME_LIGHT);
                } else {
                    properties.setProperty(AppConfig.USER_THEME_MODE, AppConfig.USER_THEME_DARK);
                }

                updateThumbnails();
                AppConfig.save(AppConfig.SETTINGS_FILE, properties);
            } catch (IOException e) {
                // vi gør ikke ngoet her, da hvis vi en fejl opstår, går det nok, da det ikke er en essentiel del at kunne gemme temaet
            }

            if (model.activeThemeModeProperty().get().equals(ThemeMode.LIGHT)) {
                Application.setUserAgentStylesheet(light.getUserAgentStylesheet());
            } else {
                Application.setUserAgentStylesheet(dark.getUserAgentStylesheet());
            }
        });
    }

    private Region content() {
        NodeUtils.bindVisibility(geographyView, model.activeControllerClassProperty().isEqualTo(GeographyController.class));
        NodeUtils.bindVisibility(profilesView, model.activeControllerClassProperty().isEqualTo(ProfilesController.class));
        NodeUtils.bindVisibility(profileView, model.activeControllerClassProperty().isEqualTo(ProfileController.class));
        NodeUtils.bindVisibility(teamsView, model.activeControllerClassProperty().isEqualTo(TeamsController.class));
        NodeUtils.bindVisibility(teamView, model.activeControllerClassProperty().isEqualTo(TeamController.class));
        NodeUtils.bindVisibility(currencyView, model.activeControllerClassProperty().isEqualTo(CurrencyController.class));

        return new StackPane(geographyView, profilesView, profileView, teamsView, teamView, currencyView);
    }

    private Region topbar() {
        var results = new HBox(LayoutConstants.STANDARD_SPACING);
        results.getStyleClass().addAll(Styles.BG_DEFAULT, "top-bar");
        results.setAlignment(Pos.CENTER_LEFT);
        results.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));
        results.setMinHeight(TOP_BAR_HEIGHT);

        results.getChildren().addAll(new Spacer(), createCurrencyToggleButton(), createThemeToggleButton(), createDarkLightModeToggleButton(), createLanguageToggleButton());
        return results;
    }

    private Tooltip createToolTip(StringProperty text) {
        var tooltip = new Tooltip();
        tooltip.textProperty().bind(text);
        tooltip.setShowDelay(LayoutConstants.TOOLTIP_DURATION);
        return tooltip;
    }

    private Node createCurrencyToggleButton() {
        var icon = new FontIcon();
        icon.iconCodeProperty().bind(CurrencyManager.currencyIconProperty());
        icon.getStyleClass().add("icon");

        var changeBtn = new Button(null, icon);
        changeBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, CssClasses.ACTIONABLE, "top-bar-toggle");
        changeBtn.disableProperty().bind(model.okToChangeCurrencyProperty().not());
        changeBtn.setOnAction(evt -> {
            animateThemeChange();
            changeBtn.disableProperty().unbind();
            changeBtn.setDisable(true);
            onChangeCurrency.accept(() -> changeBtn.disableProperty().bind(model.okToChangeCurrencyProperty().not()));
        });
        changeBtn.setTooltip(createToolTip(LocalizedText.CHANGE_CURRENCY));

        return changeBtn;
    }

    private Node createDarkLightModeToggleButton() {
        var icon = new FontIcon();
        icon.iconCodeProperty().bind(model.themeIconProperty());
        icon.getStyleClass().add("icon");

        var changeBtn = new Button(null, icon);
        changeBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, CssClasses.ACTIONABLE, "top-bar-toggle");
        changeBtn.disableProperty().bind(model.okToChangeThemeProperty().not());
        changeBtn.setOnAction(evt -> {
            animateThemeChange();
            changeBtn.disableProperty().unbind();
            changeBtn.setDisable(true);
            onChangeTheme.accept(() -> changeBtn.disableProperty().bind(model.okToChangeThemeProperty().not()));
        });
        changeBtn.setTooltip(createToolTip(LocalizedText.CHANGE_DARK_LIGHT_MODE));

        return changeBtn;
    }

    private Node createThemeToggleButton() {
        var icon = new FontIcon(Icons.THEME);
        icon.getStyleClass().add("icon");

        var changeBtn = new Button(null, icon);
        changeBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, CssClasses.ACTIONABLE, "top-bar-toggle");
        changeBtn.disableProperty().bind(model.okToChangeThemeProperty().not());
        changeBtn.setOnAction(evt -> {
            var dialog = new Dialog(LocalizedText.THEME_SELECTOR.get(), themeChooserDialog, 575, 350, modalOverlay::hide);
            modalOverlay.show(dialog);
        });
        changeBtn.setTooltip(createToolTip(LocalizedText.CHANGE_THEME));

        return changeBtn;
    }



    private Node createLanguageToggleButton() {
        var icon = new FontIcon(Icons.FLAG);
        icon.getStyleClass().add("icon");

        var changeBtn = new Button(null, icon);
        changeBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, CssClasses.ACTIONABLE, "top-bar-toggle");
        changeBtn.disableProperty().bind(model.okToChangeLanguageProperty().not());
        changeBtn.setOnAction(evt -> {
            animateThemeChange();
            changeBtn.disableProperty().unbind();
            changeBtn.setDisable(true);
            onChangeLanguage.accept(() -> changeBtn.disableProperty().bind(model.okToChangeLanguageProperty().not()));
        });
        changeBtn.setTooltip(createToolTip(LocalizedText.CHANGE_LANGUAGE));

        return changeBtn;
    }

    private Region sidebar() {
        VBox results = new VBox();
        results.getStyleClass().add(CssClasses.BG_SIDE_BAR);
        results.getStyleClass().add("side-bar");
        results.setAlignment(Pos.TOP_CENTER);
        results.setMinWidth(SIDE_BAR_WIDTH);


        results.getChildren().addAll(logo(), navigation(), new Spacer(Orientation.VERTICAL));
        return results;
    }

    private ThemeThumbnail createThemeThumbnail(SamplerTheme theme) {
        var thumbnail = new ThemeThumbnail(theme);
        thumbnail.setSpacing(10);
        thumbnail.setAlignment(Pos.CENTER);
        thumbnail.setToggleGroup(thumbnailsGroup);
        thumbnail.setUserData(theme);
        thumbnail.setSelected(Objects.equals(theme.getName(), thumbnail.getTheme().getName()));

        return thumbnail;
    }

    private void updateThumbnails() {
        boolean darkMode = model.activeThemeModeProperty().get() == ThemeMode.DARK;
        if (darkMode) {
            String name = model.activeThemeProperty().get().getValue().getName();
            primerDark.setSelected(Objects.equals(name, "Primer Dark"));
            nordDark.setSelected(Objects.equals(name, "Nord Dark"));
            cupertinoDark.setSelected(Objects.equals(name, "Cupertino Dark"));
        } else {
            String name = model.activeThemeProperty().get().getKey().getName();
            primerLight.setSelected(Objects.equals(name, "Primer Light"));
            nordLight.setSelected(Objects.equals(name, "Nord Light"));
            cupertinoLight.setSelected(Objects.equals(name, "Cupertino Light"));
        }
    }

    private Node themeChooserDialog() {
        themeThumbnails.setAlignment(Pos.TOP_CENTER);
        themeThumbnails.setPrefColumns(3);
        themeThumbnails.setStyle("-color-thumbnail-border:-color-border-subtle;");

        var root = new VBox(new StackPane(themeThumbnails));
        root.setPadding(new Insets(20));

        if (!grabThemesOnce) {
            themeThumbnails.getChildren().addAll(primerLight, cupertinoLight, nordLight, primerDark, cupertinoDark, nordDark);
            grabThemesOnce = true;
        }

        thumbnailsGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SamplerTheme theme) {
                if (theme.getName().contains("Primer")) {
                    switchToPrimer();
                } else if (theme.getName().contains("Nord")) {
                    switchToNord();
                } else {
                    switchToCupertino();
                }

                model.activeThemeModeProperty().set(theme.isDarkMode() ? ThemeMode.DARK : ThemeMode.LIGHT);
            }
        });

        return root;
    }

    private void switchToPrimer() {
        animateThemeChange();
        model.activeThemeProperty().set(new Pair<>(new PrimerLight(), new PrimerDark()));
    }

    private void switchToNord() {
        animateThemeChange();
        model.activeThemeProperty().set(new Pair<>(new NordLight(), new NordDark()));
    }

    private void switchToCupertino() {
        animateThemeChange();
        model.activeThemeProperty().set(new Pair<>(new CupertinoLight(), new CupertinoDark()));
    }

    private void animateThemeChange() {
        Image snapshot = scene.snapshot(null);
        Pane root = (Pane) scene.getRoot();

        ImageView imageView = new ImageView(snapshot);
        root.getChildren().add(imageView);

        var transition = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(imageView.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(350), new KeyValue(imageView.opacityProperty(), 0, Interpolator.EASE_OUT))
        );
        transition.setOnFinished(e -> root.getChildren().remove(imageView));
        transition.play();
    }

    private Region logo() {
        var results = new HBox();
        results.setMinHeight(TOP_BAR_HEIGHT);
        results.setAlignment(Pos.CENTER);
        results.getStyleClass().add(CssClasses.BG_SIDE_BAR_EMPHASIS);

        ImageView logoView = new ImageView();
        var logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sidebar_logo.png")));
        var logo_dark = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sidebar_logo_dark.png")));

        logoView.setImage(logo);
        model.activeThemeModeProperty().addListener((obs, ov, nv) -> {
            if (nv == ThemeMode.LIGHT) logoView.setImage(logo);
            else logoView.setImage(logo_dark);
        });


        results.getChildren().add(logoView);
        return results;
    }

    private Region navigation() {
        return new Navigation(SIDE_BAR_WIDTH, model.activeControllerClassProperty())
                .withButton(LocalizedText.GEOGRAPHY, Icons.GEOGRAPHY, onShowGeography, GeographyController.class)
                .withButton(LocalizedText.PROFILES, Icons.PROFILES, onShowProfiles, ProfilesController.class, ProfileController.class)
                .withButton(LocalizedText.TEAMS, Icons.TEAMS, onShowTeams, TeamsController.class, TeamController.class)
                .withButton(LocalizedText.CURRENCY, CurrencyManager.currencyIconProperty(), onShowCurrency, CurrencyController.class);
    }
}
