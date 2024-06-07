package ecostruxure.rate.calculator.gui.component.modals.teameditprofile;

import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class TeamEditProfileView implements View {
    private final TeamEditProfileModel model;
    private final Consumer<List<Node>> tabOrderConsumer;

    public TeamEditProfileView(TeamEditProfileModel model, Consumer<List<Node>> tabOrderConsumer) {
        this.model = model;
        this.tabOrderConsumer = tabOrderConsumer;
    }

    @Override
    public Region build() {
        StringExpression combinedTitle = Bindings.concat(LocalizedText.TEAM_PROFILE_EDIT_TITLE, " ", model.profileNameProperty());

        var nameLabel = Labels.bound(combinedTitle);
        var rateField = Fields.percentageTextField(model.utilizationRateProperty(), model.utilizationRateIsValidProperty());
        rateField.disableProperty().bind(model.utilizationRateFetchedProperty().not());
        var rateBox = new VBox(Labels.bound(LocalizedText.UTILIZATION_RATE), rateField);

        var hoursField = Fields.percentageTextField(model.utilizationHoursProperty(), model.utilizationHoursIsValidProperty());
        hoursField.disableProperty().bind(model.utilizationHoursFetchedProperty().not());
        var hoursBox = new VBox(Labels.bound(LocalizedText.UTILIZATION_HOURS), hoursField);

        tabOrderConsumer.accept(List.of(rateField, hoursField));

        return new VBox(LayoutConstants.STANDARD_SPACING, nameLabel, rateBox, hoursBox);

    }
}
