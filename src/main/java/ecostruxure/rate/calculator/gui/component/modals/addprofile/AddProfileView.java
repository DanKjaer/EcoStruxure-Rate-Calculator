package ecostruxure.rate.calculator.gui.component.modals.addprofile;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.be.enums.ResourceType;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.BindingsUtils;
import ecostruxure.rate.calculator.gui.util.CurrencyStringConverter;
import ecostruxure.rate.calculator.gui.util.property.ValidationProperty;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import ecostruxure.rate.calculator.gui.widget.TwoColGridPane;
import ecostruxure.rate.calculator.gui.widget.control.AutocompleteComboBox;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class AddProfileView implements View {
    private final AddProfileModel model;
    private final Consumer<List<Node>> tabOrderConsumer;

    public AddProfileView(AddProfileModel model, Consumer<List<Node>> tabOrderConsumer) {
        this.model = model;
        this.tabOrderConsumer = tabOrderConsumer;
    }

    @Override
    public Region build() {
        var results = new VBox(LayoutConstants.STANDARD_SPACING);

        var nameField = Fields.boundValidatedTextField(model.nameProperty(), model.nameIsValidProperty(), 100);
        var nameBox = new VBox(Labels.bound(LocalizedText.NAME), nameField);

        var locationBox = new AutocompleteComboBox<>(model.locations());
        locationBox.valueProperty().bindBidirectional(model.selectedGeographyProperty());
        ValidationProperty locationValidation = new ValidationProperty(locationBox, Styles.STATE_SUCCESS, Styles.STATE_DANGER, model.selectedGeographyIsValidProperty().get());
        model.selectedGeographyIsValidProperty().addListener((obs, ov, nv) -> locationValidation.set(nv));

        var location = new VBox(Labels.bound(LocalizedText.LOCATION), locationBox);

        var currencyBox = new AutocompleteComboBox<>(model.currencies());
        currencyBox.valueProperty().bindBidirectional(model.selectedCurrencyProperty());
        ValidationProperty currencyValidation = new ValidationProperty(currencyBox, Styles.STATE_SUCCESS, Styles.STATE_DANGER, model.selectedCurrencyIsValidProperty().get());
        model.selectedCurrencyIsValidProperty().addListener((obs, ov, nv) -> currencyValidation.set(nv));
        currencyBox.setConverter(new CurrencyStringConverter());

        var currency = new VBox(Labels.bound(LocalizedText.CURRENCY), currencyBox);

        var locCurGrid = new GridPane();
        locCurGrid.add(location, 0, 0);
        locCurGrid.add(currency, 1, 0);
        ColumnConstraints halfWidth = new ColumnConstraints();
        halfWidth.setPercentWidth(50);
        locCurGrid.getColumnConstraints().addAll(halfWidth, halfWidth);
        locCurGrid.setHgap(LayoutConstants.STANDARD_SPACING);

        var toggleGroup = new ToggleGroup();
        var overheadRadio = new RadioButton();
        overheadRadio.textProperty().bind(LocalizedText.OVERHEAD);
        overheadRadio.setUserData(ResourceType.OVERHEAD);
        overheadRadio.setToggleGroup(toggleGroup);

        var productionRadio = new RadioButton();
        productionRadio.textProperty().bind(LocalizedText.PRODUCTION);
        productionRadio.setUserData(ResourceType.PRODUCTION);
        productionRadio.setToggleGroup(toggleGroup);

        toggleGroup.selectToggle(model.selectedResourceTypeProperty().get() == ResourceType.OVERHEAD ? overheadRadio : productionRadio);

        model.selectedResourceTypeProperty().addListener((obs, ov, nv) -> {
            if (nv == ResourceType.OVERHEAD) {
                toggleGroup.selectToggle(overheadRadio);
            } else if (nv == ResourceType.PRODUCTION) {
                toggleGroup.selectToggle(productionRadio);
            }
        });

        toggleGroup.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (nv != null) model.selectedResourceTypeProperty().set((ResourceType) nv.getUserData());
        });

        var resourceTypeGrid = TwoColGridPane.styled().add(overheadRadio, productionRadio);
        var resourceTypeContainer = TwoColGridPane.withTitle(LocalizedText.RESOURCE_TYPE, resourceTypeGrid);

        var selectedCurrencyBinding = BindingsUtils.createStringBindingWithDefault(model.selectedCurrencyProperty(), "");
        var salaryField = Fields.currencyTextField(model.annualSalaryProperty(), selectedCurrencyBinding, model.annualSalaryIsValidProperty());
        var effectiveWorkingHoursField = Fields.hourTextField(model.annualEffectiveWorkingHoursProperty(), model.annualEffectiveWorkingHoursIsValidProperty(), 0, 8760);

        toggleGroup.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                if (nv.getUserData() == ResourceType.OVERHEAD) {
                    effectiveWorkingHoursField.setDisable(true);
                } else if (nv.getUserData() == ResourceType.PRODUCTION) {
                    effectiveWorkingHoursField.setDisable(false);
                }
            }
        });

        effectiveWorkingHoursField.setDisable(model.selectedResourceTypeProperty().get() == ResourceType.OVERHEAD);
        var annuallyGrid = TwoColGridPane.styled()
                .add(Labels.bound(LocalizedText.SALARY), salaryField)
                .add(Labels.bound(LocalizedText.TOTAL_HOURS), effectiveWorkingHoursField);
        var annuallyContainer = TwoColGridPane.withTitle(LocalizedText.ANNUALLY, annuallyGrid);

        var effectivenessField = Fields.percentageTextField(model.effectivenessProperty(), model.effectivenessIsValidProperty());
        var hoursPerDayField = Fields.hourTextField(model.hoursPerDayProperty(), model.hoursPerDayIsValidProperty(), 1, 24);

        var modifiersGrid = TwoColGridPane.styled()
                .add(Labels.bound(LocalizedText.EFFECTIVENESS), effectivenessField)
                .add(Labels.bound(LocalizedText.HOURS_PER_DAY), hoursPerDayField);
        var modifiersContainer = TwoColGridPane.withTitle(LocalizedText.MODIFIERS, modifiersGrid);

        results.getChildren().addAll(nameBox, locCurGrid, resourceTypeContainer, annuallyContainer, modifiersContainer);

        List<Node> nodes = List.of(
                nameField,
                locationBox,
                currencyBox,
                overheadRadio,
                productionRadio,
                salaryField,
                effectiveWorkingHoursField,
                effectivenessField,
                hoursPerDayField
        );
        tabOrderConsumer.accept(nodes);

        return results;
    }
}