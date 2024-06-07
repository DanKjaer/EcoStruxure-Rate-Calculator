package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.theme.Styles;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.Label;

public class Labels {
    public static Label styled(ObservableStringValue text, String... styles) {
        var results = new Label();
        results.getStyleClass().addAll(styles);
        results.textProperty().bind(text);
        return results;
    }

    public static Label bound(ObservableStringValue text) {
        var results = new Label();
        results.textProperty().bind(text);
        return results;
    }

    public static Label title1(ObservableStringValue text) {
        return styled(text, Styles.TITLE_1);
    }

    public static Label title2(ObservableStringValue text) {
        return styled(text, Styles.TITLE_2);
    }

    public static Label title3(ObservableStringValue text) {
        return styled(text, Styles.TITLE_3);
    }

    public static Label title4(ObservableStringValue text) {
        return styled(text, Styles.TITLE_4);
    }
}
