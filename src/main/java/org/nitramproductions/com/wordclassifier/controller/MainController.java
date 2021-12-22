package org.nitramproductions.com.wordclassifier.controller;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private ToggleSwitch toggleSwitch;

    public MainController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        leftTableView.setItems(ConnectionManager.getAllGroups());
        manageTableViewDependingOnToggleSwitch();
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        leftTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());
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

    public void manageTableViewDependingOnToggleSwitch() {
        toggleSwitch.selectedProperty().addListener((observableValueToggleSwitch, oldSelectionToggleSwitch, newSelectionToggleSwitch) -> {
            leftTableView.getItems().clear();
            leftTableView.getSelectionModel().clearSelection();
            rightTableView.getItems().clear();
            rightTableView.getSelectionModel().clearSelection();
            if (!newSelectionToggleSwitch) {
                try {
                    leftTableView.setItems(ConnectionManager.getAllGroups());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    rightTableView.setItems(ConnectionManager.getAllExpressions());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        leftTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rightTableView.getSelectionModel().clearSelection();
                if (!toggleSwitch.isSelected()) {
                    try {
                        rightTableView.setItems(ConnectionManager.getExpressionsFromGroup(newSelection));
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        rightTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                leftTableView.getSelectionModel().clearSelection();
                if (toggleSwitch.isSelected()) {
                    try {
                        leftTableView.setItems(ConnectionManager.getGroupsFromExpression(newSelection));
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
