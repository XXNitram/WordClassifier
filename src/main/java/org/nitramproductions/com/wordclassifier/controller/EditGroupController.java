package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import org.nitramproductions.com.wordclassifier.controller.helper.SearchHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.SelectionHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.TooltipForEllipsizedCells;
import org.nitramproductions.com.wordclassifier.controller.helper.ValidationHelper;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditGroupController {

    @FXML
    private ButtonBar buttonBar;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label creationDateLabel;
    @FXML
    private Label modificationDateLabel;
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
    private FilteredList<Expression> filteredLeftList;
    private ObservableList<Expression> rightList;
    private FilteredList<Expression> filteredRightList;

    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final ValidationHelper validationHelper = new ValidationHelper();
    private final SearchHelper searchHelper = new SearchHelper();

    private Group groupToEdit;
    private List<Expression> expressionsBelongingToGroupList;
    private BooleanProperty needToReloadData;

    public EditGroupController(Group groupToEdit, BooleanProperty needToReloadData) {
        this.groupToEdit = groupToEdit;
        this.needToReloadData = needToReloadData;
    }

    @FXML
    private void initialize() throws SQLException {
        expressionsBelongingToGroupList = connectionManager.getExpressionsBelongingToGroup(groupToEdit);
        initializeLists();
        initializeTableViews();
        initializeTableViewColumns();
        initializeButtons();
        initializeNameField();
        initializeDateLabels();
        searchTableViews();
        validateNewNameTextField();
        deselectListIfAnotherIsSelected();
    }

    private void initializeLists() throws SQLException {
        leftList = FXCollections.observableArrayList(connectionManager.getAllExpressions());
        leftList.removeAll(expressionsBelongingToGroupList);
        filteredLeftList = searchHelper.transformListsAndSetTableView(leftList, leftTableView);
        rightList = FXCollections.observableArrayList(expressionsBelongingToGroupList);
        filteredRightList = searchHelper.transformListsAndSetTableView(rightList, rightTableView);
    }

    private void initializeTableViews() {
        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initializeTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        leftTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
    }

    private void initializeButtons() {
        cancelButton.setCancelButton(true);
        saveButton.setDefaultButton(true);
        cancelButton.translateXProperty().set(-20);
        saveButton.translateXProperty().set(-25);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        saveButton.setOnAction(e -> onCreateNewButtonClick());

        TooltipWrapper<Button> createNewWrapper;
        createNewWrapper = validationHelper.getTooltipWrapper(saveButton, "Gruppe kann nicht gespeichert werden:");
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);
    }

    private void initializeNameField() {
        nameTextField.setText(groupToEdit.getName());
        Platform.runLater(() -> nameTextField.getParent().requestFocus());
    }

    private void initializeDateLabels() {
        modificationDateLabel.setText(groupToEdit.getFormattedDateModified());
    }

    private void searchTableViews() {
        searchHelper.searchFilteredExpressionListForNameOnly(leftTableViewSearchTextField, filteredLeftList);
        searchHelper.searchFilteredExpressionListForNameOnly(rightTableViewSearchTextField, filteredRightList);
    }

    private void validateNewNameTextField() throws SQLException {
        validationHelper.checkIfEmpty(nameTextField);
        validationHelper.checkIfIncludesSpecialCharacter(nameTextField);
        validationHelper.checkIfTooLong(nameTextField);
        List<Group> groups = connectionManager.getAllGroups();
        groups.remove(groupToEdit);
        validationHelper.checkIfGroupAlreadyExists(nameTextField, groups);
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
        String newGroupName = nameTextField.getText().trim();
        List<Expression> expressionsBelongingToGroupListCopy = new ArrayList<>(expressionsBelongingToGroupList);
        expressionsBelongingToGroupList.removeAll(rightList);
        rightList.removeAll(expressionsBelongingToGroupListCopy);
        if (!rightList.isEmpty()) {
            for (Expression expression : rightList) {
                try {
                    connectionManager.addNewBelongToRelation(groupToEdit, expression);
                    connectionManager.updateExpressionModificationDate(expression);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!expressionsBelongingToGroupList.isEmpty()) {
            for (Expression expression : expressionsBelongingToGroupList) {
                try {
                    connectionManager.deleteBelongToRelation(groupToEdit, expression);
                    connectionManager.updateExpressionModificationDate(expression);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        needToReloadData.set(true);
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
