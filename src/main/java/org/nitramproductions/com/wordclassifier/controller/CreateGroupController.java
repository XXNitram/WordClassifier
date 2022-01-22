package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import org.nitramproductions.com.wordclassifier.controller.helper.SelectionHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.TooltipForEllipsizedCells;
import org.nitramproductions.com.wordclassifier.controller.helper.ValidationHelper;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;
import java.util.List;

public class CreateGroupController {

    @FXML
    private ButtonBar buttonBar;
    @FXML
    private TextField newNameTextField;
    @FXML
    private TextField leftTableViewSearchTextField;
    @FXML
    private TextField rightTableViewSearchTextField;
    @FXML
    private TableView<Expression> leftTableView;
    @FXML
    private TableColumn<Expression, String> leftTableViewNameColumn;
    @FXML
    private TableView<Expression> rightTableView;
    @FXML
    private TableColumn<Expression, String> rightTableViewNameColumn;

    private ObservableList<Expression> leftList;
    private ObservableList<Expression> rightList;

    private final Button createNewButton = new Button("Erstellen");
    private final Button cancelButton = new Button("Abbrechen");

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final ValidationHelper validationHelper = new ValidationHelper();

    private BooleanProperty needToReloadData;

    public CreateGroupController() {

    }

    @FXML
    private void initialize() throws SQLException {
        initializeLists();
        initializeTableViews();
        initializeButtons();
        validateNewNameTextField();
        deselectListIfAnotherIsSelected();
    }

    private void initializeLists() throws SQLException {
        leftList = FXCollections.observableArrayList(connectionManager.getAllExpressions());
        rightList = FXCollections.observableArrayList();
    }

    private void initializeTableViews() {
        leftTableView.setItems(leftList);
        rightTableView.setItems(rightList);
        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initializeTableViewColumns();
    }

    private void initializeTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        leftTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
    }

    private void initializeButtons() {
        cancelButton.setCancelButton(true);
        createNewButton.setDefaultButton(true);
        cancelButton.translateXProperty().set(-20);
        createNewButton.translateXProperty().set(-25);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        createNewButton.setOnAction(e -> onCreateNewButtonClick());

        TooltipWrapper<Button> createNewWrapper;
        createNewWrapper = validationHelper.getTooltipWrapper(createNewButton, "Gruppe kann nicht erstellt werden:");
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);
    }

    private void validateNewNameTextField() throws SQLException {
        validationHelper.checkIfEmpty(newNameTextField);
        validationHelper.checkIfIncludesSpecialCharacter(newNameTextField);
        validationHelper.checkIfTooLong(newNameTextField);
        List<Group> groups = connectionManager.getAllGroups();
        validationHelper.checkIfGroupAlreadyExists(newNameTextField, groups);
    }

    private void deselectListIfAnotherIsSelected() {
        selectionHelper.deselectEitherTableViewIfOtherGetsSelected(leftTableView, rightTableView);
    }

    @FXML
    private void onRightArrowButtonClick() {
        selectionHelper.transferSelectedItemsToAnotherList(leftTableView, leftList, rightList);
    }

    @FXML
    private void onLeftArrowButtonClick() {
        selectionHelper.transferSelectedItemsToAnotherList(rightTableView, rightList, leftList);
    }

    private void onCreateNewButtonClick() {
        String newGroupName = newNameTextField.getText().trim();
        Group newGroup = new Group(newGroupName);
        try {
            connectionManager.addNewGroup(newGroup);
            if (!rightList.isEmpty()) {
                for (Expression expression : rightList) {
                    connectionManager.addNewBelongToRelation(newGroup, expression);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        needToReloadData.set(true);
        Stage stage = (Stage) createNewButton.getScene().getWindow();
        stage.close();
    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void initializeNeedToReloadDataBooleanProperty(BooleanProperty needToReloadData) {
        this.needToReloadData = needToReloadData;
    }
}
