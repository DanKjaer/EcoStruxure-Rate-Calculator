package ecostruxure.rate.calculator.gui.util;

import ecostruxure.rate.calculator.gui.common.TabOrder;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.List;

public class TabHandler {
    private static final PseudoClass TAB_PSEUDO_CLASS = PseudoClass.getPseudoClass("tab");
    private TabOrder tabOrder;
    private ChangeListener<Scene> sceneChangeListener;
    private ChangeListener<Node> focusChangeListener;
    private EventHandler<KeyEvent> tabKeyEventHandler;

    public void tabOrder(TabOrder order) {
        tabOrder = order;
        order.order().forEach(node -> node.setFocusTraversable(true));
    }

    public void initialize(Node root) {
        // Opryd i alle bindinger og listeners
        cleanup(root);

        // Giv fokus til den første node
        if (!tabOrder.order().isEmpty()) requestFocus(tabOrder.order().getFirst());

        tabKeyEventHandler = event -> {
            if (event.getCode() == KeyCode.TAB) handleTabKeyPress(event, root);
        };
        root.addEventFilter(KeyEvent.KEY_PRESSED, tabKeyEventHandler);

        // Vi lytter til når scenen får et nyt 'focus', da hvis vi ikke har det her,
        // så vil vores TAB_PSEUDO_CLASS ikke blive fjernet når vi tabber ind i en Node og vælger en anden med musen, da så får den fokus.
        sceneChangeListener = (obs, oldScene, newScene) -> {
            if (newScene != null) {
                focusChangeListener = this::focusChanged;
                newScene.focusOwnerProperty().addListener(focusChangeListener);
            }
        };

        root.sceneProperty().addListener(sceneChangeListener);
    }

    private void cleanup(Node root) {
        if (tabKeyEventHandler != null) root.removeEventFilter(KeyEvent.KEY_PRESSED, tabKeyEventHandler);
        if (sceneChangeListener != null) root.sceneProperty().removeListener(sceneChangeListener);
        if (root.getScene() != null && focusChangeListener != null) root.getScene().focusOwnerProperty().removeListener(focusChangeListener);
    }

    private void requestFocus(Node node) {
        // Meget underlig måde at request focus på, men platform.runlater() virkede ikke
        // vi laver egentlig lidt ala et while loop, dog med 100 ms delay mellem hvert requestFocus
        var timeline = new Timeline(new KeyFrame(Duration.millis(100), evt -> {
            if (!node.isFocused()) {
                node.requestFocus();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // vi stopper timelinen når noden får fokus
        node.focusedProperty().addListener((obs, ov, nv) -> {
            if (nv) timeline.stop();
        });
    }

    private void handleTabKeyPress(KeyEvent event, Node root) {
        Node focusedNode = root.getScene().getFocusOwner();
        List<Node> order = tabOrder.order();

        if (focusedNode != null && order != null && !order.isEmpty()) {
            int currentIndex = order.indexOf(focusedNode);
            if (currentIndex != -1) {
                Node nextNode = getNextFocusableNode(order, currentIndex, event.isShiftDown());
                if (nextNode == null) return;

                // Vi bruger en TAB_PSEUDO_CLASS for at give noget ligesom fokus, men kun ved tab.
                // Det er mest fordi, der var et problem med at når vi havde en radiobutton, så kunne vi give fokus til den selected radio button
                // men vi vil kun have fokus på den der ikke er valgt - så det her blev lige løsningen.
                focusedNode.pseudoClassStateChanged(TAB_PSEUDO_CLASS, false);
                nextNode.pseudoClassStateChanged(TAB_PSEUDO_CLASS, true);

                nextNode.requestFocus();

                event.consume();
            }
        }
    }

    private Node getNextFocusableNode(List<Node> order, int currentIndex, boolean isShiftDown) {
        int step = isShiftDown ? -1 : 1;
        int size = order.size();

        for (int i = currentIndex + step; i != currentIndex; i += step) {
            if (i < 0) i = size - 1; // shift+tab -> bagud
            else if (i >= size) i = 0; // tab -> fremad

            Node node = order.get(i);
            // .isDisabled() er fordi, i f.eks. Modal vindue så er gem knappen disabled før alt er valideret
            // dette gør dog at den agerer som en mur og vi kan ikke tabbe gennem den, med det her kan vi - så vi kan tabbe cirkulært igennem alle felter.
            if (node.isFocusTraversable() && !node.isDisabled() && !isSelectedRadioButton(node)) return node;
        }

        return null;
    }

    private boolean isSelectedRadioButton(Node node) {
        if (node instanceof RadioButton radioButton) {
            // kun tab ind i en radio button som ikke er valgt i en togglegroup
            ToggleGroup toggleGroup = radioButton.getToggleGroup();
            return toggleGroup != null && toggleGroup.getSelectedToggle() == radioButton;
        }
        return false;
    }

    private void focusChanged(ObservableValue<? extends Node> obs, Node ov, Node nv) {
        if (ov != null) ov.pseudoClassStateChanged(TAB_PSEUDO_CLASS, false);
        if (nv != null && tabOrder.order().contains(nv)) nv.pseudoClassStateChanged(TAB_PSEUDO_CLASS, true);
    }
}
