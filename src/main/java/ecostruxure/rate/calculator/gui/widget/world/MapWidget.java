package ecostruxure.rate.calculator.gui.widget.world;

import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.NodeUtils;
import eu.hansolo.fx.countries.CountryPane;
import eu.hansolo.fx.countries.RegionPane;
import eu.hansolo.fx.countries.tools.BusinessRegion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MapWidget extends StackPane {
    private final WorldWidget world;
    private final ZoomableScrollPane scrollPane;

    public MapWidget() {
        world = new WorldWidget();

        scrollPane = new ZoomableScrollPane(world);

        this.backgroundProperty().bind(world.backgroundProperty());

        this.getChildren().addAll(scrollPane);

        bindScrollPaneSize();
    }

    private void bindScrollPaneSize() {
        Scene scene = this.getScene();
        if (scene != null) {
            scrollPane.prefWidthProperty().bind(scene.widthProperty().multiply(0.6));
            scrollPane.prefHeightProperty().bind(scene.heightProperty().multiply(0.6));
        } else {
            this.sceneProperty().addListener((obs, ov, nv) -> {
                if (nv != null) {
                    scrollPane.prefWidthProperty().bind(nv.widthProperty().multiply(0.6));
                    scrollPane.prefHeightProperty().bind(nv.heightProperty().multiply(0.6));
                }
            });
        }
    }

    public WorldWidget worldWidget() {
        return world;
    }
}