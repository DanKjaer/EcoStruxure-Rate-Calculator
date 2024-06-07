package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.util.filter.FixedPositiveDecimalFilter;
import ecostruxure.rate.calculator.gui.util.property.ValidationProperty;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Fields {
    public static CustomTextField searchField(int prefWidth) {
        var results = new CustomTextField();
        results.getStyleClass().add("search-field");
        results.promptTextProperty().bind(LocalizedText.SEARCH_PROMPT);
        results.setPrefWidth(prefWidth);
        results.setLeft(new FontIcon(Icons.SEARCH));
        return results;
    }

    public static TextField searchFieldGroup(int prefWidth) {
        var results = new TextField();
        results.getStyleClass().add("search-field-input-group");
        results.promptTextProperty().bind(LocalizedText.SEARCH_PROMPT);
        results.setPrefWidth(prefWidth);

        return results;
    }

    public static CustomTextField searchFieldWithDelete(int prefWidth, StringProperty search) {
        var results = searchField(prefWidth);
        results.promptTextProperty().bind(LocalizedText.SEARCH_PROMPT);
        results.setPrefWidth(prefWidth);

        results.setRight(new FontIcon(Icons.X));
        results.getRight().visibleProperty().bind(Bindings.createBooleanBinding(() -> !search.get().isEmpty(), search));

        results.getRight().getStyleClass().add(CssClasses.ACTIONABLE);
        results.getRight().setOnMouseClicked(event -> search.set(""));
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(50), results.getRight());
        scaleUp.setToX(1.3);
        scaleUp.setToY(1.3);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(50), results.getRight());
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        results.getRight().setOnMouseEntered(event -> scaleUp.playFromStart());
        results.getRight().setOnMouseExited(event -> scaleDown.playFromStart());
        return results;
    }

    public static TextField boundValidatedTextField(StringProperty boundProperty, BooleanProperty isValid) {
        var results = new TextField();
        results.textProperty().bindBidirectional(boundProperty);
        var validation = new ValidationProperty(results, Styles.STATE_SUCCESS, Styles.STATE_DANGER, isValid.get());
        isValid.addListener((obs, ov, nv) -> validation.set(nv));
        return results;
    }

    public static TextField boundValidatedTextField(StringProperty boundProperty, BooleanProperty isValid, int maxLength) {
        var results = boundValidatedTextField(boundProperty, isValid);
        results.textProperty().addListener((obs, ov, nv) -> {
            if (nv.length() > maxLength) results.setText(nv.substring(0, maxLength));
        });
        return results;
    }

    public static CustomTextField boundValidatedTextField(StringProperty boundProperty, String ikon, BooleanProperty isValid) {
        var results = new CustomTextField();
        results.textProperty().bindBidirectional(boundProperty);
        var validation = new ValidationProperty(results, Styles.STATE_SUCCESS, Styles.STATE_DANGER, isValid.get());
        isValid.addListener((obs, ov, nv) -> validation.set(nv));
        results.setRight(new Label(ikon));
        return results;
    }

    public static CustomTextField boundValidatedTextField(StringProperty boundProperty, ObservableStringValue ikon, BooleanProperty isValid) {
        var results = new CustomTextField();
        results.textProperty().bindBidirectional(boundProperty);
        var validation = new ValidationProperty(results, Styles.STATE_SUCCESS, Styles.STATE_DANGER, isValid.get());
        isValid.addListener((obs, ov, nv) -> validation.set(nv));

        var icon = Labels.bound(ikon);
        Text textMeasurer = new Text();
        textMeasurer.setFont(icon.getFont());

        icon.textProperty().addListener((obs, ov, nv) -> {
            textMeasurer.setText(nv);
            double textWidth = textMeasurer.getLayoutBounds().getWidth();

            int maxWidth = 40;
            int minWidth = 30;
            double newWidth = Math.min(maxWidth, Math.max(minWidth, textWidth + 10));

            icon.setPrefWidth(newWidth);
            icon.setMinWidth(newWidth);
            icon.setMaxWidth(newWidth);
        });

        icon.setAlignment(Pos.CENTER_RIGHT);
        icon.setTextAlignment(TextAlignment.RIGHT);

        icon.setPrefWidth(30);
        icon.setMinWidth(30);
        icon.setMaxWidth(30);

        results.setRight(icon);
        return results;
    }

    public static Node percentageTextField(StringProperty boundProperty, BooleanProperty isValid) {
        var results = boundValidatedTextField(boundProperty, "%", isValid);
        var filter = new FixedPositiveDecimalFilter(2, 100.00);
        var formatter = new TextFormatter<>(filter);
        results.setTextFormatter(formatter);
        return results;
    }

    public static CustomTextField hourTextField(StringProperty boundProperty, BooleanProperty isValid, double minHours, double maxHours) {
        var results = boundValidatedTextField(boundProperty, LocalizedText.HOUR_SYMBOL_UPPERCASE, isValid);
        var filter = new FixedPositiveDecimalFilter(2, minHours, maxHours);
        var formatter = new TextFormatter<>(filter);
        results.setTextFormatter(formatter);
        return results;
    }

    public static TextField multiplierTextField(StringProperty boundProperty, BooleanProperty isValid) {
        var results = boundValidatedTextField(boundProperty, "×", isValid);
        var filter = new FixedPositiveDecimalFilter(2, 1000.00);
        var formatter = new TextFormatter<>(filter);
        results.setTextFormatter(formatter);
        return results;
    }

    public static CustomTextField currencyTextField(StringProperty boundProperty, String currencyIcon, BooleanProperty isValid) {
        var results = boundValidatedTextField(boundProperty, currencyIcon, isValid);
        var filter = new FixedPositiveDecimalFilter(4, 99999999999999.9999);
        var formatter = new TextFormatter<>(filter);
        results.setTextFormatter(formatter);
        return results;
    }

    public static CustomTextField currencyTextField(StringProperty boundProperty, ObservableStringValue currencyIcon, BooleanProperty isValid) {
        var results = boundValidatedTextField(boundProperty, currencyIcon, isValid);
        var filter = new FixedPositiveDecimalFilter(4, 99999999999999.9999);
        var formatter = new TextFormatter<>(filter);
        results.setTextFormatter(formatter);
        return results;
    }
}
