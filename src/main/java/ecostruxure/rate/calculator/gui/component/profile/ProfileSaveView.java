package ecostruxure.rate.calculator.gui.component.profile;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.be.enums.ResourceType;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.property.ValidationProperty;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import ecostruxure.rate.calculator.gui.widget.TwoColGridPane;
import ecostruxure.rate.calculator.gui.widget.control.AutocompleteComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ProfileSaveView implements View {
    private final ProfileSaveModel model;

    public ProfileSaveView(ProfileSaveModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        var results = new VBox(LayoutConstants.STANDARD_SPACING);

        var nameLbl = Labels.bound(LocalizedText.NAME);
        var nameField = Fields.boundValidatedTextField(model.nameProperty(), model.nameIsValidProperty(), 100);
        nameField.getStyleClass().add("save-model-text-field");
        nameField.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
        var name = new VBox(nameLbl, nameField);
        nameField.disableProperty().bind(model.disableFieldsProperty());

        var locationBox = new AutocompleteComboBox<>(model.locations());
        locationBox.getStyleClass().add("save-model-combo-box");
        locationBox.valueProperty().bindBidirectional(model.selectedGeographyProperty());
        ValidationProperty locationValidation = new ValidationProperty(locationBox, Styles.STATE_SUCCESS, Styles.STATE_DANGER, model.selectedGeographyIsValidProperty().get());
        model.selectedGeographyIsValidProperty().addListener((obs, ov, nv) -> locationValidation.set(nv));
        locationBox.disableProperty().bind(model.disableFieldsProperty());

        var location = new VBox(Labels.bound(LocalizedText.LOCATION), locationBox);

        var toggleGroup = new ToggleGroup();
        var overheadRadio = new RadioButton();
        overheadRadio.disableProperty().bind(model.disableFieldsProperty());
        overheadRadio.getStyleClass().add("save-model-radio-button");
        overheadRadio.textProperty().bind(LocalizedText.OVERHEAD);
        overheadRadio.setUserData(ResourceType.OVERHEAD);
        overheadRadio.setToggleGroup(toggleGroup);


        var productionRadio = new RadioButton();
        productionRadio.disableProperty().bind(model.disableFieldsProperty());
        productionRadio.getStyleClass().add("save-model-radio-button");
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

        var annualSalaryField = Fields.currencyTextField(model.annualSalaryProperty(), "EUR", model.annualSalaryIsValidProperty());
        var totalHoursField = Fields.hourTextField(model.annualTotalHoursProperty(), model.annualTotalHoursIsValidProperty(), 1,8760);

        annualSalaryField.getStyleClass().add("save-model-text-field");
        totalHoursField.getStyleClass().add("save-model-text-field");

        annualSalaryField.disableProperty().bind(model.disableFieldsProperty());
        totalHoursField.disableProperty().bind(model.disableFieldsProperty());

        var annuallyGrid = TwoColGridPane.styled()
                .add(Labels.bound(LocalizedText.SALARY), annualSalaryField)
                .add(Labels.bound(LocalizedText.TOTAL_HOURS), totalHoursField);
        var annuallyContainer = TwoColGridPane.withTitle(LocalizedText.ANNUALLY, annuallyGrid);

        var overheadField = Fields.percentageTextField(model.effectivenessProperty(), model.effectivenessIsValidProperty());
        var hoursPerDayField = Fields.hourTextField(model.hoursPerDayProperty(), model.hoursPerDayIsValidProperty(), 1, 24);

        overheadField.getStyleClass().add("save-model-text-field");
        hoursPerDayField.getStyleClass().add("save-model-text-field");

        overheadField.disableProperty().bind(model.disableFieldsProperty());
        hoursPerDayField.disableProperty().bind(model.disableFieldsProperty());

        var modifiersGrid = TwoColGridPane.styled()
                .add(Labels.bound(LocalizedText.EFFECTIVENESS), overheadField)
                .add(Labels.bound(LocalizedText.HOURS_PER_DAY), hoursPerDayField);
        var modifiersContainer = TwoColGridPane.withTitle(LocalizedText.MODIFIERS, modifiersGrid);

        results.getChildren().addAll(name, location, resourceTypeContainer, annuallyContainer, modifiersContainer);
        return results;
    }
}
