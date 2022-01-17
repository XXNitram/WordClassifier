package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

public class SearchHelper {

    public SearchHelper() {

    }

    public <T> FilteredList<T> transformListsAndSetTableView(ObservableList<T> observableList,
                                                  TableView<T> tableView) {
        FilteredList<T> filteredList = new FilteredList<>(observableList, T -> true);
        SortedList<T> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        return filteredList;
    }

    public void searchFilteredExpressionListDependingOnChoiceBox(TextField textField,
                                                                 ChoiceBox<String> choiceBox,
                                                                 FilteredList<Expression> filteredExpressionList) {
        textField.textProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if ("Name".equals(choiceBox.getValue())) {
                filteredExpressionList.setPredicate(expression -> expression.getContent().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
            if ("Änderungsdatum".equals(choiceBox.getValue())) {
                filteredExpressionList.setPredicate(expression -> expression.getFormattedDateModified().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
        });
    }

    public void searchFilteredGroupListDependingOnChoiceBox(TextField textField,
                                                            ChoiceBox<String> choiceBox,
                                                            FilteredList<Group> filteredGroupList) {
        textField.textProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if ("Name".equals(choiceBox.getValue())) {
                filteredGroupList.setPredicate(group -> group.getName().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
            if ("Änderungsdatum".equals(choiceBox.getValue())) {
                filteredGroupList.setPredicate(group -> group.getFormattedDateModified().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
        });
    }

    public void clearTextFieldIfChoiceBoxChanged(ChoiceBox<String> choiceBox, TextField textField) {
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                textField.setText("");
            }
        });
    }
}
