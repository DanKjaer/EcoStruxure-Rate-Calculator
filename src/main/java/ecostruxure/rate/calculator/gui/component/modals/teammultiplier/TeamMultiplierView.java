package ecostruxure.rate.calculator.gui.component.modals.teammultiplier;

import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class TeamMultiplierView implements View {
    private final TeamMultiplierModel model;
    private final Consumer<List<Node>> tabOrderConsumer;

    public TeamMultiplierView(TeamMultiplierModel model, Consumer<List<Node>> tabOrderConsumer) {
        this.model = model;
        this.tabOrderConsumer = tabOrderConsumer;
    }

    @Override
    public Region build() {
        var markupField = Fields.percentageTextField(model.markupProperty(), model.markupIsValidProperty());
        markupField.disableProperty().bind(model.markupFetchedProperty().not());
        var markupBox = new VBox(Labels.bound(LocalizedText.MARKUP), markupField);

        var grossMarginField = Fields.percentageTextField(model.grossMarginProperty(), model.grossMarginIsValidProperty());
        grossMarginField.disableProperty().bind(model.grossMarginFetchedProperty().not());
        var grossMarginBox = new VBox(Labels.bound(LocalizedText.GROSS_MARGIN), grossMarginField);

        tabOrderConsumer.accept(List.of(markupField, grossMarginField));

        return new VBox(LayoutConstants.STANDARD_SPACING, markupBox, grossMarginBox);
    }
}