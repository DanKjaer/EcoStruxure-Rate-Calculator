package ecostruxure.rate.calculator.gui.component.modals.addteam;

import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.util.NodeUtils;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import ecostruxure.rate.calculator.gui.widget.listview.NestedControlsListCell;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AddTeamView implements View {
    private AddTeamModel model;
    private final Consumer<List<Node>> tabOrderConsumer;
    private final FilteredList<AddProfileItemModel> filteredProfileItemModels;
    private final SortedList<AddProfileItemModel> tableItems;

    public AddTeamView(AddTeamModel model, Consumer<List<Node>> tabOrderConsumer) {
        this.model = model;
        this.tabOrderConsumer = tabOrderConsumer;

        this.filteredProfileItemModels = new FilteredList<>(model.profiles());
        this.tableItems = new SortedList<>(filteredProfileItemModels);

        setupSearchFilter();
    }

    private void setupSearchFilter() {
        model.searchProperty().addListener((obs, ov, nv) -> {
            Predicate<AddProfileItemModel> searchPredicate = (AddProfileItemModel addProfileItemModel) -> {
                if (nv == null || nv.isEmpty())
                    return true;

                var lowercase = nv.toLowerCase();
                return addProfileItemModel.nameProperty().get().toLowerCase().contains(lowercase);
            };

            filteredProfileItemModels.setPredicate(searchPredicate);
        });
    }

    @Override
    public Region build() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        NodeUtils.bindVisibility(progressIndicator, model.profilesFetchedProperty().not());

        var nameField = Fields.boundValidatedTextField(model.nameProperty(), model.nameIsValidProperty(), 100);
        var nameBox = new VBox(Labels.bound(LocalizedText.NAME), nameField);

        var searchField = Fields.searchFieldWithDelete(100, model.searchProperty());
        searchField.textProperty().bindBidirectional(model.searchProperty());

        var profilesBox = new VBox(Labels.bound(LocalizedText.PROFILES), searchField);
        var box = new VBox(LayoutConstants.STANDARD_SPACING, profilesBox, nestedControlsList());

        tabOrderConsumer.accept(List.of(nameField, searchField));

        var layout = new VBox(LayoutConstants.STANDARD_SPACING, nameBox, box);

        return new StackPane(progressIndicator, layout);
    }

    private ListView<AddProfileItemModel> nestedControlsList() {
        ListView<AddProfileItemModel> lv = new ListView<>();

        lv.setCellFactory(param -> new NestedControlsListCell(lv));
        lv.setItems(tableItems);
        return lv;
    }
}
