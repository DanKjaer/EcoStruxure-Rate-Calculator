package ecostruxure.rate.calculator.gui.component.currency;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.common.CurrencyItemModel;
import ecostruxure.rate.calculator.gui.common.View;
import ecostruxure.rate.calculator.gui.util.constants.Icons;
import ecostruxure.rate.calculator.gui.util.constants.LayoutConstants;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.CustomContextMenu;
import ecostruxure.rate.calculator.gui.widget.tables.CustomTableView;
import ecostruxure.rate.calculator.gui.widget.tables.contextmenu.MenuItemInfo;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;
import java.util.List;

public class CurrencyView implements View {
    private final CurrencyModel model;

    public CurrencyView(CurrencyModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        var results = new VBox(LayoutConstants.STANDARD_SPACING);
        VBox.setVgrow(results, Priority.ALWAYS);

        var content = new VBox(LayoutConstants.STANDARD_SPACING);
        content.setPadding(new Insets(LayoutConstants.STANDARD_PADDING));
        VBox.setMargin(content, new Insets(LayoutConstants.CONTENT_PADDING));
        VBox.setVgrow(content, Priority.ALWAYS);

        var currencyTable = createCurrencyTable();
        VBox.setVgrow(currencyTable, Priority.ALWAYS);
        content.getChildren().addAll(currencyTable);

        results.getChildren().addAll(content);
        return results;
    }

    private Region createCurrencyTable() {
        CustomTableView<CurrencyItemModel> customTableView = new CustomTableView<>();

        // todo: skal nok være BigDecimal
        TableColumn<CurrencyItemModel, String> currencyColumn = customTableView.createColumn(LocalizedText.CURRENCY, CurrencyItemModel::currencyCodeProperty);
        TableColumn<CurrencyItemModel, String> eurColumn = customTableView.createColumn(LocalizedText.EUR_CONVERSION_RATE, CurrencyItemModel::eurConversionRateProperty);
        TableColumn<CurrencyItemModel, String> usdColumn = customTableView.createColumn(LocalizedText.USD_CONVERSION_RATE, CurrencyItemModel::usdConversionRateProperty);

        TableView<CurrencyItemModel> tableView = customTableView.createCustomTableView(Arrays.asList(currencyColumn, eurColumn, usdColumn));
        tableView.getStyleClass().add(Styles.STRIPED);

        var sortedCurrencies = new SortedList<>(model.currencies());
        sortedCurrencies.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedCurrencies);

        List<MenuItemInfo<CurrencyItemModel>> menuItemInfos = List.of();
        ContextMenu contextMenu = CustomContextMenu.createContextMenu(menuItemInfos, tableView);

        Runnable doubleClickAction = () -> {};

        customTableView.addRowClickHandler(tableView, contextMenu, doubleClickAction);

        //TableColumn<CurrencyItemModel, Void> optionsColumn = customTableView.createColumnWithMenu(new FontIcon(Icons.GEAR), tableView, contextMenu);

       // optionsColumn.setMaxWidth(50);
      //  optionsColumn.setMinWidth(50);
      //  tableView.getColumns().add(optionsColumn);

        return tableView;
    }
}