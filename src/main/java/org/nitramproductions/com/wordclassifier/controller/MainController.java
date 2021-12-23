package org.nitramproductions.com.wordclassifier.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MainController {

    @FXML
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableColumn<Group, LocalDateTime> leftTableViewDateModifiedColumn;
    @FXML
    private TableView<Expression> rightTableView;
    @FXML
    private TableColumn<Expression, String> rightTableViewNameColumn;
    @FXML
    private TableColumn<Expression, LocalDateTime> rightTableViewDateModifiedColumn;

    @FXML
    private ChoiceBox<String> leftTableViewChoiceBox;
    @FXML
    private ChoiceBox<String> rightTableViewChoiceBox;
    @FXML
    private TextField leftTableViewTextField;
    @FXML
    private TextField rightTableViewTextField;

    @FXML
    private ToggleSwitch toggleSwitch;

    ObservableList<Group> observableGroupList;
    FilteredList<Group> filteredGroupList;
    SortedList<Group> sortedGroupList;

    ObservableList<Expression> observableExpressionList;
    FilteredList<Expression> filteredExpressionList;
    SortedList<Expression> sortedExpressionList;

    public MainController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        leftTableViewChoiceBox.getItems().clear();
        leftTableViewChoiceBox.getItems().addAll("Name");
        leftTableViewChoiceBox.getSelectionModel().select("Name");
        rightTableViewChoiceBox.getItems().clear();
        rightTableViewChoiceBox.getItems().addAll("Name");
        rightTableViewChoiceBox.getSelectionModel().select("Name");

        leftTableViewTextField.setPromptText("Gib hier ein Suchwort ein!");
        rightTableViewTextField.setPromptText("Gib hier ein Suchwort ein!");

        observableGroupList = ConnectionManager.getAllGroups();
        updateGroupLists();
        updateGroupListsIfChange();

        observableExpressionList = ConnectionManager.getAllExpressions();
        updateExpressionLists();
        updateExpressionListsIfChange();

        searchLeftTableView();
        searchRightTableView();
        updateLeftTableViewDependingOnSelectionInRightTableView();
        updateRightTableViewDependingOnSelectionInLeftTableView();
        updateTableViewDependingOnToggleSwitch();

        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        leftTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());
    }

    public void updateGroupLists() {
        filteredGroupList = new FilteredList<>(observableGroupList, group -> true);
        sortedGroupList = new SortedList<>(filteredGroupList);
        sortedGroupList.comparatorProperty().bind(leftTableView.comparatorProperty());
        leftTableView.setItems(sortedGroupList);
    }

    public void updateExpressionLists() {
        filteredExpressionList = new FilteredList<>(observableExpressionList, expression -> true);
        sortedExpressionList = new SortedList<>(filteredExpressionList);
        sortedExpressionList.comparatorProperty().bind(rightTableView.comparatorProperty());
        rightTableView.setItems(sortedExpressionList);
    }

    public void updateGroupListsIfChange() {
        observableGroupList.addListener((ListChangeListener<Group>) change -> {
            if (change.next()) {
                updateGroupLists();
            }
        });
    }

    public void updateExpressionListsIfChange() {
        observableExpressionList.addListener((ListChangeListener<Expression>) change -> {
            if (change.next()) {
                updateExpressionLists();
            }
        });
    }

    public void searchLeftTableView() {
        leftTableViewTextField.textProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if ("Name".equals(leftTableViewChoiceBox.getValue())) {
                filteredGroupList.setPredicate(group -> group.getName().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
        });

        leftTableViewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                leftTableViewTextField.setText("");
            }
        });
    }

    public void searchRightTableView() {
        rightTableViewTextField.textProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if ("Name".equals(rightTableViewChoiceBox.getValue())) {
                filteredExpressionList.setPredicate(expression -> expression.getContent().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
        });

        rightTableViewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rightTableViewTextField.setText("");
            }
        });
    }

    public void updateRightTableViewDependingOnSelectionInLeftTableView() {
        leftTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rightTableView.getSelectionModel().clearSelection();
                if (!toggleSwitch.isSelected()) {
                    try {
                        observableExpressionList.clear();
                        observableExpressionList.addAll(ConnectionManager.getExpressionsFromGroup(newSelection));
                        if ("Name".equals(leftTableViewChoiceBox.getValue())) {
                            filteredExpressionList.setPredicate(expression -> expression.getContent().toLowerCase().contains(rightTableViewTextField.getText().toLowerCase().trim()));
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updateLeftTableViewDependingOnSelectionInRightTableView() {
        rightTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                leftTableView.getSelectionModel().clearSelection();
                if (toggleSwitch.isSelected()) {
                    try {
                        observableGroupList.clear();
                        observableGroupList.addAll(ConnectionManager.getGroupsFromExpression(newSelection));
                        if ("Name".equals(leftTableViewChoiceBox.getValue())) {
                            filteredGroupList.setPredicate(group -> group.getName().toLowerCase().contains(leftTableViewTextField.getText().toLowerCase().trim()));
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updateTableViewDependingOnToggleSwitch() {
        toggleSwitch.selectedProperty().addListener((observableValue, oldSelection, newSelection) -> {
            leftTableViewTextField.setText("");
            rightTableViewTextField.setText("");
            observableGroupList.clear();
            leftTableView.getSelectionModel().clearSelection();
            observableExpressionList.clear();
            rightTableView.getSelectionModel().clearSelection();

            if (!newSelection) {
                try {
                    observableGroupList.addAll(ConnectionManager.getAllGroups());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    observableExpressionList.addAll(ConnectionManager.getAllExpressions());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void onCreateNewMenuItemClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((MenuItem)event.getTarget()).getParentPopup().getOwnerWindow());
        stage.setScene(new Scene(root));
        stage.show();
    }
}
