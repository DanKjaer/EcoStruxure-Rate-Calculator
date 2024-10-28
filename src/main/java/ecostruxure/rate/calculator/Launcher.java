package ecostruxure.rate.calculator;

import atlantafx.base.theme.*;
import ecostruxure.rate.calculator.gui.component.main.MainController;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.util.AppConfig;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class Launcher extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        try {
            var settingsFile = AppConfig.load(AppConfig.SETTINGS_FILE);
            var userLanguage = settingsFile.getProperty(AppConfig.USER_LANGUAGE);
            if (userLanguage != null && userLanguage.contains(AppConfig.USER_LANGUAGE_DA)) {
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