package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import org.nitramproductions.com.wordclassifier.MainApplication;
import org.nitramproductions.com.wordclassifier.controller.helper.CreateHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.ValidationHelper;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateGroupController {

    @FXML
    private ButtonBar buttonBar;

    @FXML
    private TextField newNameTextField;

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
    private final CreateHelper createHelper = new CreateHelper();
    private final ValidationHelper validationHelper = new ValidationHelper();

    private BooleanProperty needToReloadData;

    public CreateGroupController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        leftList = FXCollections.observableArrayList(connectionManager.getAllExpressions());
        rightList = FXCollections.observableArrayList();
        leftTableView.setItems(leftList);
        rightTableView.setItems(rightList);

        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        cancelButton.translateXProperty().set(-20);
        createNewButton.setDefaultButton(true);
        createNewButton.setOnAction(e -> onCreateNewButtonClick());
        createNewButton.translateXProperty().set(-25);
        TooltipWrapper<Button> createNewWrapper;
        createNewWrapper = validationHelper.getTooltipWrapper(createNewButton, "Wort kann nicht erstellt werden:");
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);

        validateNewNameTextField();
        deselectListIfAnotherIsSelected();

        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
    }

    public void initializeNeedToReloadDataBooleanProperty(BooleanProperty needToReloadData) {
        this.needToReloadData = needToReloadData;
    }

    private void validateNewNameTextField() throws SQLException {
        validationHelper.checkIfEmpty(newNameTextField);
        validationHelper.checkIfIncludesSpecialCharacter(newNameTextField);
        validationHelper.checkIfTooLong(newNameTextField);
        List<Group> groups = connectionManager.getAllGroups();
        validationHelper.checkIfGroupAlreadyExists(newNameTextField, groups);
    }

    private void deselectListIfAnotherIsSelected() {
        createHelper.deselectEitherTableViewIfOtherGetsSelected(leftTableView, rightTableView);
    }

    @FXML
    private void onRightArrowButtonClick() {
        createHelper.transferSelectedItemsToAnotherList(leftTableView, leftList, rightList);
    }

    @FXML
    private void onLeftArrowButtonClick() {
        createHelper.transferSelectedItemsToAnotherList(rightTableView, rightList, leftList);
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

}
