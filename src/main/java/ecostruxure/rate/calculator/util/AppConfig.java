package ecostruxure.rate.calculator.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    public static final String CONFIG_FILE = "config/config.properties";
    public static final String SETTINGS_FILE = "config/settings.properties";

    public static final String DB_SERVER = "db.server";
    public static final String DB_DATABASE = "db.database";
    public static final String DB_PORT = "db.port";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_USE_INTEGRATED_SECURITY = "db.use_integrated_security";
    public static final String DB_SCHEMA = "db.schema";

    public static final String USER_LANGUAGE = "user.language";
    public static final String USER_LANGUAGE_EN = "en";
    public static final String USER_LANGUAGE_DA = "da";

    public static final String USER_THEME_MODE = "user.theme_mode";
    public static final String USER_THEME_LIGHT = "light";
    public static final String USER_THEME_DARK = "dark";

    public static final String USER_THEME = "user.theme";
    public static final String USER_THEME_PRIMER = "primer";
    public static final String USER_THEME_NORD = "nord";
    public static final String USER_THEME_CUPERTINO = "cupertino";

    public static final String MESSAGES_BUNDLE = "messages";

    public static Properties load(String filePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        }
        return properties;
    }

    public static void save(String filePath, Properties properties) throws IOException {
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        }
    }
}