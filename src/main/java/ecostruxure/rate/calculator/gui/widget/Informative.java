package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import javafx.beans.value.ObservableStringValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class Informative {
    private static final int PADDING = LayoutConstants.CONTENT_PADDING;

    public static HBox pageHeader(ObservableStringValue headerText, ObservableStringValue descriptionText, Ikon ikon) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(PADDING - 5, 0, 0, PADDING));
        hBox.getStyleClass().add("page-header");
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);

        FontIcon fontIcon = new FontIcon(ikon);
        fontIcon.getStyleClass().add("icon");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinWidth(55);
        vBox.setMaxHeight(55);
        vBox.getStyleClass().addAll(CssClasses.ROUNDED_DEFAULT, Styles.BG_DEFAULT, CssClasses.STATS_SHADOW);
        vBox.getChildren().add(fontIcon);

        VBox textDetails = new VBox(0);
        textDetails.setAlignment(Pos.TOP_LEFT);

        Text header = new Text();
        header.getStyleClass().addAll("header-txt", Styles.TEXT_MUTED);
        header.setTranslateY(0); // Dum løsning, men font size rykker åbenbart på den selvom du sætter position til TOP_LEFT f.eks.

        header.textProperty().bind(headerText);

        Text description = new Text();
        description.getStyleClass().addAll("description-txt", Styles.TEXT, Styles.TEXT_SUBTLE);
        description.setTranslateY(-5); // Nævnt i overstående kommentar
        description.textProperty().bind(descriptionText);

        textDetails.getChildren().addAll(header, description);

        hBox.getChildren().addAll(vBox, textDetails, new Spacer());
        return hBox;
    }

    public static VBox headerStats(ObservableStringValue name, ObservableStringValue value, String color, boolean isLast) {
        VBox vBox = new VBox(2);
        vBox.getStyleClass().add("page-header");
        vBox.setAlignment(Pos.TOP_RIGHT);
        vBox.setPadding(new Insets(PADDING - 5, isLast ? PADDING : PADDING - 10, 0, PADDING - 10));

        HBox hBox = new HBox(15);

        Label header = new Label();
        header.textProperty().bind(name);
        header.getStyleClass().addAll("stats-header-txt", Styles.TEXT_SUBTLE);

        FontIcon fontIcon = new FontIcon(Icons.CHART);
        fontIcon.getStyleClass().addAll("stats-icon", color); // color defineret i style.css

        hBox.getChildren().addAll(header, fontIcon);

        Label val = new Label();
        val.textProperty().bind(value);
        val.getStyleClass().add("stats-value-txt");

        vBox.getChildren().addAll(hBox, val);

        return vBox;
    }

    public static VBox headerSeparator() {
        //skal måske omskrives senere hen..
        VBox container = new VBox();
        container.setTranslateY(0);
        container.setAlignment(Pos.CENTER);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPrefHeight(40);

        container.getChildren().add(separator);

        return container;
    }

    public static HBox statsContainer() {
        HBox hBox = new HBox(PADDING);
        hBox.setPadding(new Insets(LayoutConstants.CONTENT_PADDING - 10, LayoutConstants.CONTENT_PADDING, 0, LayoutConstants.CONTENT_PADDING));

        return hBox;
    }

    public static HBox statsBox(ObservableStringValue name, ObservableStringValue value, String color, Ikon ikon) {
        HBox box = new HBox();
        box.setPadding(new Insets(10, 10, 0, 10));
        box.getStyleClass().addAll(CssClasses.STATS_SHADOW, "stats", "stats-" + color, CssClasses.ROUNDED_HALF);

        box.setMinWidth(100);
        box.setPrefWidth(Integer.MAX_VALUE);
        box.setMinHeight(105);
        box.setMaxHeight(105);
        box.setPrefHeight(105);

        VBox info = new VBox(-5);

        Label valueLabel = new Label();
        valueLabel.textProperty().bind(value);
        valueLabel.getStyleClass().addAll("text", "value");

        Label typeLabel = new Label();
        typeLabel.getStyleClass().addAll("text", "type");
        typeLabel.textProperty().bind(name);

        info.getChildren().addAll(valueLabel, typeLabel);

        VBox logo = new VBox();
        logo.setAlignment(Pos.CENTER_RIGHT);

        FontIcon icon = new FontIcon(ikon);
        icon.getStyleClass().addAll("icon");

        logo.getChildren().add(icon);

        // aldrig prøvet noget mere bøvlet
        box.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double mainBox = box.getWidth();

            double iconPref = icon.prefWidth(-1);

            // få infoBox
            double infoBox = info.getWidth();

            // find min størrelse ikon skal være i - padding
            double minIconBox = mainBox - infoBox - PADDING;

            // Tjek om minimum ikon størrelse er større end den preffered størrelse, hvis den er fjern icon.
            boolean reAddIcon = minIconBox >= iconPref;

            // Den er 0.0 på initalization, derfor 0 så den ikke fjerner med det samme.
            if (mainBox == 0.0)
                return;

            // maxTextWidth > infoBoxWidth &&
            if (!reAddIcon)
                box.getChildren().remove(logo);
             else
                if (!box.getChildren().contains(logo) && reAddIcon)
                    box.getChildren().add(logo);
        });

        HBox.setHgrow(logo, Priority.ALWAYS);
        box.getChildren().addAll(info, logo);

        return box;
    }
}
