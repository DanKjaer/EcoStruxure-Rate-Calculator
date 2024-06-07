package ecostruxure.rate.calculator.gui.widget.tables;

import ecostruxure.rate.calculator.gui.widget.CustomPaginationSkin;
import ecostruxure.rate.calculator.gui.widget.tables.cellfactory.ButtonCellFactory;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.function.Function;

public class CustomTableView<T> {

    private Pagination pagination;
    private int itemsPerPage;
    private ObservableList<T> data = FXCollections.observableArrayList();
    private TableView<T> paginationTableView;

    public CustomTableView() {}

    public CustomTableView(int itemsPerPage, ObservableList<T> initialData) {
        this.itemsPerPage = itemsPerPage;
        this.data = initialData;
    }

    /**
     * Beregner antal sider
     * @param dataSize - størrelse på liste
     * @param itemsPerPage
     * @return int
     */
    private int calculatePageCount(int dataSize, int itemsPerPage) {
        return itemsPerPage != 0 ? (int) Math.ceil((double) dataSize / itemsPerPage) : 0;
    }

    /**
     * Opdaterer max antal sider
     */
    public void updateMaxCount() {
        int maxPage = calculatePageCount(data.size(), itemsPerPage);
        pagination.setPageCount(maxPage);
        pagination.setMaxPageIndicatorCount(maxPage);
    }

    /**
     * Opretter pagination
     * @param tableView
     * @return TableView
     */
    public TableView<T> setupPagination(TableView<T> tableView) {
        this.paginationTableView = tableView;
        paginationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Create the pagination control
        pagination = new Pagination(calculatePageCount(data.size(), itemsPerPage), 0);
        pagination.setPageFactory(this::createPage);
        pagination.setPageCount(calculatePageCount(data.size(), itemsPerPage));

        pagination.setSkin(new CustomPaginationSkin(pagination));

        return paginationTableView;
    }

    /**
     * Opdaterer pagination tabellen og sikrer index ikke bliver for stort.
     * @param pageIndex
     */
    public void updatePaginationTable(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, data.size());

        if (fromIndex >= toIndex) {
            fromIndex = 0;
            toIndex = Math.min(fromIndex + itemsPerPage, data.size());
        }

        if (data.isEmpty()) {
            pagination.setMaxPageIndicatorCount(1);
            pagination.setCurrentPageIndex(0);
            pagination.setPageCount(1);
            paginationTableView.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
            return;
        }

        updateMaxCount();
        paginationTableView.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
    }

    public void refreshTable() {
        paginationTableView.refresh();
    }

    public void setPaginationPages(int page) {
        pagination.setPageCount(page);
    }

    private TableView<T> createPage(int pageIndex) {
        updatePaginationTable(pageIndex);

        return paginationTableView;
    }

    public TableView<T> createTableView() {
        return new TableView<>();
    }

    /**
     * Bind kolonne data til selve kolonne
     * @param column
     * @param title
     * @param value
     */
    private <S> void bindColumnData(TableColumn<T, S> column, StringProperty title, Function<T, ObservableValue<S>> value) {
        column.textProperty().bind(title);
        column.setCellValueFactory(cellData -> value.apply(cellData.getValue()));
    }

    /**
     * Opretter en kolonne
     * @param title
     * @param value
     * @return TableColumn
     */
    public <S> TableColumn<T, S> createColumn(StringProperty title, Function<T, ObservableValue<S>> value) {
        TableColumn<T, S> column = new TableColumn<>();
        bindColumnData(column, title, value);
        return column;
    }

    public void addColumnsToPagination(List<TableColumn<T, ?>> columns) {
        paginationTableView.getColumns().addAll(columns);
    }

    public <S> void addColumnToPagination(TableColumn<T, ?> column) {
        paginationTableView.getColumns().add(column);
    }

    /**
     * Opretter en kolonne med en contextMenu
     * @param tableView - TableView der skal bruges
     * @param contextMenu - Den oprettet ContextMenu
     * @return TableColumn
     */
    public <T> TableColumn<T, Void> createColumnWithMenu(FontIcon fontIcon, TableView<T> tableView, ContextMenu contextMenu) {
        TableColumn<T, Void> column = new TableColumn<>();
        column.setCellFactory(new ButtonCellFactory<>(tableView, contextMenu));
        column.setSortable(false);
        column.getStyleClass().add("allignCenter");

        return column;
    }

    /**
     * Opretter en kolonne med en custom cell factory
     * @param title
     * @param value
     * @param cellFactory
     * @return TableColumn
     */
    public <S> TableColumn<T, S> createColumn(StringProperty title, Function<T, ObservableValue<S>> value, Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory) {
        TableColumn<T, S> column = new TableColumn<>();
        bindColumnData(column, title, value);
        if (cellFactory != null)
            column.setCellFactory(cellFactory);

        return column;
    }

    /**
     * Tilføjer højre klik til hver række i et kabelView, metoder defineres i klasse der bliver kaldt fra
     * Eksempel data:
     * customTableView.addRowRightClick(tableView, Map.of(
     *      "Edit", this::edit,
     *      "Delete", this::delete
     *  ));
     * @param tableView
     * @param contextMenu
     */
    public <T> void addRowRightClick(TableView<T> tableView, ContextMenu contextMenu) {
        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (!row.isEmpty()) {
                        if (contextMenu.isShowing())
                            contextMenu.hide();

                        tableView.getSelectionModel().select(row.getIndex());
                        contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                    } else {
                        contextMenu.hide();
                    }
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
            });

            return row;
        });
    }

    public void addRowDoubleClick(TableView<T> tableView, Runnable action) {
        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    action.run();
                }
            });
            return row;
        });
    }

    public void addRowClickHandler(TableView<T> tableView, ContextMenu rightClick, Runnable doubleClickAction) {
        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (row.getItem() == null) {
                        rightClick.hide();
                        return;
                    }

                    tableView.getSelectionModel().select(row.getIndex());
                    Platform.runLater(() -> {
                        rightClick.show(row, event.getScreenX(), event.getScreenY());
                        rightClick.hide();
                        rightClick.show(row, event.getScreenX(), event.getScreenY());
                    });
                }

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty())
                    doubleClickAction.run();

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (rightClick.isShowing()) rightClick.hide();
//                    else doubleClickAction.run(); // skulle lige teste at man godt kan lave en select handler
                }


            });
            return row;
        });
    }

    public void addRowClickHandler(TableView<T> tableView, ContextMenu rightClick, Runnable singleClickAction, Runnable doubleClickAction) {
        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (rightClick != null && event.getButton() == MouseButton.SECONDARY) {
                    if (row.getItem() == null) {
                        rightClick.hide();
                        return;
                    }

                    tableView.getSelectionModel().select(row.getIndex());
                    rightClick.show(row, event.getScreenX(), event.getScreenY());
                }

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty())
                    doubleClickAction.run();

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (rightClick != null && rightClick.isShowing()) rightClick.hide();
                    else singleClickAction.run();
                }
            });
            return row;
        });
    }


    /**
     * Opretter en custom TableView med de kolonner der er sendt med
     * @param columns
     * @return TableView
     */
    public TableView<T> createCustomTableView(List<TableColumn<T, ?>> columns) {
        TableView<T> tableView = createTableView();
        tableView.getColumns().addAll(columns);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        updatePaginationTable(pagination.getCurrentPageIndex());
    }

    public ObservableList<T> getData() {
        return data;
    }

    public void setData(ObservableList<T> data) {
        this.data = data;
        updatePaginationTable(0);
    }
}
