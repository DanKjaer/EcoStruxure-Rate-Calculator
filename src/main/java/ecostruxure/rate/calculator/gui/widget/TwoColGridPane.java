package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import javafx.beans.value.ObservableStringValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class TwoColGridPane extends GridPane {
    private int currentRow = 0;

    public TwoColGridPane() {
        var leftCol = new ColumnConstraints();
        leftCol.setHalignment(HPos.LEFT);
        leftCol.setPercentWidth(50);

        var rightCol = new ColumnConstraints();
        rightCol.setHalignment(HPos.LEFT);
        rightCol.setPercentWidth(50);


        getColumnConstraints().addAll(leftCol, rightCol);
        setHgap(LayoutConstants.STANDARD_SPACING);
        setVgap(LayoutConstants.STANDARD_SPACING);
        setPadding(new Insets(LayoutConstants.STANDARD_SPACING));
    }

    public TwoColGridPane add(Node left, Node right) {
        add(left, 0, currentRow);
        add(right, 1, currentRow);

        currentRow++;
        return this;
    }

    public static TwoColGridPane styled() {
        var results = new TwoColGridPane();
        results.getStyleClass().addAll(Styles.BG_SUBTLE, Styles.BORDER_MUTED, CssClasses.ROUNDED_HALF);
        return results;
    }

    public static VBox withTitle(ObservableStringValue title, TwoColGridPane gridPane) {
        return new VBox(LayoutConstants.STANDARD_SPACING, Labels.title4(title), gridPane);
    }
}
