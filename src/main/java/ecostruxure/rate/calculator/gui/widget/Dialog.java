package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Tweaks;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Dialog extends ModalBox {
    private final Card body = new Card();
    private final Tile header = new Tile();

    public Dialog(String title, Node node, int width, int height, Runnable closeAction) {
        super();

        header.setTitle(title);
        body.setBody(node);
        setMinSize(width, height);
        setMaxSize(width, height);

        getStyleClass().addAll("modal-dialog", CssClasses.ROUNDED_HALF, CssClasses.PADDING_DEFAULT);

        super.closeButton.getStyleClass().add(CssClasses.ACTIONABLE);
        super.closeButton.setOnMouseClicked(evt -> closeAction.run());
        createView();
    }


    private void createView() {
        body.setHeader(header);
        body.getStyleClass().add(Tweaks.EDGE_TO_EDGE);

        AnchorPane.setTopAnchor(body, 0d);
        AnchorPane.setRightAnchor(body, 0d);
        AnchorPane.setBottomAnchor(body, 0d);
        AnchorPane.setLeftAnchor(body, 0d);

        addContent(body);
        getStyleClass().add("modal-dialog");
    }
}