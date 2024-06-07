package ecostruxure.rate.calculator.gui.system.modal;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import ecostruxure.rate.calculator.gui.common.TabOrder;
import ecostruxure.rate.calculator.gui.common.ModalController;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.util.constants.CssClasses;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.TabHandler;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ModalDialog extends ModalBox {
    private final EventBus eventBus;
    private final TabHandler tabHandler;
    private final Card body = new Card();
    private final Tile header = new Tile();
    private ModalController controller;
    private ModalModel model;

    private Button closeBtn;
    private Button saveBtn;
    private boolean loaded;
    private List<Node> nodes;

    public ModalDialog(EventBus eventBus, ModalController controller) {
        this(eventBus);
        this.controller = controller;
        this.model = controller.modalModel();
        initializeComponents();
        createView();
        initializeTabOrder();
    }

    public ModalDialog(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
        this.tabHandler = new TabHandler();
        this.controller = null;
        this.model = null;
    }

    public void setController(ModalController controller) {
        this.controller = controller;
        this.model = controller.modalModel();
        initializeComponents();

        if (!loaded) {
            createView();
            loaded = true;
        }

        initializeTabOrder();
    }

    private void initializeTabOrder() {
        List<Node> tabOrder = controller.order();
        if (tabOrder == null) return;
        nodes = new ArrayList<>(tabOrder);
        nodes.add(closeBtn);
        nodes.add(saveBtn);

        TabOrder combinedTabOrder = () -> nodes;
        tabHandler.tabOrder(combinedTabOrder);
        tabHandler.initialize(body);
    }


    private void initializeComponents() {
        header.titleProperty().bind(model.titleProperty());
        body.setBody(controller.view());
        body.setFooter(createFooter());
        setMinSize(model.widthProperty().get(), model.heightProperty().get());
        setMaxSize(model.widthProperty().get(), model.heightProperty().get());
        getStyleClass().addAll("modal-dialog", CssClasses.ROUNDED_HALF, CssClasses.PADDING_DEFAULT);
        pseudoClassStateChanged(CssClasses.LIGHT, true);
        // todo: ændre til dark cssclass

        super.closeButton.getStyleClass().add(CssClasses.ACTIONABLE);
        super.closeButton.setOnMouseClicked(evt -> {
            handleModalClose(controller.onClose());
        });
    }

    private void handleModalClose(Runnable event) {
        if (event != null) event.run();
        eventBus.publish(new HideModalEvent());
    }

    private Node createFooter() {
        var results = new HBox(LayoutConstants.STANDARD_SPACING);
        results.getStyleClass().add("results");
        results.setAlignment(Pos.CENTER_RIGHT);
        VBox.setVgrow(results, Priority.NEVER);

        closeBtn = createButton(
                model.closeButtonTextProperty(),
                "form-action", Styles.TEXT_BOLD, Styles.TEXT_MUTED, CssClasses.ACTIONABLE, CssClasses.MODAL_CLOSE_BUTTON
        );
        closeBtn.setOnMouseClicked(evt -> handleModalClose(controller.onClose()));
        closeBtn.setCancelButton(true);

        saveBtn = createButton(
                model.saveButtonTextProperty(),
                "form-action", Styles.TEXT_BOLD, Styles.SUCCESS, CssClasses.ACTIONABLE
        );
        saveBtn.disableProperty().bind(model.okToSaveProperty().not());
        saveBtn.setOnAction(evt -> {
            saveBtn.disableProperty().unbind();
            saveBtn.setDisable(true);
            controller.onSave(() -> saveBtn.disableProperty().bind(model.okToSaveProperty().not()));
        });
        saveBtn.setDefaultButton(true);

        results.getChildren().addAll(closeBtn, new Spacer(), saveBtn);
        return results;
    }

    private Button createButton(StringProperty buttonText, String... styles) {
        Button button = new Button();
        button.textProperty().bind(buttonText);
        button.getStyleClass().addAll(styles);
        return button;
    }

    private void createView() {
        body.setHeader(header);
        body.getStyleClass().add(Tweaks.EDGE_TO_EDGE);

        AnchorPane.setTopAnchor(body, 0d);
        AnchorPane.setRightAnchor(body, 0d);
        AnchorPane.setBottomAnchor(body, 0d);
        AnchorPane.setLeftAnchor(body, 0d);

        addContent(body);
    }
}