package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;

public class Navigation extends VBox {
    private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
    private final int width;
    private final ObjectProperty<Class<? extends Controller>> activeControllerClassProperty;


    public Navigation(int width, ObjectProperty<Class<? extends Controller>> activeControllerClassProperty) {
        this.width = width;
        this.activeControllerClassProperty = activeControllerClassProperty;
        setPadding(new Insets(LayoutConstants.STANDARD_PADDING * 2, 0, 0, 0));
    }


    @SafeVarargs
    public final Navigation withButton(ObservableStringValue text, Ikon icon, Runnable action, Class<? extends Controller>... controllerClasses) {
        var button = createSidebarButton(text, icon, controllerClasses);
        button.setOnAction(event -> action.run());
        getChildren().add(button);
        return this;
    }

    @SafeVarargs
    public final Navigation withButton(ObservableStringValue text, ObjectProperty<Ikon> iconProperty, Runnable action, Class<? extends Controller>... controllerClasses) {
        var button = createSidebarButton(text, iconProperty, controllerClasses);
        button.setOnAction(event -> action.run());
        getChildren().add(button);
        return this;
    }

    @SafeVarargs
    private Button createSidebarButton(ObservableStringValue text, Ikon icon, Class<? extends Controller>... controllerClasses) {
        var button = new Button();
        button.getStyleClass().addAll(Styles.TEXT_BOLD, CssClasses.ACTIONABLE, "nav-button");

        var hbox = new HBox(LayoutConstants.STANDARD_SPACING);
        hbox.setAlignment(Pos.CENTER_LEFT);

        var lbl = new Label();
        lbl.getStyleClass().add("label");
        lbl.textProperty().bind(text);

        if (icon != null) {
            var fontIcon = new FontIcon(icon);
            fontIcon.getStyleClass().add("icon");
            hbox.getChildren().add(fontIcon);
        }
        hbox.getChildren().add(lbl);

        button.setGraphic(hbox);
        button.setMinWidth(width);

        button.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, false);
        activeControllerClassProperty.addListener((obs, ov, nv) -> {
            boolean isActive = nv != null && Arrays.asList(controllerClasses).contains(nv);
            button.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, isActive);
        });

        return button;
    }

    @SafeVarargs
    private Button createSidebarButton(ObservableStringValue text, ObjectProperty<Ikon> iconProperty, Class<? extends Controller>... controllerClasses) {
        var button = new Button();
        button.getStyleClass().addAll(Styles.TEXT_BOLD, CssClasses.ACTIONABLE, "nav-button");
        button.setEffect(null);

        var hbox = new HBox(LayoutConstants.STANDARD_SPACING);
        hbox.setAlignment(Pos.CENTER_LEFT);

        var lbl = new Label();
        lbl.getStyleClass().add("label");
        lbl.textProperty().bind(text);

        FontIcon fontIcon = new FontIcon();
        fontIcon.getStyleClass().add("icon");
        fontIcon.iconCodeProperty().bind(iconProperty);
        hbox.getChildren().add(fontIcon);
        hbox.getChildren().add(lbl);

        button.setGraphic(hbox);
        button.setMinWidth(width);

        button.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, false);
        activeControllerClassProperty.addListener((obs, ov, nv) -> {
            boolean isActive = nv != null && Arrays.asList(controllerClasses).contains(nv);
            button.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, isActive);
        });

        return button;
    }
}
