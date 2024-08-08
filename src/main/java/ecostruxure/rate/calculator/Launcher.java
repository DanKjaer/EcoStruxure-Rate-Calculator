package ecostruxure.rate.calculator;

import atlantafx.base.theme.*;
import ecostruxure.rate.calculator.gui.component.main.MainController;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.world.WorldWidget;
import ecostruxure.rate.calculator.util.AppConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

public class Launcher extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        try {
            var settingsFile = AppConfig.load(AppConfig.SETTINGS_FILE);
            var userLanguage = settingsFile.getProperty(AppConfig.USER_LANGUAGE);
            if (userLanguage.contains(AppConfig.USER_LANGUAGE_DA)) {
                LocalizedText.CURRENT_LOCALE.set(Locale.of(userLanguage));
            } else {
                LocalizedText.CURRENT_LOCALE.set(Locale.ENGLISH);
            }
        } catch (IOException e) {
            LocalizedText.CURRENT_LOCALE.set(Locale.ENGLISH);
        }
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        var scene = new Scene(new VBox(), stage.getMaxWidth(), stage.getHeight());


        var icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/app_icon.png")));
        stage.getIcons().add(icon);
        stage.setTitle("EcoStruxure - Rate Calculator");
        stage.setScene(scene);

        MainController mainController = new MainController(stage);
        scene.setRoot(mainController.view());
        stage.show();
    }
}

//public class Launcher extends Application {
//    private Stage stage;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) {
//        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
//        this.stage = stage;
//        initUI();
//    }
//
//    private void initUI() {
//        try {
//            var settingsFile = AppConfig.load(AppConfig.SETTINGS_FILE);
//            var userLanguage = settingsFile.getProperty(AppConfig.USER_LANGUAGE);
//            if (userLanguage.contains(AppConfig.USER_LANGUAGE_DA)) {
//                LocalizedText.CURRENT_LOCALE.set(Locale.of(userLanguage));
//            } else {
//                LocalizedText.CURRENT_LOCALE.set(Locale.ENGLISH);
//            }
//
////            var userTheme = settingsFile.getProperty(AppConfig.USER_THEME);
////            switch (userTheme) {
////                case AppConfig.USER_THEME_PRIMER -> Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
////                case AppConfig.USER_THEME_NORD -> Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
////                case AppConfig.USER_THEME_CUPERTINO -> Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
////                default -> Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
////            }
//        } catch (IOException e) {
//            LocalizedText.CURRENT_LOCALE.set(Locale.ENGLISH);
//        }
//
//        Scene splashScene = new Scene(createSplashScreen(), 1340, 940);
//
//        stage.setTitle("Loading...");
//        var icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/app_icon.png")));
//        stage.getIcons().add(icon);
//        stage.setScene(splashScene);
//        stage.show();
//
//        new Thread(() -> {
//            MainController mainController = setupMainController();
//            Platform.runLater(() -> {
//                Scene mainScene = new Scene(mainController.view(), 1340, 940);
//                stage.setScene(mainScene);
//                stage.setTitle("EcoStruxure - Rate Calculator");
//
//                stage.show();
//            });
//        }).start();
//    }
//
//    private Region createSplashScreen() {
//        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sidebar_logo.png")));
//        ImageView logoView = new ImageView(logo);
//
//        Label loadingLabel = new Label("Loading... Please wait.");
//        loadingLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
//
//        ProgressIndicator progressIndicator = new ProgressIndicator();
//        progressIndicator.setStyle("-fx-pref-width: 50px; -fx-pref-height: 50px;");
//
//        VBox splashLayout = new VBox(20);
//        splashLayout.setAlignment(Pos.CENTER);
//        splashLayout.getChildren().addAll(logoView, loadingLabel, progressIndicator);
//        splashLayout.setStyle("-fx-padding: 30; -fx-background-color: #6BAE61; -fx-alignment: center;");
//        return splashLayout;
//    }
//
//    private MainController setupMainController() {
//        return new MainController(stage);
//    }
//}