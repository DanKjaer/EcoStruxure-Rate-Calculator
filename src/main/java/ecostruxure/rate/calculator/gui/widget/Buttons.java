package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class Buttons {
    public static Button actionButton(ObservableStringValue text, EventHandler<ActionEvent> actionEventHandler, String... styles) {
        return actionButton(text, null, actionEventHandler, styles);
    }

    public static Button actionButton(ObservableStringValue text, Ikon ikon, EventHandler<ActionEvent> actionEventHandler, String... styles) {
        Button results = new Button();
        results.textProperty().bind(text);
        results.setGraphic(ikon == null ? null : new FontIcon(ikon));
        results.getStyleClass().add(CssClasses.ACTIONABLE);
        results.getStyleClass().addAll(styles);
        results.setOnAction(actionEventHandler);
        return results;
    }

    public static Button actionIconButton(ObservableStringValue text, Ikon ikon, EventHandler<ActionEvent> actionEventHandler, String... styles) {
        Button results = new Button();
        results.setGraphic(ikon == null ? null : new FontIcon(ikon));
        results.getStyleClass().addAll(CssClasses.ACTIONABLE, Styles.BUTTON_ICON);
        results.getStyleClass().addAll(styles);
        results.setOnAction(actionEventHandler);

        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(text);
        tooltip.setShowDelay(LayoutConstants.TOOLTIP_DURATION);
        results.setTooltip(tooltip);

        return results;
    }
}
