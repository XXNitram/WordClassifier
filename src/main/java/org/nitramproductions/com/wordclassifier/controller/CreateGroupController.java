package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;

public class CreateGroupController {

    @FXML
    private TextField newNameTextField;

    @FXML
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableView<Group> rightTableView;
    @FXML
    private TableColumn<Group, String> rightTableViewNameColumn;
    @FXML
    private Button rightArrowButton;
    @FXML
    private Button leftArrowButton;

    @FXML
    private Button createNewButton;
    @FXML
    private Button cancelButton;

    private ObservableList<Group> leftList;
    private ObservableList<Group> rightList;

    public CreateGroupController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        leftList = ConnectionManager.getAllGroups();
        leftTableView.setItems(leftList);
        rightTableView.setItems(rightList);

        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }



}
