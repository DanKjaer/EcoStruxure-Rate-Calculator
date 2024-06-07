package ecostruxure.rate.calculator.gui.component.modals.teamedit;

import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class TeamEditView implements View {
    private final TeamEditModel model;
    private final Consumer<List<Node>> tabOrderConsumer;

    public TeamEditView(TeamEditModel model, Consumer<List<Node>> tabOrderConsumer) {
        this.model = model;
        this.tabOrderConsumer = tabOrderConsumer;
    }

    @Override
    public Region build() {
        var nameField = Fields.boundValidatedTextField(model.newNameProperty(), model.newNameValidProperty(), 100);
        tabOrderConsumer.accept(List.of(nameField));


        return new VBox(Labels.bound(LocalizedText.NAME), nameField);
    }
}
