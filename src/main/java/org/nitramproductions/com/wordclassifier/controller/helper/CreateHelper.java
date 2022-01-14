package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class CreateHelper {

    public CreateHelper() {
    }

    public <T, S> void addListenerToDeselectTableViewIfOtherGetsSelected(TableView<T> tableViewSelected, TableView<S> tableViewToDeselect) {
        tableViewSelected.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends T> change) -> {
            if (!tableViewSelected.getSelectionModel().getSelectedItems().isEmpty()) {
                tableViewToDeselect.getSelectionModel().clearSelection();
            }
        });
    }

    public <T, S> void deselectEitherTableViewIfOtherGetsSelected(TableView<T> firstTableView, TableView<S> secondTableView) {
        addListenerToDeselectTableViewIfOtherGetsSelected(firstTableView, secondTableView);
        addListenerToDeselectTableViewIfOtherGetsSelected(secondTableView, firstTableView);
    }

    public <T> void transferSelectedItemsToAnotherList(TableView<T> tableViewOfSourceList,
                                                       ObservableList<T> sourceList,
                                                       ObservableList<T> destinationList) {
        ObservableList<T> selectedItems = tableViewOfSourceList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            destinationList.addAll(selectedItems);
            sourceList.removeAll(selectedItems);
            tableViewOfSourceList.getSelectionModel().clearSelection();
        }
    }
}
