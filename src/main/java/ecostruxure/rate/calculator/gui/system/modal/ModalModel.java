package ecostruxure.rate.calculator.gui.system.modal;

import javafx.beans.property.*;
import javafx.beans.value.ObservableStringValue;

public class ModalModel {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty saveButtonText = new SimpleStringProperty();
    private final StringProperty closeButtonText = new SimpleStringProperty();
    private final BooleanProperty okToSave = new SimpleBooleanProperty();
    private final IntegerProperty width = new SimpleIntegerProperty();
    private final IntegerProperty height = new SimpleIntegerProperty();

    public ModalModel(ObservableStringValue title,
                      ObservableStringValue saveButtonText,
                      ObservableStringValue closeButtonText,
                      BooleanProperty okToSave,
                      IntegerProperty width,
                      IntegerProperty height) {
        this.title.bind(title);
        this.saveButtonText.bind(saveButtonText);
        this.closeButtonText.bind(closeButtonText);
        this.okToSave.bind(okToSave);
        this.width.bind(width);
        this.height.bind(height);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty saveButtonTextProperty() {
        return saveButtonText;
    }

    public StringProperty closeButtonTextProperty() {
        return closeButtonText;
    }

    public BooleanProperty okToSaveProperty() {
        return okToSave;
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public IntegerProperty heightProperty() {
        return height;
    }
}
