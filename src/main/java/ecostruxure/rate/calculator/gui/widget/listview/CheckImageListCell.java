package ecostruxure.rate.calculator.gui.widget.listview;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.Objects;

public class CheckImageListCell<T> extends ListCell<T> {
    private final ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallback;
    private final ObjectProperty<Callback<T, Image>> imageCallback;
    private final ObjectProperty<Callback<T, ObservableValue<String>>> textCallback;

    private final CheckBox checkBox;
    private final ImageView imageView;
    private final Label nameLabel;
    private final HBox hBox;

    private ObservableValue<Boolean> booleanProperty;

    public CheckImageListCell(Callback<T, ObservableValue<Boolean>> getSelectedProperty,
                           Callback<T, Image> getImage,
                           Callback<T, ObservableValue<String>> getTextProperty) {
        selectedStateCallback = new SimpleObjectProperty<>(getSelectedProperty);
        imageCallback = new SimpleObjectProperty<>(getImage);
        textCallback = new SimpleObjectProperty<>(getTextProperty);

        checkBox = new CheckBox();
        imageView = new ImageView();
        imageView.setFitWidth(24);
        imageView.setPreserveRatio(true);
        nameLabel = new Label();
        hBox = new HBox(8, checkBox, imageView, nameLabel);
        hBox.setAlignment(Pos.CENTER_LEFT);

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            var selectedStateCallback = this.selectedStateCallback.get();
            var imageCallback = this.imageCallback.get();
            var textCallback = this.textCallback.get();

            Objects.requireNonNull(selectedStateCallback, "The CheckImageListCell selectedStateCallbackProperty can not be null");
            Objects.requireNonNull(imageCallback, "The CheckImageListCell imageCallbackProperty can not be null");
            Objects.requireNonNull(textCallback, "The CheckImageListCell textCallbackProperty can not be null");

            loadImageAsync(item);
            nameLabel.textProperty().bind(textCallback.call(item));
            setGraphic(hBox);

            if (booleanProperty != null) {
                checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
            }
            booleanProperty = selectedStateCallback.call(item);
            if (booleanProperty != null) {
                checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
            }
        }
    }

    private void loadImageAsync(T item) {
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() {
                return imageCallback.get().call(item);
            }

            @Override
            protected void succeeded() {
                imageView.setImage(getValue());
            }
        };
        new Thread(loadImageTask).start();
    }
}
