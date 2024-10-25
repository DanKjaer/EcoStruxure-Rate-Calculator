package ecostruxure.rate.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ecostruxure.rate.calculator.controllers")
public class Launcher {
    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }
}

/*import atlantafx.base.theme.*;
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
import java.util.Properties;*/


/*
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
        //var scene = new Scene(new VBox(), 1340, 940); -- Old scene size


        var icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/app_icon.png")));
        stage.getIcons().add(icon);
        stage.setTitle("EcoStruxure - Rate Calculator");
        stage.setScene(scene);

        MainController mainController = new MainController(stage);
        scene.setRoot(mainController.view());
        stage.show();
    }
}*/
