package ecostruxure.rate.calculator.gui.widget;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.PaginationSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class CustomPaginationSkin extends PaginationSkin {

    private HBox controlBox;
    private Button prev;
    private Button next;
    private Button first;
    private Button last;

    private void patchNavigation() {
        Pagination pagination = getSkinnable();
        Node control = pagination.lookup(".control-box");
        if (!(control instanceof HBox))
            return;

        controlBox = (HBox) control;
        prev = (Button) controlBox.getChildren().getFirst();
        next = (Button) controlBox.getChildren().getLast();

        prev.setGraphic(new FontIcon(Icons.PREVIOUS));
        next.setGraphic(new FontIcon(Icons.NEXT));

        first = new Button(null, new FontIcon(Icons.FIRST));
        first.setMaxHeight(20);
        first.setMaxWidth(20);
        first.getStyleClass().addAll(Styles.BUTTON_ICON);
        first.setOnAction(e -> {
            pagination.setCurrentPageIndex(0);
        });
        first.disableProperty().bind(
                pagination.currentPageIndexProperty().isEqualTo(0));

        first.getStyleClass().add("left-arrow-button");

        last = new Button(null, new FontIcon(Icons.LAST));
        last.setMaxHeight(20);
        last.setMaxWidth(20);
        last.getStyleClass().addAll(Styles.BUTTON_ICON);

        controlBox.setMinWidth(300);

        last.setOnAction(e -> {
            pagination.setCurrentPageIndex(pagination.getPageCount());
        });
        BooleanBinding disableLastButtonBinding = Bindings.createBooleanBinding(() ->
                        pagination.getCurrentPageIndex() >= pagination.getPageCount() - 1 || pagination.getPageCount() <= 1,
                pagination.currentPageIndexProperty(),
                pagination.pageCountProperty()
        );

        last.disableProperty().bind(disableLastButtonBinding);

        last.getStyleClass().add("right-arrow-button");


        ListChangeListener childrenListener = c -> {
            while (c.next()) {
                // implementation detail: when nextButton is added, the setup is complete
                if (c.wasAdded() && !c.wasRemoved() // real addition
                        && c.getAddedSize() == 1 // single addition
                        && c.getAddedSubList().getFirst() == next) {
                    addCustomNodes();
                }
            }
        };
        controlBox.getChildren().addListener(childrenListener);
        addCustomNodes();

    }

    protected void addCustomNodes() {
        // guarding against duplicate child exception
        // (some weird internals that I don't fully understand...)
        if (first.getParent() == controlBox) return;
        controlBox.getChildren().addFirst(first);
        controlBox.getChildren().add(last);
    }

    /**
     * @param pagination
     */
    public CustomPaginationSkin(Pagination pagination) {
        super(pagination);
        patchNavigation();
    }
}
