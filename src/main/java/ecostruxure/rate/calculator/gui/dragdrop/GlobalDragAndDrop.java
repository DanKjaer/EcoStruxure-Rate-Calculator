package ecostruxure.rate.calculator.gui.dragdrop;

import ecostruxure.rate.calculator.bll.service.CurrencyService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.*;
import java.util.function.Consumer;


public class GlobalDragAndDrop {
    private final Node dropTarget;
    private final String allowedFileType;
    private final Consumer<File> onProcessFile;

    public GlobalDragAndDrop(Scene scene, Node dropTarget, String allowedFileType, Consumer<File> onProcessFile) {
        this.dropTarget = dropTarget;
        this.allowedFileType = allowedFileType;
        this.onProcessFile = onProcessFile;

        dropTarget.setMouseTransparent(true);
        dropTarget.setVisible(false);

        setupDragAndDrop(scene);
    }

    public Node dropTargetNode() {
        return dropTarget;
    }

    public void setupDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() && db.getFiles().getFirst().getName().toLowerCase().endsWith(allowedFileType)) {
                event.acceptTransferModes(TransferMode.COPY);
                dropTarget.setVisible(true);
                event.consume();
            }
        });

        scene.setOnDragExited(event -> {
            dropTarget.setVisible(false);
        });

        scene.setOnDragEntered(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() && db.getFiles().getFirst().getName().toLowerCase().endsWith(allowedFileType)) {
                dropTarget.setVisible(true);
            }
        });

        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                if (db.getFiles().getFirst().getName().toLowerCase().endsWith(allowedFileType)) {
                    onProcessFile.accept(db.getFiles().getFirst());
                    success = true;
                }
            }
            event.setDropCompleted(success);
            dropTarget.setVisible(false);
        });
    }
}
