package ecostruxure.rate.calculator.gui.component.modals.verifyprofiles;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.component.modals.addteam.AddProfileItemModel;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.util.NodeUtils;
import ecostruxure.rate.calculator.gui.widget.Fields;
import ecostruxure.rate.calculator.gui.widget.Labels;
import ecostruxure.rate.calculator.gui.widget.listview.NestedControlsListCell;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VerifyProfilesView implements View {
    private final VerifyProfilesModel model;
    private final Consumer<List<Node>> tabOrderConsumer;
    private final FilteredList<AddProfileItemModel> filteredProfileItemModels;
    private final SortedList<AddProfileItemModel> tableItems;


    public VerifyProfilesView(VerifyProfilesModel model, Consumer<List<Node>> tabOrderConsumer) {
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

        var profilesLabel = new Label("", new FontIcon(Icons.ERROR));
        profilesLabel.textProperty().bind(LocalizedText.VERIFY_PROFILE_UNARCHIVE);
        profilesLabel.getStyleClass().addAll(Styles.TEXT, Styles.DANGER);

        var searchField = Fields.searchFieldWithDelete(100, model.searchProperty());
        searchField.textProperty().bindBidirectional(model.searchProperty());

        var profilesBox = new VBox(Labels.bound(LocalizedText.PROFILES), searchField);
        var box = new VBox(LayoutConstants.STANDARD_SPACING, profilesBox, nestedControlsList());

        tabOrderConsumer.accept(List.of(searchField));
        var layout = new VBox(LayoutConstants.STANDARD_SPACING, profilesLabel, box);

        return new StackPane(progressIndicator, layout);
    }

    private ListView<AddProfileItemModel> nestedControlsList() {
        ListView<AddProfileItemModel> lv = new ListView<>();

        lv.setCellFactory(param -> new NestedControlsListCell(lv));
        lv.setItems(tableItems);
        return lv;
    }
}
