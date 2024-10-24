package ecostruxure.rate.calculator.gui.widget.listview;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.component.modals.addteam.AddProfileItemModel;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.widget.BigDecimalSpinner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;

public class NestedControlsListCell extends ListCell<AddProfileItemModel> {

    private final HBox root;
    private final Label titleLabel;
    private final Label locationLabel;
    private final Label costAllocationLabel;
    private final Label hourAllocationLabel;
    private CheckBox checkBox;

    private BigDecimalSpinner rateSpinner;
    private BigDecimalSpinner hoursSpinner;

    private boolean onLoad;
    private final ListView<AddProfileItemModel> listView;

    public NestedControlsListCell(ListView<AddProfileItemModel> listView) {
        this.listView = listView;
        titleLabel = new Label();
        locationLabel = new Label();
        locationLabel.getStyleClass().add(Styles.TEXT_MUTED);

        var Vbox = new VBox(0, titleLabel, locationLabel);
        Vbox.setMaxWidth(220);

        hourAllocationLabel = new Label("", new FontIcon(Icons.CLOCK));
        hourAllocationLabel.getStyleClass().add(Styles.TEXT_SMALL);
        var hourAllocationBox = new HBox(LayoutConstants.STANDARD_SPACING, getHoursSpinner());
        hourAllocationBox.setAlignment(Pos.CENTER_RIGHT);

        costAllocationLabel = new Label("", new FontIcon(Icons.MONEY));

        costAllocationLabel.getStyleClass().add(Styles.TEXT_SMALL);
        var costAllocationBox = new HBox(LayoutConstants.STANDARD_SPACING, getRateSpinner());
        costAllocationBox.setAlignment(Pos.CENTER_RIGHT);

        var ratesBox = new VBox(2, hourAllocationLabel, costAllocationLabel);
        ratesBox.setPrefWidth(100);
        ratesBox.setPadding(new Insets(5, 5, 5, 5));

        root = new HBox(5,
                getCheckBox(),
                Vbox,
                new Spacer(),
                ratesBox,
                new HBox(5, hourAllocationBox, costAllocationBox)
        );

        root.setAlignment(Pos.CENTER_LEFT);
        super.setPadding(new Insets(5, 5, 5, 5));

        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                checkBox.setSelected(!checkBox.isSelected());
            }
        });

    }

    private BigDecimalSpinner getRateSpinner() {
        if (rateSpinner == null) {
            rateSpinner = new BigDecimalSpinner(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal(0.5));

            rateSpinner.getTextField().setLeft(new FontIcon(Icons.MONEY));

            rateSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (getItem() != null) {
                        getItem().setCostAllocationProperty().set(newVal);

                    if (!onLoad) {
                        checkBox.setSelected(newVal.compareTo(BigDecimal.ZERO) != 0);
                    }

                    updateRateLabel(getItem());
                    onLoad = true;
                }
            });
        }
        return rateSpinner;
    }

    private BigDecimalSpinner getHoursSpinner() {
        if (hoursSpinner == null) {
            hoursSpinner = new BigDecimalSpinner(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal(0.5));
            hoursSpinner.getTextField().setLeft(new FontIcon(Icons.CLOCK));
            hoursSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (getItem() != null) {
                    getItem().setHourAllocationProperty().set(newVal);

                    if (!onLoad) {
                        getItem().setHourAllocationProperty().set(newVal);
                        checkBox.setSelected(newVal.compareTo(BigDecimal.ZERO) != 0);
                    }

                    updateHourLabel(getItem());
                    onLoad = true;
                }
            });
        }
        return hoursSpinner;
    }

    private CheckBox getCheckBox() {
        if (checkBox == null) {
            checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (getItem() != null) {
                    getItem().selectedProperty().set(newVal);
                }
            });
        }
        return checkBox;
    }

    private void updateRateLabel(AddProfileItemModel profileItemModel) {
        BigDecimal currentRate = profileItemModel.currentCostAllocationProperty().get();
        BigDecimal setRate = profileItemModel.setCostAllocationProperty().get();

        BigDecimal substracted = currentRate.subtract(setRate);
        if (substracted.compareTo(BigDecimal.ZERO) < 0) {
            costAllocationLabel.setText("0%");
            return;
        }

        costAllocationLabel.setText(substracted + "%");
    }

    private void updateHourLabel(AddProfileItemModel profileItemModel) {
        BigDecimal currentHours = profileItemModel.currentHourAllocationProperty().get();
        BigDecimal setHours = profileItemModel.setHourAllocationProperty().get();

        BigDecimal substracted = currentHours.subtract(setHours);
        if (substracted.compareTo(BigDecimal.ZERO) < 0) {
            hourAllocationLabel.setText("0%");
            return;
        }
        hourAllocationLabel.setText(substracted + "%");
    }

    @Override
    public void updateItem(AddProfileItemModel profileItemModel, boolean empty) {
        super.updateItem(profileItemModel, empty);

        if (empty || profileItemModel == null) {
            setGraphic(null);
            return;
        }

        rateSpinner.setValue(profileItemModel.setCostAllocationProperty().get());
        rateSpinner.setMaximum(profileItemModel.currentCostAllocationProperty().get());

        hoursSpinner.setValue(profileItemModel.setHourAllocationProperty().get());
        hoursSpinner.setMaximum(profileItemModel.currentHourAllocationProperty().get());

        updateRateLabel(profileItemModel);
        updateHourLabel(profileItemModel);

        titleLabel.setText(profileItemModel.nameProperty().get());
        locationLabel.setText(profileItemModel.locationProperty().get());

        getCheckBox().setSelected(profileItemModel.selectedProperty().get());

        setGraphic(root);
    }

    private void scrollToListViewCell(ListCell<?> cell) {
        if (cell != null && listView != null) {
            int index = cell.getIndex();
            listView.scrollTo(index);
        }
    }

    public ObservableList<AddProfileItemModel> getSelectedItems() {
        ObservableList<AddProfileItemModel> selectedItems = FXCollections.observableArrayList();
        for (AddProfileItemModel item : getListView().getItems())
            if (item != null && item.selectedProperty().get())
                selectedItems.add(item);
        return selectedItems;
    }
}