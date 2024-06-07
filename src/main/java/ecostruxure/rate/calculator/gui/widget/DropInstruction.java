package ecostruxure.rate.calculator.gui.widget;

import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class DropInstruction extends VBox {
    public DropInstruction() {
        setStyle("-fx-background-color: rgba(0,0,0,0.6)");

        var icon = new FontIcon(Icons.IMPORT);
        icon.getStyleClass().add("drag-and-drop-icon");
        var lbl = Labels.bound(LocalizedText.CSV_DROP_INSTRUCTIONS);
        lbl.setStyle("-fx-text-fill: white");
        getChildren().addAll(icon, lbl);
        setAlignment(Pos.CENTER);
        setViewOrder(-100);
    }
}
